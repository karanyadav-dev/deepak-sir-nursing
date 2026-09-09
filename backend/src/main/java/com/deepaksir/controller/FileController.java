package com.deepaksir.controller;

import com.deepaksir.dto.ApiResponse;
import com.deepaksir.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    
    private final StorageService storageService;
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String directory) {
        
        String fileUrl = storageService.storeFile(file, directory);
        
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", 
            Map.of("url", fileUrl)));
    }
    
    @GetMapping("/{directory}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String directory,
            @PathVariable String filename) {
        
        String fileUrl = "/api/uploads/" + directory + "/" + filename;
        byte[] fileBytes = storageService.getFile(fileUrl);
        
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "inline; filename=\"" + filename + "\"")
            .body(resource);
    }
}