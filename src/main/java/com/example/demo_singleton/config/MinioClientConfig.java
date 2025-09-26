package com.example.demo_singleton.config;
import com.example.demo_singleton.prop.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MinioClientConfig {
    @Autowired
    private MinioProperties properties;

    private MinioClient minioClient;

    @PostConstruct
    public void init() throws Exception {



        // 创建 MinIO 客户端
        minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();

        // 检查存储桶是否存在，不存在则创建
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            log.info("存储桶 {} 创建成功", properties.getBucket());
        } else {
            log.info("存储桶 {} 已存在", properties.getBucket());
        }
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }
}