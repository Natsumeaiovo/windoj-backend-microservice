package com.serein.windojjudgeservice.judge.strategy;

import com.serein.windojmodel.model.codesandbox.JudgeInfo;
import com.serein.windojmodel.model.dto.question.JudgeCase;
import com.serein.windojmodel.model.entity.Question;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: serein
 * @date: 2024/9/5 21:00
 * @description: 判题上下文，用于定义在策略中传递的参数
 */
@Data
@Builder
public class JudgeContext {

    /**
     * 原题信息
     */
    private Question question;

    /**
     * 题目提交信息
     */
    private QuestionSubmit questionSubmit;

    /**
     * 通过代码沙箱返回的判题信息
     */
    private JudgeInfo exeJudgeInfo;

    /**
     * 原题的输入用例列表
     */
    private List<String> inputList;

    /**
     * 输入用例通过代码沙箱执行后返回的输出列表
     */
    private List<String> outputList;

    /**
     * 原题中设置的判题用例，包括输入用例，和【预期输出用例】
     */
    List<JudgeCase> judgeCaseList;

}
