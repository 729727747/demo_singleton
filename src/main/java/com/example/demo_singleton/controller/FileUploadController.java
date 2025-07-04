package com.example.demo_singleton.controller;

import com.example.demo_singleton.config.MinioClientConfig;
import com.example.demo_singleton.prop.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final String CHUNK_DIR = "uploads/chunks/";
    private final String FINAL_DIR = "uploads/final/";

    /**
     * 初始化上传
     * @param fileName 文件名
     * @param fileMd5 文件唯一标识
     */
    @PostMapping("/init")
    public ResponseEntity<String> initUpload(
            @RequestParam String fileName,
            @RequestParam String fileMd5){

        // 创建分块临时目录
        String uploadId = UUID.randomUUID().toString();
        Path chunkDir = Paths.get(CHUNK_DIR, fileMd5 + "_" + uploadId);
        try {
            Files.createDirectories(chunkDir);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建目录失败");
        }
        return ResponseEntity.ok(uploadId);
    }

    /**
     * 上传分块
     * @param chunk 分块文件
     * @param index 分块索引
     */
    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam MultipartFile chunk,
            @RequestParam String uploadId,
            @RequestParam String fileMd5,
            @RequestParam Integer index){

        // 生成分块文件名
        String chunkName = "chunk_" + index + ".tmp";
        Path filePath = Paths.get(CHUNK_DIR, fileMd5 + "_" + uploadId, chunkName);

        try {
            chunk.transferTo(filePath);
            return ResponseEntity.ok("分块上传成功");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("分块保存失败");
        }
    }

    /**
     * 合并文件分块
     */
    @PostMapping("/merge")
    public ResponseEntity<String> mergeChunks(
            @RequestParam String fileName,
            @RequestParam String uploadId,
            @RequestParam String fileMd5){

        // 1. 获取分块目录
        File chunkDir = new File(CHUNK_DIR + fileMd5 + "_" + uploadId);

        // 2. 获取排序后的分块文件
        File[] chunks = chunkDir.listFiles();
        if (chunks == null || chunks.length == 0) {
            return ResponseEntity.badRequest().body("无分块文件");
        }

        Arrays.sort(chunks, Comparator.comparingInt(f ->
                Integer.parseInt(f.getName().split("_")[1].split("\\.")[0])));

        // 3. 合并文件
        Path finalPath = Paths.get(FINAL_DIR, fileName);
        try (BufferedOutputStream outputStream =
                     new BufferedOutputStream(Files.newOutputStream(finalPath))) {

            for (File chunkFile : chunks) {
                Files.copy(chunkFile.toPath(), outputStream);
            }

            // 4. 清理临时分块
            FileUtils.deleteDirectory(chunkDir);

            return ResponseEntity.ok("文件合并成功：" + finalPath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("合并失败：" + e.getMessage());
        }
    }


    /**
     * 断点续传
     * @param fileMd5
     * @param uploadId
     * @return
     */
    @GetMapping("/check/{fileMd5}/{uploadId}")
    public ResponseEntity<List<Integer>> getUploadedChunks(
            @PathVariable String fileMd5,
            @PathVariable String uploadId) {

        Path chunkDir = Paths.get(CHUNK_DIR, fileMd5 + "_" + uploadId);
        if (!Files.exists(chunkDir)) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        try {
            List<Integer> uploaded = Files.list(chunkDir)
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.startsWith("chunk_"))
                    .map(name -> name.replace("chunk_", "").replace(".tmp", ""))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(uploaded);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }


    @Autowired
    private MinioClientConfig minioClientConfig;
    @Autowired
    private MinioProperties minioProperties;

    /**
     * 上传文件到 MinIO
     */
    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) {
        try (InputStream fileStream = file.getInputStream()){
            // 生成对象名称（例如：时间戳+原文件名）
            String objectName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 上传文件到 MinIO
            minioClientConfig.getMinioClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(fileStream, file.getSize(), -1) // -1 表示不限制分块大小
                            .contentType(file.getContentType()) // 设置文件类型（可选）
                            .build()
            );

            // 返回文件访问 URL（如果是私有桶，需生成预签名 URL）

            return minioClientConfig.getMinioClient().getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS) // URL 有效期（可选，私有桶需要）
                            .method(Method.GET) // 添加HTTP方法
                            .build()
            );
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return "";
        }
    }

}
