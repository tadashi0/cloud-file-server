package com.jjsk.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;

/**
 * 分页入参
 */
@Data
@ApiModel("分页入参")
public class PageBo {
    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example = "1")
    protected Long current = 1L;
    /**
     * 每页显示条数，默认 10
     */
    @ApiModelProperty(value = "页码大小", required = true, example = "10")
    protected Long size = 10L;

    @ApiModelProperty("排序 key:字段名，value:DESC/ASC ")
    protected HashMap<String, String> sorter;
}