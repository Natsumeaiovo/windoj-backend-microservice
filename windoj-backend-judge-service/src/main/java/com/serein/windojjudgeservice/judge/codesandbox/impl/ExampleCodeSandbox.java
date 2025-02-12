package com.serein.windojjudgeservice.judge.codesandbox.impl;

import com.serein.windojjudgeservice.judge.codesandbox.CodeSandbox;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeRequest;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeResponse;
import com.serein.windojmodel.model.codesandbox.JudgeInfo;
import com.serein.windojmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * @author: serein
 * @date: 2024/9/5 11:350
 * @description: 示例代码沙箱(用于测试业务流程)
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("示例代码沙箱");
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
