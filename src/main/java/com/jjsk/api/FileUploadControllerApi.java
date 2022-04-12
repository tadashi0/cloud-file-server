package com.jjsk.api;

import com.jjsk.common.domain.ApiResult;
import com.jjsk.entity.bo.UploadPartBo;
import com.jjsk.entity.vo.CheckPartVo;
import com.jjsk.entity.vo.FileUploadVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Tadashi
 * @descroption
 * @date 2021/1/22 10:44
 */
@Api(tags = "GridFS存储服务接口")
public interface FileUploadControllerApi {

    @ApiOperation("上传文件")
    public ApiResult upload(MultipartFile file);

    @ApiOperation("批量上传文件")
    public ApiResult batchUpload(MultipartFile[] file);

    @ApiOperation("文件预览")
    public void preview(String id, boolean inline, HttpServletResponse response);

    @ApiOperation("文件删除")
    public ApiResult delete(String id);

    @ApiOperation("批量删除")
    public ApiResult batchDelete(List<String> ids);

    @ApiOperation("获取文件二进制内容")
    public ApiResult getFileById(String id);

    @ApiOperation("获取文件信息")
    public ApiResult getFileInfoById(String id);

    @ApiOperation("重命名文件")
    public ApiResult rename(String id, String name);

    @ApiOperation("分片上传")
    public ApiResult<FileUploadVo> uploadPart(UploadPartBo file);

    @ApiOperation("检查分片断点")
    public ApiResult<CheckPartVo> checkFileMd5(String md5, String name, Integer chunkTotal);
}
