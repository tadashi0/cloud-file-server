package com.jjsk.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页响应")
public class PageVo<T> implements Serializable {

    @ApiModelProperty("当前页")
    private Long current;

    @ApiModelProperty("页码大小")
    private Long size;

    @ApiModelProperty("总页数")
    private Long totalPage;

    @ApiModelProperty("总数")
    private Long total;

    @ApiModelProperty("结果集合")
    private List<T> list;

    public PageVo(Long current, Long size, Long total, List<T> list) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.list = list;
    }

    public PageVo(Long current, Long size, Long total) {
        this.current = current;
        this.size = size;
        this.total = total;
    }

    public Long getTotalPage() {
        return total % size == 0 ? total / size : total / size + 1;
    }

}
