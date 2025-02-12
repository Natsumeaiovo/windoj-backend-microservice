package com.serein.windojjudgeservice.controller.inner;

import com.serein.windojjudgeservice.judge.JudgeService;
import com.serein.windojmodel.model.entity.QuestionSubmit;
import com.serein.windojserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: serein
 * @date: 2025/2/12 15:06
 * @description: 微服务相互调用的方法，仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    private JudgeService judgeService;

    /**
     * @Description: 判题
     * @Param: [questionSubmitId]
     * @return: com.serein.windoj.model.vo.QuestionSubmitVO
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}
