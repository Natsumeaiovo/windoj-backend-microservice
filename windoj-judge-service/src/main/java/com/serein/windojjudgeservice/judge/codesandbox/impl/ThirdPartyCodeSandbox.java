package com.serein.windojjudgeservice.judge.codesandbox.impl;

import com.serein.windojjudgeservice.judge.codesandbox.CodeSandbox;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeRequest;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * @author: serein
 * @date: 2024/9/5 11:35
 * @description: 第三方代码沙箱（调用现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
