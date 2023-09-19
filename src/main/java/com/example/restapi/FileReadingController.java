package com.example.restapi;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileReadingController {

    @Value("${file.upload.location}")
    private String uploadLocation;

    @PostMapping("/process/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<List<List<String>>> processCsvFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadLocation, "uploadedfiles", fileName);

            CSVParser csvParser = CSVParser.parse(filePath.toFile(), Charset.defaultCharset(), CSVFormat.DEFAULT);

            List<List<String>> csvData = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {
                List<String> rowData = new ArrayList<>();
                for (String value : csvRecord) {
                    rowData.add(value);
                }
                csvData.add(rowData);
            }

            return ResponseEntity.ok(csvData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}