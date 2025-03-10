package com.serein.windojcommon.common;

import lombok.Data;
import com.serein.windojcommon.constant.CommonConstant;

/**
 * 分页请求
 *
 * @author serein
 * @from <a href="https://natsumeaiovo.github.io">serein's blog</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
