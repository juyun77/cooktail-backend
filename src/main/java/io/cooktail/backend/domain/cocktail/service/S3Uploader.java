package io.cooktail.backend.domain.cocktail.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class S3Uploader {
  @Value("${cloud.aws.s3.bucketName}")
  private String bucket;

  private final AmazonS3Client amazonS3Client;

  public List<String> uploadFiles(String dirName, List<MultipartFile> multipartFiles) {
    List<String> s3files = new ArrayList<>();

    for (MultipartFile multipartFile : multipartFiles) {

      if (multipartFile.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다.");
      }

      String uploadFileUrl = "";

      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(multipartFile.getSize());
      objectMetadata.setContentType(multipartFile.getContentType());

      try (InputStream inputStream = multipartFile.getInputStream()) {
        String key = dirName + "/" + UUID.randomUUID() + "." + multipartFile.getOriginalFilename();
        amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        uploadFileUrl = amazonS3Client.getUrl(bucket, key).toString();
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
      }

      s3files.add(uploadFileUrl);
    }
    return s3files;
  }

  public void deleteFile(String imageUrl) {
    String key = getKeyFromImageUrl(imageUrl);
    try{
      amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
    }catch (AmazonServiceException e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 삭제 중 오류가 발생했습니다.", e);
    }
  }

  private String getKeyFromImageUrl(String imageUrl){
    try{
      URL url = new URL(imageUrl);
      String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
      return decodingKey.substring(1); // 맨 앞의 '/' 제거
    }catch (MalformedURLException | UnsupportedEncodingException e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 키 추출 중 오류가 발생했습니다. 이미지 URL: " + imageUrl, e);
    }
  }

  public String uploadFile(String dirName, MultipartFile multipartFile) {
    if (multipartFile.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다.");
    }

    String uploadFileUrl = "";

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(multipartFile.getSize());
    objectMetadata.setContentType(multipartFile.getContentType());

    try (InputStream inputStream = multipartFile.getInputStream()) {
      String key = dirName + "/" + UUID.randomUUID() + "." + multipartFile.getOriginalFilename();
      amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
      uploadFileUrl = amazonS3Client.getUrl(bucket, key).toString();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
    }

    return uploadFileUrl;
  }
}
