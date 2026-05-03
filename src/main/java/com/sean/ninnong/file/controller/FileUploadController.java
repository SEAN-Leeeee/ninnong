package com.sean.ninnong.file.controller;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.file.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserPrincipal user) {
        String savedFileName = fileStorageService.storeFile(file);
        String fileUrl = "/uploads/" + savedFileName;


        return ResponseEntity.ok(new FileUploadResponse(savedFileName, fileUrl));
    }

    public record FileUploadResponse(String fileName, String fileUrl) {}
}
