package com.jjsk.controller;

import com.jjsk.api.FileUploadControllerApi;
import com.jjsk.common.domain.ApiResult;
import com.jjsk.entity.bo.UploadPartBo;
import com.jjsk.entity.vo.CheckPartVo;
import com.jjsk.entity.vo.FileUploadVo;
import com.jjsk.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Tadashi
 * @descroption
 * @date 2021/1/22 10:47
 */
@Slf4j
@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileUploadController implements FileUploadControllerApi {

    private final FileUploadService service;

    @Override
    @PostMapping("upload")
    public ApiResult upload(@RequestParam MultipartFile file) {
        return ApiResult.ok(service.upload(file));
    }

    @Override
    @PostMapping("batch/upload")
    public ApiResult batchUpload(@RequestParam MultipartFile[] file) {
        return ApiResult.ok(service.batchUpload(file));
    }

    @Override
    @GetMapping("preview/{id}.*")
    public void preview(@PathVariable String id, @RequestParam(required = false,defaultValue = "false") boolean inline, HttpServletResponse response) {
        service.preview(id, inline, response);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResult delete(@PathVariable String id) {
        return service.delete(id);
    }

    @Override
    @DeleteMapping("batchDelete")
    public ApiResult batchDelete(@RequestParam List<String> ids) {
        return service.batchDelete(ids);
    }

    @Override
    @GetMapping("get/{id}")
    public ApiResult getFileById(@PathVariable String id) {
        return service.getFileById(id);
    }

    @Override
    @GetMapping("getFileInfo/{id}")
    public ApiResult getFileInfoById(@PathVariable String id) {
        return service.getFileInfo(id);
    }

    @Override
    @PutMapping("rename/{id}/{name}")
    public ApiResult rename(@PathVariable String id, @PathVariable String name) {
        return service.rename(id, name);
    }

    @Override
    @PostMapping("uploadPart")
    public ApiResult<FileUploadVo> uploadPart(UploadPartBo file) {
        FileUploadVo vo = service.uploadPart(file);
        log.info("  uploadPart {}", vo);
        return ApiResult.ok(vo);
    }

    @Override
    @GetMapping("checkFileMd5/{md5}/{name}/{chunkTotal}")
    public ApiResult<CheckPartVo> checkFileMd5(@PathVariable String md5, @PathVariable String name, @PathVariable Integer chunkTotal) {
        return service.checkFileMd5(md5, name, chunkTotal);
    }
}
