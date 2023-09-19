package com.example.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.mock.web.MockMultipartFile;

@RestController
public class ModifyCsvContentController {

    @Autowired
    private CsvModification csvModification;

    @Value("${file.upload.location}")
    private String uploadLocation;

    @PostMapping("/modify-and-save/{fileName:.+}")
@ResponseBody
public ResponseEntity<String> modifyAndSaveCsv(@PathVariable String fileName) {
    try {
        Path uploadedFilePath = Paths.get(uploadLocation, "uploadedfiles", fileName);
        byte[] fileBytes = Files.readAllBytes(uploadedFilePath);
        MultipartFile uploadedFile = new MockMultipartFile(fileName, fileName, "text/csv", fileBytes);
        csvModification.processUploadedCsv(uploadedFile);
        return ResponseEntity.ok("CSV file modified and saved as " + uploadedFilePath.toString());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error modifying and saving file: " + e.getMessage());
    }
}
    }