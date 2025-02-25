package com.serein.windojjudgeservice.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.serein.windojjudgeservice.judge.strategy.JudgeContext;
import com.serein.windojjudgeservice.judge.strategy.JudgeStrategy;
import com.serein.windojmodel.model.codesandbox.JudgeInfo;
import com.serein.windojmodel.model.dto.question.JudgeCase;
import com.serein.windojmodel.model.dto.question.JudgeConfig;
import com.serein.windojmodel.model.entity.Question;
import com.serein.windojmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * @author: serein
 * @date: 2024/9/5 21:01
 * @description: 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {

    /**
    * @Description: 默认执行判题
    * @Param: [judgeContext]
    * @return: com.serein.windoj.model.dto.questionsubmit.JudgeInfo
    * @Author: serein
    * @Date: 21:23 2024/9/5
    */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        // 沙箱执行代码并响应返回 的 判题信息
        JudgeInfo exeJudgeInfo = judgeContext.getExeJudgeInfo();
        Long memory = exeJudgeInfo.getMemory();
        Long time = exeJudgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> exeOutputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        // 判断沙箱 执行的结果输出数量 是否和 预期输出数量 相等
        if (exeOutputList.size() != inputList.size()) {
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
            return judgeInfoResponse;
        }

        // 依次判断 每一项输出 和 预期输出 是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(exeOutputList.get(i))) {
                judgeInfoResponse.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                return judgeInfoResponse;
            }
        }

        // 判断原题目的 运行时间、内存使用等 是否符合 判题限制要求
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        if (memory > memoryLimit) {
            // 如果沙箱中执行程序的内存超过了原题的内存限制，那么
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }
        if (time > timeLimit) {
            judgeInfoResponse.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return judgeInfoResponse;
        }

        judgeInfoResponse.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        return judgeInfoResponse;
    }
}
