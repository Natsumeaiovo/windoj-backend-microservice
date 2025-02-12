package com.serein.windojjudgeservice.judge.codesandbox;

import com.serein.windojmodel.model.codesandbox.ExecuteCodeRequest;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: serein
 * @date: 2024/9/5 19:04
 * @description: 代码沙箱代理类，增强代码沙箱
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    /**
    * @Description: 调用代码沙箱执行代码，并输出执行前的请求信息 和 执行后的相应信息 的日志
    * @Param: [executeCodeRequest]
    * @return: com.serein.windoj.judge.codesandbox.model.ExecuteCodeResponse
    * @Author: serein
    * @Date: 19:07 2024/9/5
    */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱相应信息" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
