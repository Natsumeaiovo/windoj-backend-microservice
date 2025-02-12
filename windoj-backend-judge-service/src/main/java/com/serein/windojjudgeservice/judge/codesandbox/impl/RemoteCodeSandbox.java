package com.serein.windojjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.serein.windojcommon.common.ErrorCode;
import com.serein.windojcommon.exception.BusinessException;
import com.serein.windojjudgeservice.judge.codesandbox.CodeSandbox;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeRequest;
import com.serein.windojmodel.model.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: serein
 * @date: 2024/9/5 11:35
 * @description: 远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {

    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";   // 使用本地代码沙箱
//        String url = "http://192.168.159.132:8090/executeCode";   // 远程代码沙箱
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String httpResponseBody = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(httpResponseBody)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + httpResponseBody);
        }

        return JSONUtil.toBean(httpResponseBody, ExecuteCodeResponse.class);
    }
}
