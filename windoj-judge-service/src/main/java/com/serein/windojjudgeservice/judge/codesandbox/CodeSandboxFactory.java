package com.serein.windojjudgeservice.judge.codesandbox;

import com.serein.windojjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.serein.windojjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.serein.windojjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * @author: serein
 * @date: 2024/9/5 11:56
 * @description: 代码沙箱工厂类（静态工厂），根据字符串参数创建指定的代码沙箱实例
 */
public class CodeSandboxFactory {

    /**
    * @Description: 创建代码沙箱实例
    * @Param: [type] 沙箱类型
    * @return: com.serein.windoj.judge.codesandbox.CodeSandbox
    * @Author: serein
    * @Date: 11:58 2024/9/5
    */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
