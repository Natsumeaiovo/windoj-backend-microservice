package com.serein.windojserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.serein.windojmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.serein.windojmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import com.serein.windojmodel.model.entity.User;
import com.serein.windojmodel.model.vo.QuestionSubmitVO;


/**
* @author Kusanagi
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-08-08 00:10:40
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     * @param questionSubmitAddRequest 题目提交请求信息
     * @param loginUser 登录用户
     * @return 提交记录的 id
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取 mp 对应的查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQuerySubmitWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目提交信息封装类（脱敏）
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交信息的封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
