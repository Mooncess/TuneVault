package ru.mooncess.file_service.components;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class MinioComponent {
    @Value("${minio.bucket.logo}")
    private String logo;
    @Value("${minio.bucket.demo}")
    private String demo;
    @Value("${minio.bucket.source}")
    private String source;

    private final MinioClient minioClient;

    @Autowired
    public MinioComponent(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    private void initializeBuckets() {
        try {
            createBucketIfNotExists(logo);
            createBucketIfNotExists(demo);
            createBucketIfNotExists(source);
        } catch (Exception e) {
            System.err.println("Ошибка при создании buckets: " + e.getMessage());
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println("Bucket " + bucketName + " создан.");
        }
    }

    public void putResource(MultipartFile file, String uniqueFileName, String bucket) {
        try {
            InputStream in = new ByteArrayInputStream(file.getBytes());
            putObject(uniqueFileName, in, bucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putObject(String objectName, InputStream inputStream, String bucketName) {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .stream(inputStream, -1, 10485760).build());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public byte[] getObject(String objectName, String bucketName) {
        try (InputStream stream = minioClient
                .getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {
            return stream.readAllBytes();
        } catch (ErrorResponseException | InsufficientDataException |
                 InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public boolean compareExtension(String file1, String file2) {
        return file1.substring(file1.lastIndexOf(".")).equals(file2.substring(file2.lastIndexOf(".")));
    }

    public void overwriteFile(MultipartFile file, String oldFileName, String bucket) {
        try {
            String uniqueFileName = oldFileName.substring(0, oldFileName.lastIndexOf(".")) +
                    file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            InputStream in = new ByteArrayInputStream(file.getBytes());
            putObject(uniqueFileName, in, bucket);
            deleteFile(oldFileName, bucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateUniqueFileName(MultipartFile file) {
        if (file == null) {
            return "";
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueID = UUID.randomUUID().toString();
        return uniqueID + fileExtension;
    }

    public boolean deleteFile(String objectName, String bucketName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            System.err.println("Ошибка при удалении файла: файл не найден");
        } catch (Exception e) {
            System.err.println("Ошибка при удалении файла: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
