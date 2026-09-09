package com.deepaksir.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    
    /**
     * Store an image file and return the URL
     */
    String storeImage(MultipartFile file, String directory);
    
    /**
     * Store any file and return the URL
     */
    String storeFile(MultipartFile file, String directory);
    
    /**
     * Delete a file by URL
     */
    void deleteFile(String fileUrl);
    
    /**
     * Get file bytes by URL
     */
    byte[] getFile(String fileUrl);
    
    /**
     * Check if file exists
     */
    boolean fileExists(String fileUrl);
}