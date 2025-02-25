package com.serein.windojquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.windojcommon.common.ErrorCode;
import com.serein.windojcommon.constant.CommonConstant;
import com.serein.windojcommon.exception.BusinessException;
import com.serein.windojcommon.utils.SqlUtils;
import com.serein.windojmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.serein.windojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.serein.windojmodel.model.entity.Question;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import com.serein.windojmodel.model.entity.User;
import com.serein.windojmodel.model.enums.QuestionSubmitLanguageEnum;
import com.serein.windojmodel.model.enums.QuestionSubmitStatusEnum;
import com.serein.windojmodel.model.vo.QuestionSubmitVO;
import com.serein.windojmodel.model.vo.UserVO;
import com.serein.windojquestionservice.mapper.QuestionSubmitMapper;
import com.serein.windojquestionservice.rabbitmq.MyMessageProducer;
import com.serein.windojquestionservice.service.QuestionService;
import com.serein.windojquestionservice.service.QuestionSubmitService;
import com.serein.windojserviceclient.service.JudgeFeignClient;
import com.serein.windojserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Kusanagi
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-08-08 00:10:40
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交请求信息
     * @param loginUser                登录用户
     * @return 提交记录的 id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());  // 暂且设置为 待判题
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 执行判题服务
        Long questionSubmitId = questionSubmit.getId();
//        CompletableFuture.runAsync(() -> judgeFeignClient.doJudge(questionSubmitId));
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        return questionSubmitId;
    }

    /**
     * 获取查询包装类（根据前端用户传来的请求对象（根据哪些字段查询），得到 mp 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目提交信息封装类（脱敏）
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏策略：提交的代码 只有本人和管理员能看见。
        Long loginUserId = loginUser.getId();    // 拿到登录用户的 userId
        Long submitUserId = questionSubmit.getUserId();
        // 脱敏，如果登录用户的 id 与题目提交者 id 不同，并且登录用户又不是管理员，那么拒绝访问代码
        if (!Objects.equals(loginUserId, submitUserId) && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        // 由 提交用户id 转为 UserVO，这里直接调用数据库，下面page中则是在 HashMap 中取
        User submitUser = userFeignClient.getById(submitUserId);
        UserVO submitUserVO = userFeignClient.getUserVO(submitUser);
        questionSubmitVO.setUserVO(submitUserVO);
        return questionSubmitVO;
    }

    /**
     * 分页获取题目提交信息的封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        // 获取到原问题提交分页的所有 submit 形成一个列表
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        // 关联查询此页 submit 所含的所有用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));   // 由于用户 id 是唯一的，所以每个 userId 对应的 List<User> 只有一个元素

        // 填充信息，将原 questionSubmit 分页的列表转换为 questionSubmitVO 列表
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            // 脱敏策略：提交的代码 只有本人和管理员能看见
            Long loginUserId = loginUser.getId();    // 拿到登录用户的 userId
            Long submitUserId = questionSubmit.getUserId();
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            if (!Objects.equals(loginUserId, submitUserId) && !userFeignClient.isAdmin(loginUser)) {
                questionSubmitVO.setCode(null);
            }
            User user = null;
            // 这里从 map 中去取，优化性能
            if (userIdUserListMap.containsKey(submitUserId)) {
                user = userIdUserListMap.get(submitUserId).get(0);
            }
            questionSubmitVO.setUserVO(userFeignClient.getUserVO(user));  // 将 user 转换为 userVO
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
        // todo QuestionSubmitVO 还有一条 QuestionVO 属性没有封装进去
    }
}




