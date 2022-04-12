package com.jjsk.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * file - vo
 */
@Data
@Builder
@ApiModel("File-Vo")
public class FileUploadVo {
    
    @ApiModelProperty("文件Id")
    private String fileId;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件大小")
    private Long size;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty("文件后缀")
    private String suffix;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;

    @ApiModelProperty("预览地址")
    private String previewUrl;


}
