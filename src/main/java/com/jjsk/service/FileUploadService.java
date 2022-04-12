package com.jjsk.service;

import com.alibaba.fastjson.JSON;
import com.jjsk.common.domain.ApiResult;
import com.jjsk.common.domain.BaseConstants;
import com.jjsk.common.domain.ResultMsg;
import com.jjsk.entity.bo.UploadPartBo;
import com.jjsk.entity.vo.CheckPartVo;
import com.jjsk.entity.vo.FileUploadVo;
import com.jjsk.exception.BusinessException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author Tadashi
 * @descroption
 * @date 2021/1/22 10:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final GridFsTemplate gridFsTemplate;
    private final MongoClient mongoClient;
    private final GridFSBucket gridFSBucket;
    @Value("${base.fs.base-preview-url}")
    private String previewUrl;

    @Value("${base.fs.download-file-path}")
    private String downloadTempPath;// = "D:/work";

    @Value("${spring.data.mongodb.database}")
    String database;

    public FileUploadVo upload(MultipartFile file) {
        try {
            String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse(file.getName());
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("md5").is(DigestUtils.md5Hex(file.getInputStream())).and("filename").is(fileName)));
            if (Objects.nonNull(gridFSFile)) {
                return FileUploadVo.builder()
                        .fileId(gridFSFile.getObjectId().toHexString())
                        .name(fileName)
                        .size(gridFSFile.getLength())
                        .contentType(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                        .suffix(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .gmtCreated(LocalDateTime.ofInstant(gridFSFile.getUploadDate().toInstant(), ZoneId.systemDefault()))
                        .previewUrl(previewUrl + "/file/preview/" + gridFSFile.getObjectId().toHexString() + gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .build();
            } else {
                Document metadata = new Document()
                        .append(BaseConstants.FILE_METADATA_CONTENT_TYPE, file.getContentType())
                        .append(BaseConstants.FILE_METADATA_SUFFIX, fileName.substring(fileName.lastIndexOf(".")));
                log.info("name: {}, size: {}", fileName, file.getSize());
                ObjectId objectId = gridFsTemplate.store(file.getInputStream(), fileName, metadata);
                log.info("upload success {}, objectId: {}", fileName, objectId.toHexString());
                return FileUploadVo.builder()
                        .fileId(objectId.toHexString())
                        .name(fileName)
                        .size(file.getSize())
                        .contentType(metadata.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                        .suffix(metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .gmtCreated(LocalDateTime.now())
                        .previewUrl(previewUrl + "/file/preview/" + objectId.toHexString() + metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .build();
            }
        } catch (Exception e) {
            log.info("upload file exception <<<=== ", e);
            throw new BusinessException("File upload failed！Error: " + e.getMessage());
        }
    }

    public List<FileUploadVo> batchUpload(MultipartFile[] file) {
        return Stream.of(file).map(this::upload).collect(Collectors.toList());
    }

    public void preview(String id, boolean inline, HttpServletResponse response) {
        try (OutputStream out = response.getOutputStream()) {
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
            if (null != gridFSFile) {
                Document document = Optional.ofNullable(gridFSFile.getMetadata()).orElse(new Document());
                response.setCharacterEncoding("UTF-8");
                if (inline) {
                    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + URLEncoder.encode(gridFSFile.getFilename(), String.valueOf(StandardCharsets.UTF_8)));
                } else {
                    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + URLEncoder.encode(gridFSFile.getFilename(), String.valueOf(StandardCharsets.UTF_8)));

                }
                response.addHeader(HttpHeaders.CONTENT_TYPE, Optional.ofNullable(document.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE)).orElse("image/jpeg"));
                response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(gridFSFile.getLength()));
                gridFSBucket.downloadToStream(gridFSFile.getObjectId(), out);
            } else {
                out.write(JSON.toJSONString(ApiResult.error("file does not exist!")).getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error("file preview exception <<<=== ", e);
            throw new BusinessException("File preview failed！Error: " + e.getMessage());
        }
    }

    public ApiResult delete(String id) {
        try {
            gridFsTemplate.delete(new Query().addCriteria(Criteria.where("_id").is(id)));
            return ApiResult.ok();
        } catch (Exception e) {
            log.debug("删除文件失败: fileId = {}", id);
            throw BusinessException.of(ResultMsg.DATA_NOT_FOUND);
        }
    }

    public ApiResult batchDelete(List<String> ids) {
        return ApiResult.ok(ids.stream().map(this::delete).collect(Collectors.toList()));
    }

    public ApiResult getFileById(String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (null == gridFSFile) {
            return ApiResult.ok(ResultMsg.DATA_NOT_FOUND.getMsg());
        }
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        try {
            byte[] bytes = IOUtils.toByteArray(gridFsResource.getInputStream());
            return ApiResult.ok(new String(bytes, "ISO-8859-1"));
        } catch (IOException e) {
            log.error("file fetch exception <<<=== ", e);
            throw new BusinessException("File fetch failed！Error: " + e.getMessage());
        }
    }

    public ApiResult getFileInfo(String id) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (null == gridFSFile) {
            return ApiResult.ok(ResultMsg.DATA_NOT_FOUND.getMsg());
        }
        return ApiResult.ok(FileUploadVo.builder()
                .fileId(gridFSFile.getObjectId().toHexString())
                .name(gridFSFile.getFilename())
                .size(Long.valueOf(gridFSFile.getChunkSize()))
                .contentType(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                .suffix(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                .gmtCreated(LocalDateTime.ofInstant(gridFSFile.getUploadDate().toInstant(), ZoneId.systemDefault()))
                .previewUrl(previewUrl + "/file/preview/" + gridFSFile.getObjectId().toHexString() + gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                .build());
    }

    public ApiResult rename(String id, String name) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (null == gridFSFile) {
            return ApiResult.ok(ResultMsg.DATA_NOT_FOUND.getMsg());
        }
        gridFSBucket.rename(new ObjectId(id), name);
        return ApiResult.ok(FileUploadVo.builder()
                .fileId(gridFSFile.getObjectId().toHexString())
                .name(name)
                .size(Long.valueOf(gridFSFile.getChunkSize()))
                .contentType(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                .suffix(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                .gmtCreated(LocalDateTime.ofInstant(gridFSFile.getUploadDate().toInstant(), ZoneId.systemDefault()))
                .previewUrl(previewUrl + "/file/preview/" + gridFSFile.getObjectId().toHexString() + gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                .build());
    }

    public FileUploadVo uploadPart(UploadPartBo part) {
        log.info("uploadPart ===>>> part: {}, chunkSize: {}, ContentType: {}", part, part.getFile().getSize(), part.getFile().getContentType());
        try {
            MultipartFile file = part.getFile();
            if (StringUtils.hasText(part.getFileId())) {
                // TODO 块验证
                ObjectId objectId = new ObjectId(part.getFileId());
                writeChunk(new BsonObjectId(objectId), part.getChunkIndex(), file.getBytes());
                log.info("第 {} 片上传完毕, 文件Id: {}", part.getChunkIndex(), objectId);
                return FileUploadVo.builder().fileId(objectId.toHexString()).build();
            } else {
                ObjectId objectId = new ObjectId();
                BsonValue fileId = new BsonObjectId(objectId);
                Document metadata = new Document()
                        .append(BaseConstants.FILE_METADATA_CONTENT_TYPE, file.getContentType())
                        .append(BaseConstants.FILE_METADATA_SUFFIX, file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
                log.info(" --metadata--  {}", metadata);

                String fileName = part.getName();
                MongoCollection<GridFSFile> filesCollection = getFilesCollection(mongoClient.getDatabase(database), gridFSBucket.getBucketName());
                GridFSFile gridFSFile = new GridFSFile(fileId, fileName, part.getSize(), part.getChunkSize(), new Date(), part.getMd5(), metadata);
                filesCollection.insertOne(gridFSFile);
                writeChunk(fileId, part.getChunkIndex(), file.getBytes());
                return FileUploadVo.builder()
                        .fileId(objectId.toHexString())
                        .name(fileName)
                        .size(part.getSize())
                        .contentType(metadata.getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                        .suffix(metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .gmtCreated(LocalDateTime.now())
                        .previewUrl(previewUrl + "/file/preview/" + objectId.toHexString() + metadata.getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .build();
            }
        } catch (IOException e) {
            log.error("fragment upload exception <<<=== part: {}, msg: {}", part, e.getMessage());
            throw new BusinessException("Fragment upload failed！Error: " + e.getMessage());
        }
    }

    public ApiResult<CheckPartVo> checkFileMd5(String md5, String name, Integer chunkTotal) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("md5").is(md5).and("filename").is(name)));
        CheckPartVo build = CheckPartVo.builder().chunkIndex(0).status(2).build();
        if (Objects.nonNull(gridFSFile)) {
            MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(database), gridFSBucket.getBucketName());
            FindIterable<Document> findIterable = chunksCollection
                    .find(Filters.eq("files_id", gridFSFile.getId()))
                    .sort(Filters.eq("n", -1))
                    .limit(1);
            int chunkIndex = findIterable.first().getInteger("n", 0);
            // 当前片数 < 总片数 说明没有上传完成
            // 等于 至直接返回文件Id
            if (chunkIndex < chunkTotal) {
                build.setFileId(gridFSFile.getObjectId().toHexString());
                build.setChunkIndex(chunkIndex);
                build.setStatus(0);
            } else {
                build.setStatus(1);
                build.setFileInfo(FileUploadVo.builder()
                        .fileId(gridFSFile.getObjectId().toHexString())
                        .name(name)
                        .size(gridFSFile.getLength())
                        .contentType(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_CONTENT_TYPE))
                        .suffix(gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .gmtCreated(LocalDateTime.ofInstant(gridFSFile.getUploadDate().toInstant(), ZoneId.systemDefault()))
                        .previewUrl(previewUrl + "/file/preview/" + gridFSFile.getObjectId().toHexString() + gridFSFile.getMetadata().getString(BaseConstants.FILE_METADATA_SUFFIX))
                        .build());
            }
        }
        return ApiResult.ok(build);
    }


    private void writeChunk(BsonValue fileId, Integer chunkIndex, byte[] bytes) {
        log.info("writeChunk ===>>> fileId: {} , chunkIndex: {} , length: {} ", fileId, chunkIndex, bytes.length);
        MongoCollection<Document> chunksCollection = getChunksCollection(mongoClient.getDatabase(database), gridFSBucket.getBucketName());
        chunksCollection.insertOne(new Document("files_id", fileId).append("n", chunkIndex).append("data", new Binary(bytes)));
    }

    private static MongoCollection<GridFSFile> getFilesCollection(final MongoDatabase database, final String bucketName) {
        return database.getCollection(bucketName + ".files", GridFSFile.class).withCodecRegistry(
                fromRegistries(database.getCodecRegistry(), MongoClientSettings.getDefaultCodecRegistry())
        );
    }

    private static MongoCollection<Document> getChunksCollection(final MongoDatabase database, final String bucketName) {
        return database.getCollection(bucketName + ".chunks").withCodecRegistry(MongoClientSettings.getDefaultCodecRegistry());
    }


    private byte[] getBytes(InputStream inputStream) throws  Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int  i = 0;
        while (-1!=(i=inputStream.read(b))){
            bos.write(b,0,i);
        }
        return bos.toByteArray();
    }

    /**
     * 文件下载
     *
     * @param response
     * @param zipFileName
     */
    private void downFile(HttpServletResponse response, String zipFileName, Long totalBytes) {
        try {
            String path = downloadTempPath + zipFileName;
            File file = new File(path);
            if (file.exists()) {
                try (InputStream ins = new FileInputStream(path);
                     BufferedInputStream bins = new BufferedInputStream(ins);
                     OutputStream outs = response.getOutputStream();
                     BufferedOutputStream bouts = new BufferedOutputStream(outs)) {
                    response.setContentType("application/x-download");
                    response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(zipFileName, "UTF-8"));
                    int bytesRead = 0;
                    byte[] buffer = new byte[totalBytes.intValue()];
                    while ((bytesRead = bins.read(buffer, 0, totalBytes.intValue())) != -1) {
                        bouts.write(buffer, 0, bytesRead);
                    }
                    bouts.flush();
                }
            }
        } catch (Exception e) {
            log.error("文件下载出错", e);
        }
    }
}
