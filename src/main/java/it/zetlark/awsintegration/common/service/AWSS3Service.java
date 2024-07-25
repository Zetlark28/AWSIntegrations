package it.zetlark.awsintegration.common.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import it.zetlark.awsintegration.application.config.DocumentProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.S3Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AWSS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket.name}")
    private String s3BucketName;
    @Autowired
    private DocumentProperties documentProperties;

    public String upload(MultipartFile file) throws IOException {
        var fileName = file.getResource().getFilename();
        log.info("Uploading file with name {}", fileName);
        String filePath = documentProperties.getAwsS3Folder() + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // Imposta la lunghezza del contenuto se conosciuta
        // metadata.setContentLength(fileSize);
        try {
            final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, filePath, file.getInputStream(), metadata);
            amazonS3.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());

        }
        var url =  URLDecoder.decode(amazonS3.getUrl(s3BucketName, filePath).toString(), StandardCharsets.UTF_8);

        URI uri = URI.create(url);
        S3Client s3Client = S3Client.create();
        S3Utilities s3Utilities = s3Client.utilities();
        S3Uri s3Uri = s3Utilities.parseUri(uri);
        System.out.println(s3Uri.bucket());
        System.out.println(s3Uri.key());
        System.out.println(s3Uri.region());

        return url;
    }



    public Resource downloadAwsFile(String url){
        URI uri = URI.create(url);
        S3Client s3Client = S3Client.create();
        S3Utilities s3Utilities = s3Client.utilities();
        S3Uri s3Uri = s3Utilities.parseUri(uri);
        System.out.println(s3Uri.bucket());
        System.out.println(s3Uri.key());
        System.out.println(s3Uri.region());
        var response =  amazonS3.getObject(s3Uri.bucket().get(),s3Uri.key().get());

        return new InputStreamResource(response.getObjectContent().getDelegateStream());
    }
//    @Async
//    public String upload(final MultipartFile multipartFile, String awsDestPath) {
//        final File file = convertMultiPartFileToFile(multipartFile);
//        final String fileName = file.getName();
//        log.info("Uploading file with name {}", fileName);
//        String filePath = replaceSlash(documentProperties.getAwsS3BaseFolder()) + awsDestPath;
//        try {
//            final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, filePath + fileName, file);
//            amazonS3.putObject(putObjectRequest);
//        } catch (AmazonServiceException e) {
//            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
//        }
//        return URLDecoder.decode(amazonS3.getUrl(s3BucketName, filePath).toString(), StandardCharsets.UTF_8);
//    }
//
//    public File convertMultiPartFileToFile(final MultipartFile multipartFile) {
//        final File file = new File(multipartFile.getOriginalFilename());
//        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
//            outputStream.write(multipartFile.getBytes());
//        } catch (IOException e) {
//            log.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
//        }
//        return file;
//    }

    private String replaceSlash(String filePath) {
        final String path = filePath.replaceAll("\\\\", "/");
        return path;
    }
}
