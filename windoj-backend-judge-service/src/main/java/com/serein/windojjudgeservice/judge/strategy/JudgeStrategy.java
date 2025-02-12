package com.serein.windojjudgeservice.judge.strategy;

import com.serein.windojmodel.model.codesandbox.JudgeInfo;

/**
 * @author: serein
 * @date: 2024/9/5 20:58
 * @description: 判题策略接口
 */
public interface JudgeStrategy {

    /**
    * @Description: 执行判题
    * @Param: [judgeContext]
    * @return: com.serein.windoj.model.dto.questionsubmit.JudgeInfo
    * @Author: serein
    * @Date: 21:01 2024/9/5
    */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
