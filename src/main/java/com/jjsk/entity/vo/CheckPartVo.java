package com.jjsk.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author chonghui.tian
 * @descroption
 * @date 2021/5/20 17:48
 */
@Data
@Builder
@ApiModel("Check-Vo")
public class CheckPartVo{
    @ApiModelProperty("分块索引")
    private Integer chunkIndex;

    @ApiModelProperty("文件Id")
    private String fileId;

    @ApiModelProperty("状态: 0=从断点开始传, 1=秒传, 2=从新上传")
    private Integer status;

    @ApiModelProperty("元数据信息")
    private FileUploadVo fileInfo;
}
