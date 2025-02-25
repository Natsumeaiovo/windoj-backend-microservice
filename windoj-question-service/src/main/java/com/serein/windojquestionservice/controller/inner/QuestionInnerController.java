package com.serein.windojquestionservice.controller.inner;

import com.serein.windojmodel.model.entity.Question;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import com.serein.windojquestionservice.service.QuestionService;
import com.serein.windojquestionservice.service.QuestionSubmitService;
import com.serein.windojserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: serein
 * @date: 2025/2/12 14:54
 * @description: 微服务相互调用的方法，仅内部调用，不是给前端的
 */

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return  questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

}
