package com.jjsk.common.domain;

/**
 * 基础常量
 */
public interface BaseConstants {

    /**
     * 小文件最大大小
     */
    Integer DEFAULT_CHUNK_SIZE_BYTES = 255 * 1024;

    /**
     * 文件元数据
     */
    String FILE_METADATA_CONTENT_TYPE = "_contentType";
    String FILE_METADATA_SUFFIX = "_suffix";
}
