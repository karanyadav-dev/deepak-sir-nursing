package com.deepaksir.service.storage;

import com.deepaksir.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService implements StorageService {
    
    @Value("${storage.local.path:./uploads}")
    private String basePath;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    public String storeImage(MultipartFile file, String directory) {
        return storeFile(file, directory);
    }
    
    @Override
    public String storeFile(MultipartFile file, String directory) {
        try {
            // Create directory if not exists
            Path dirPath = Paths.get(basePath, directory);
            Files.createDirectories(dirPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Return public URL
            return baseUrl + "/api/uploads/" + directory + "/" + filename;
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new ApiException("Failed to store file");
        }
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        try {
            String filePath = fileUrl.replace(baseUrl + "/api/uploads/", "");
            Path path = Paths.get(basePath, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
        }
    }
    
    @Override
    public byte[] getFile(String fileUrl) {
        try {
            String filePath = fileUrl.replace(baseUrl + "/api/uploads/", "");
            Path path = Paths.get(basePath, filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Failed to read file: {}", fileUrl, e);
            throw new ApiException("File not found");
        }
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String filePath = fileUrl.replace(baseUrl + "/api/uploads/", "");
            Path path = Paths.get(basePath, filePath);
            return Files.exists(path);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}