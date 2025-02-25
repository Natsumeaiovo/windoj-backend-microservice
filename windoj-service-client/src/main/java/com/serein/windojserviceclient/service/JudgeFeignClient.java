package com.serein.windojserviceclient.service;

import com.serein.windojmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: serein
 * @date: 2024/9/5 19:41
 * @description: 判题服务接口
 */
@FeignClient(name = "windoj-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    
    /** 
    * @Description: 判题
    * @Param: [questionSubmitId] 
    * @return: com.serein.windoj.model.vo.QuestionSubmitVO
    * @Author: serein
    * @Date: 19:46 2024/9/5 
    */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
