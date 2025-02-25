package com.serein.windojjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.serein.windojcommon.common.ErrorCode;
import com.serein.windojcommon.exception.BusinessException;
import com.serein.windojjudgeservice.judge.codesandbox.CodeSandbox;
import com.serein.windojjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.serein.windojjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.serein.windojjudgeservice.judge.strategy.JudgeContext;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeRequest;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeResponse;
import com.serein.windojmodel.model.codesandbox.JudgeInfo;
import com.serein.windojmodel.model.dto.question.JudgeCase;
import com.serein.windojmodel.model.entity.Question;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import com.serein.windojmodel.model.enums.QuestionSubmitStatusEnum;
import com.serein.windojserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: serein
 * @date: 2024/9/5 19:46
 * @description: 判题服务实现类
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 根据传入的题目的提交 id，从数据库获取到对应的题目提交信息（包括代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2. 如果题目提交信息不为 待判题 状态，那么结束执行
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中，或已经判题结束");
        }
        // 3. 更改判题（题目提交）的状态为 “判题中”，防止代码沙箱重复执行，也能让用户即时看到状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean isUpdated = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!isUpdated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        // 4. 调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .language(language)
                .code(code)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = null;  // 拿到代码沙箱执行结果
        try {
            executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            System.out.println("代码沙箱返回结果：" + executeCodeResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 设置题目提交状态为失败
            log.error("代码提交失败！");
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        }
        JudgeInfo exeJudgeInfo = executeCodeResponse.getJudgeInfo();
        List<String> outputList = executeCodeResponse.getOutputList();

        // 5. 根据沙箱的执行结果，使用不同的判题策略 来 设置题目的判题状态和信息
        JudgeContext judgeContext = JudgeContext.builder()
                .question(question)
                .questionSubmit(questionSubmit)
                .exeJudgeInfo(exeJudgeInfo)
                .inputList(inputList)
                .outputList(outputList)
                .judgeCaseList(judgeCaseList)
                .build();
        // 根据题目提交信息中的 语言 来更改判题策略
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6. 修改数据库中的判题结果
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        isUpdated = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!isUpdated) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}
