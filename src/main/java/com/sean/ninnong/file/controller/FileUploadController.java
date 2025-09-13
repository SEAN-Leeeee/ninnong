package com.sean.ninnong.file.controller;

import com.sean.ninnong.file.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String savedFileName = fileStorageService.storeFile(file);
        String fileUrl = "/uploads/" + savedFileName;


        return ResponseEntity.ok(new FileUploadResponse(savedFileName, fileUrl));
    }

    public record FileUploadResponse(String fileName, String fileUrl) {}
}
