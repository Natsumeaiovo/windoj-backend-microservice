package com.serein.windojjudgeservice.judge;

import com.serein.windojmodel.model.entity.QuestionSubmit;

/**
 * @author: serein
 * @date: 2024/9/5 19:41
 * @description: 判题服务接口
 */
public interface JudgeService {
    
    /** 
    * @Description: 判题
    * @Param: [questionSubmitId] 
    * @return: com.serein.windoj.model.vo.QuestionSubmitVO
    * @Author: serein
    * @Date: 19:46 2024/9/5 
    */ 
    QuestionSubmit doJudge(long questionSubmitId);
}
