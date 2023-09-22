//testing with postman
package com.example.restapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class FileUploadController {

    @Autowired
    private CsvModification csvModification; 

    @Value("${file.upload.location}")
    private String uploadLocation;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFile(@RequestParam("file") MultipartFile file,
                                             RedirectAttributes redirectAttributes,
                                             Model model) {
        try {
            String fileName = file.getOriginalFilename();
            Path uploadFolderPath = Paths.get(uploadLocation, "uploadedfiles");
            Path filePath = uploadFolderPath.resolve(fileName);

            if (!Files.exists(uploadFolderPath)) {
                Files.createDirectories(uploadFolderPath);
            }

            Files.write(filePath, file.getBytes());
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
            byte[] modifiedFileBytes = csvModification.processUploadedCsv(file);

            if (modifiedFileBytes != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(modifiedFileBytes);
            } else {
                redirectAttributes.addFlashAttribute("message", "Error modifying and saving file.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//testing with browser

//     private long getFileCreationDate(Path filePath) {
//         try {
//             return Files.readAttributes(filePath, BasicFileAttributes.class).creationTime().toMillis();
//         } catch (IOException e) {
//             return 0L;
//         }
//     }
 }

// package com.example.restapi;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.attribute.BasicFileAttributes;
// import java.util.Comparator;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;

// @Controller
// public class FileUploadController {

//     @Autowired
//     private ModifyCsvContentController modifyCsvContentController;

//     @Value("${file.upload.location}")
//     private String uploadLocation;

//     @GetMapping("/")
//     public String index() {
//         return "index";
//     }

//     @PostMapping("/upload")
//     public String uploadFile(@RequestParam("file") MultipartFile file,
//                              RedirectAttributes redirectAttributes,
//                              Model model) {
//         try {
//             String fileName = file.getOriginalFilename();
//             Path uploadFolderPath = Paths.get(uploadLocation, "uploadedfiles");
//             Path filePath = uploadFolderPath.resolve(fileName);

//             if (!Files.exists(uploadFolderPath)) {
//                 Files.createDirectories(uploadFolderPath);
//             }

//             Files.write(filePath, file.getBytes());
//             redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");

//         } catch (IOException e) {
//             redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
//         }

//         try {
            
//             Path uploadedFolderPath = Paths.get(uploadLocation, "uploadedfiles");
//             List<Path> csvFiles = Files.walk(uploadedFolderPath)
//                 .filter(path -> path.toString().endsWith(".csv"))
//                 .collect(Collectors.toList());

            
//             Path mostRecentCsvFile = csvFiles.stream()
//                 .max(Comparator.comparingLong(path -> getFileCreationDate(path)))
//                 .orElse(null);

//             if (mostRecentCsvFile != null) {
//                 ResponseEntity<String> responseEntity = modifyCsvContentController.modifyAndSaveCsv(mostRecentCsvFile.getFileName().toString());
//                 String message = responseEntity.getBody();

//                 if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                     redirectAttributes.addFlashAttribute("message", message);
//                 } else {
//                     redirectAttributes.addFlashAttribute("message", "Error modifying and saving file: " + message);
//                 }
//             } else {
//                 redirectAttributes.addFlashAttribute("message", "No CSV files found in the 'uploadedfiles' folder.");
//             }
//         } catch (Exception e) {
//             redirectAttributes.addFlashAttribute("message", "Error modifying and saving file: " + e.getMessage());
//         }
        
//         return "redirect:/";
         
        
//     }
//     private long getFileCreationDate(Path filePath) {
//         try {
//             return Files.readAttributes(filePath, BasicFileAttributes.class).creationTime().toMillis();
//         } catch (IOException e) {
//             return 0L;
//         }
//     }
// }


