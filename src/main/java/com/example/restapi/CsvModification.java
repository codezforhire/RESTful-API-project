
//testing with postman

package com.example.restapi;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CsvModification {

    private static final String PROCESSED_LOCATION = "C:\\Users\\anass\\Downloads\\restapi\\restapi\\src\\main\\resources\\uploads\\uploadedfiles\\processedfiles";
    public byte[] processUploadedCsv(MultipartFile uploadedFile) throws IOException {
          String originalFileName = uploadedFile.getOriginalFilename();
        String processedFileName = originalFileName.replace(".csv", "-processed.csv");
        Path processedFilePath = Paths.get(PROCESSED_LOCATION, processedFileName);
        String originalContent = new String(uploadedFile.getBytes(), Charset.defaultCharset());
        String modifiedContent = modifyCsvContent(originalContent, processedFilePath);
        Files.createDirectories(processedFilePath.getParent());
        Files.write(processedFilePath, modifiedContent.getBytes());
        List<List<String>> csvTable = extractCsvDataFromFile(processedFilePath);
        csvTable = modifyCsvTable(csvTable);
        saveModifiedCsv(csvTable, processedFilePath);
        convertTableToCsvFile(csvTable, processedFilePath);
    
        System.out.println("CSV file modified and saved as " + processedFilePath.toString());
        
        return Files.readAllBytes(processedFilePath); 
    }
public List<List<String>> extractCsvDataFromFile(Path filePath) throws IOException {
    try (InputStream inputStream = Files.newInputStream(filePath)) {
        CSVParser csvParser = CSVParser.parse(inputStream, Charset.defaultCharset(), CSVFormat.DEFAULT);
        List<List<String>> csvData = new ArrayList<>();

        for (CSVRecord csvRecord : csvParser) {
            List<String> row = new ArrayList<>();
            for (String cell : csvRecord) {
                row.add(cell);
            }
            csvData.add(row);
        }

        return csvData;
    }
}

    public String currentyear(){
        Year currentyear=Year.now();
        String year=String.valueOf(currentyear.getValue());
        return year;
    }

    public List<List<String>> modifyCsvTable(List<List<String>> csvTable) {
        for (List<String> row : csvTable) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                cell = cell.replaceAll("(- VIRI -)", ", VIRI ,")
                            .replaceAll("(-LIB_ECRITURE :)", ", LIB_ECRITURE ,")
                            .replaceAll("(-VIRT)", ", VIRT")
                            .replaceAll("(- CHEQ - )", ",CHEQ,")
                            .replaceAll("(PPR)", " PPR,")
                            .replaceAll("(RABAT)", "RABAT,")
                            .replaceAll("-(NUM_PIECE.{9})", " ,$1, ");
                            
                row.set(i, cell);
            }
        }
        return csvTable;
    }
    public String modifyCsvContent(String originalContent,Path processedFilePath) {
            String modifiedContent = originalContent.replaceAll("", "");
            modifiedContent = modifylongDates(modifiedContent);
            modifiedContent = modifyDates(modifiedContent);
            modifiedContent = modifyhalfDates(modifiedContent);
            modifiedContent = modifyshortDates(modifiedContent);
            
            return modifiedContent;
        }

    public String modifyDates(String content) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearPattern = String.valueOf(year);
        
        Pattern datePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[0-2])(" + yearPattern + ")");
        Matcher dateMatcher = datePattern.matcher(content);
        StringBuffer result = new StringBuffer();
             while (dateMatcher.find()) {
            String date = dateMatcher.group(1) + "/" + dateMatcher.group(2) + "/" + dateMatcher.group(3);
            dateMatcher.appendReplacement(result, date);
        }
        dateMatcher.appendTail(result);
        
        return result.toString();
    }
    public String modifyhalfDates(String content) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearPattern = String.valueOf(year);
        Pattern halfdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(-)(" + yearPattern + ")");
        Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
        StringBuffer result = new StringBuffer();
        while (halfdateMatcher.find()) {
            String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3);
            halfdateMatcher.appendReplacement(result, halfdate);
        }
        halfdateMatcher.appendTail(result);
        
        return result.toString();
    }
    public String modifyshortDates(String content) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearPattern = String.valueOf(year);
        Pattern shortdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(" + yearPattern + ")");
        Matcher shortdateMatcher = shortdatePattern.matcher(content);
        
        StringBuffer result = new StringBuffer();
        while (shortdateMatcher.find()) {
            String shortdate = shortdateMatcher.group(1) + "/" + shortdateMatcher.group(2);
            shortdateMatcher.appendReplacement(result, shortdate);
        }
        shortdateMatcher.appendTail(result);
        
        return result.toString();
    }
    public String modifylongDates(String content) {
        Year currentYear = Year.now();
        int year = currentYear.getValue();
        String yearPattern = String.valueOf(year);
        Pattern halfdatePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(-)(0?[1-9]|1[0-2])(-)(" + yearPattern + ")");
        Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
        StringBuffer result = new StringBuffer();
        while (halfdateMatcher.find()) {
            String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3)+"/" +halfdateMatcher.group(5) ;
            halfdateMatcher.appendReplacement(result, halfdate);
        }
        halfdateMatcher.appendTail(result);
        
        return result.toString();
    }
    

    public void saveModifiedCsv(List<List<String>> csvTable, Path filePath) throws IOException {
        String modifiedContent = convertTableToCsvContent(csvTable);
        Files.write(filePath, modifiedContent.getBytes());
    }

    public String convertTableToCsvContent(List<List<String>> csvTable) {
        StringBuilder csvContent = new StringBuilder();

        for (List<String> row : csvTable) {
            if (!row.isEmpty()) {
                for (String cell : row) {
                    csvContent.append(escapeCsvCell(cell)).append(",");
                }
                csvContent.deleteCharAt(csvContent.length() - 1);
                csvContent.append("\n");
            }
        }
        

        return csvContent.toString();
    }
    public void convertTableToCsvFile(List<List<String>> csvTable, Path filePath) throws IOException {
    try (FileWriter fileWriter = new FileWriter(filePath.toString())) {
        for (List<String> row : csvTable) {
            if (!row.isEmpty()) {
                for (String cell : row) {
                    fileWriter.append(escapeCsvCell(cell)).append(",");
                }
                fileWriter.append("\n");
            }
        }

    }
}

    private String escapeCsvCell(String cell) {
    return cell; }

    public List<List<String>> extractCsvDataFromString(String csvContent) {
        List<List<String>> csvData = new ArrayList<>();
        String[] lines = csvContent.split("\n");

        for (String line : lines) {
            List<String> row = Arrays.asList(line.split(","));
            csvData.add(row);
        }

        return csvData;
    }
   
}//testing with browser


// package com.example.restapi;

// import org.apache.commons.csv.CSVFormat;
// import org.apache.commons.csv.CSVParser;
// import org.apache.commons.csv.CSVRecord;
// import org.springframework.stereotype.Component;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.charset.Charset;
// import java.nio.file.*;
// import java.time.Year;
// import java.util.*;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// @Component
// public class CsvModification {

//     private static final String UPLOAD_LOCATION = "C:\\Users\\anass\\Downloads\\restapi\\restapi\\src\\main\\resources\\uploads\\uploadedfiles";
//     private static final String PROCESSED_LOCATION = "C:\\Users\\anass\\Downloads\\restapi\\restapi\\src\\main\\resources\\uploads\\uploadedfiles\\processedfiles";
//     public void processUploadedCsv(MultipartFile uploadedFile) throws IOException {
//         String originalFileName = uploadedFile.getOriginalFilename();
//         String processedFileName = originalFileName.replace(".csv", "-processed.csv");
//         Path uploadedFilePath = Paths.get(UPLOAD_LOCATION, originalFileName);
//         Path processedFilePath = Paths.get(PROCESSED_LOCATION, processedFileName);
//         String originalContent = new String(uploadedFile.getBytes(), Charset.defaultCharset());
//         String modifiedContent = modifyCsvContent(originalContent, processedFilePath);
//         Files.createDirectories(processedFilePath.getParent());
//         Files.write(processedFilePath, modifiedContent.getBytes());
//         List<List<String>> csvTable = extractCsvDataFromFile(processedFilePath);
//         csvTable = modifyCsvTable(csvTable);
//         saveModifiedCsv(csvTable, processedFilePath);
//         convertTableToCsvFile(csvTable, processedFilePath);
    
//         System.out.println("CSV file modified and saved as " + processedFilePath.toString());
//     }
// public List<List<String>> extractCsvDataFromFile(Path filePath) throws IOException {
//     try (InputStream inputStream = Files.newInputStream(filePath)) {
//         CSVParser csvParser = CSVParser.parse(inputStream, Charset.defaultCharset(), CSVFormat.DEFAULT);
//         List<List<String>> csvData = new ArrayList<>();

//         for (CSVRecord csvRecord : csvParser) {
//             List<String> row = new ArrayList<>();
//             for (String cell : csvRecord) {
//                 row.add(cell);
//             }
//             csvData.add(row);
//         }

//         return csvData;
//     }
// }

//     public String currentyear(){
//         Year currentyear=Year.now();
//         String year=String.valueOf(currentyear.getValue());
//         return year;
//     }

//     public List<List<String>> modifyCsvTable(List<List<String>> csvTable) {
//         for (List<String> row : csvTable) {
//             for (int i = 0; i < row.size(); i++) {
//                 String cell = row.get(i);
//                 cell = cell.replaceAll("(- VIRI -)", ", VIRI ,")
//                             .replaceAll("(-LIB_ECRITURE :)", ", LIB_ECRITURE ,")
//                             .replaceAll("(-VIRT)", ", VIRT")
//                             .replaceAll("(- CHEQ - )", ",CHEQ,")
//                             .replaceAll("(PPR)", " PPR,")
//                             .replaceAll("(RABAT)", "RABAT,")
//                             .replaceAll("-(NUM_PIECE.{9})", " ,$1, ");
                            
//                 row.set(i, cell);
//             }
//         }
//         return csvTable;
//     }
//     public String modifyCsvContent(String originalContent,Path processedFilePath) {
//             String modifiedContent = originalContent.replaceAll("", "");
//             modifiedContent = modifylongDates(modifiedContent);
//             modifiedContent = modifyDates(modifiedContent);
//             modifiedContent = modifyhalfDates(modifiedContent);
//             modifiedContent = modifyshortDates(modifiedContent);
            
//             return modifiedContent;
//         }

//     public String modifyDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
        
//         Pattern datePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[0-2])(" + yearPattern + ")");
//         Matcher dateMatcher = datePattern.matcher(content);
//         StringBuffer result = new StringBuffer();
//              while (dateMatcher.find()) {
//             String date = dateMatcher.group(1) + "/" + dateMatcher.group(2) + "/" + dateMatcher.group(3);
//             dateMatcher.appendReplacement(result, date);
//         }
//         dateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifyhalfDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern halfdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(-)(" + yearPattern + ")");
//         Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (halfdateMatcher.find()) {
//             String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3);
//             halfdateMatcher.appendReplacement(result, halfdate);
//         }
//         halfdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifyshortDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern shortdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(" + yearPattern + ")");
//         Matcher shortdateMatcher = shortdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (shortdateMatcher.find()) {
//             String shortdate = shortdateMatcher.group(1) + "/" + shortdateMatcher.group(2);
//             shortdateMatcher.appendReplacement(result, shortdate);
//         }
//         shortdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifylongDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern halfdatePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(-)(0?[1-9]|1[0-2])(-)(" + yearPattern + ")");
//         Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (halfdateMatcher.find()) {
//             String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3)+"/" +halfdateMatcher.group(5) ;
//             halfdateMatcher.appendReplacement(result, halfdate);
//         }
//         halfdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
    

//     public void saveModifiedCsv(List<List<String>> csvTable, Path filePath) throws IOException {
//         String modifiedContent = convertTableToCsvContent(csvTable);
//         Files.write(filePath, modifiedContent.getBytes());
//     }

//     public String convertTableToCsvContent(List<List<String>> csvTable) {
//         StringBuilder csvContent = new StringBuilder();

//         for (List<String> row : csvTable) {
//             if (!row.isEmpty()) {
//                 for (String cell : row) {
//                     csvContent.append(escapeCsvCell(cell)).append(",");
//                 }
//                 csvContent.deleteCharAt(csvContent.length() - 1);
//                 csvContent.append("\n");
//             }
//         }
        

//         return csvContent.toString();
//     }
//     public void convertTableToCsvFile(List<List<String>> csvTable, Path filePath) throws IOException {
//     try (FileWriter fileWriter = new FileWriter(filePath.toString())) {
//         for (List<String> row : csvTable) {
//             if (!row.isEmpty()) {
//                 for (String cell : row) {
//                     fileWriter.append(escapeCsvCell(cell)).append(",");
//                 }
//                 fileWriter.append("\n");
//             }
//         }

//     }
// }

//     private String escapeCsvCell(String cell) {
//     return cell; }

//     public List<List<String>> extractCsvDataFromString(String csvContent) {
//         List<List<String>> csvData = new ArrayList<>();
//         String[] lines = csvContent.split("\n");

//         for (String line : lines) {
//             List<String> row = Arrays.asList(line.split(","));
//             csvData.add(row);
//         }

//         return csvData;
//     }
// }


// package com.example.restapi;

// import org.apache.commons.csv.CSVFormat;
// import org.apache.commons.csv.CSVParser;
// import org.apache.commons.csv.CSVRecord;
// import org.springframework.stereotype.Component;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.charset.Charset;
// import java.nio.file.*;
// import java.time.Year;
// import java.util.*;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// @Component
// public class CsvModification {

//     private static final String UPLOAD_LOCATION = "C:\\Users\\anass\\Downloads\\restapi\\restapi\\src\\main\\resources\\uploads\\uploadedfiles";
//     private static final String PROCESSED_LOCATION = "C:\\Users\\anass\\Downloads\\restapi\\restapi\\src\\main\\resources\\uploads\\uploadedfiles\\processedfiles";

//     public byte[] processAndReturnModifiedCsv(MultipartFile uploadedFile) throws IOException {
//         String originalFileName = uploadedFile.getOriginalFilename();
//         String processedFileName = originalFileName.replace(".csv", "-processed.csv");
//         Path uploadedFilePath = Paths.get(UPLOAD_LOCATION, originalFileName);
//         Path processedFilePath = Paths.get(PROCESSED_LOCATION, processedFileName);
//         String originalContent = new String(uploadedFile.getBytes(), Charset.defaultCharset());
//         String modifiedContent = modifyCsvContent(originalContent, processedFilePath);

//         // Create the 'processedfiles' directory if it doesn't exist
//         Files.createDirectories(processedFilePath.getParent());

//         // Write the modified content to the processed file
//         Files.write(processedFilePath, modifiedContent.getBytes());

//         // Return the modified content as a byte array
//         return Files.readAllBytes(processedFilePath);
//     }
// public List<List<String>> extractCsvDataFromFile(Path filePath) throws IOException {
//     try (InputStream inputStream = Files.newInputStream(filePath)) {
//         CSVParser csvParser = CSVParser.parse(inputStream, Charset.defaultCharset(), CSVFormat.DEFAULT);
//         List<List<String>> csvData = new ArrayList<>();

//         for (CSVRecord csvRecord : csvParser) {
//             List<String> row = new ArrayList<>();
//             for (String cell : csvRecord) {
//                 row.add(cell);
//             }
//             csvData.add(row);
//         }

//         return csvData;
//     }
// }

//     public String currentyear(){
//         Year currentyear=Year.now();
//         String year=String.valueOf(currentyear.getValue());
//         return year;
//     }

//     public List<List<String>> modifyCsvTable(List<List<String>> csvTable) {
//         for (List<String> row : csvTable) {
//             for (int i = 0; i < row.size(); i++) {
//                 String cell = row.get(i);
//                 cell = cell.replaceAll("(- VIRI -)", ", VIRI ,")
//                             .replaceAll("(-LIB_ECRITURE :)", ", LIB_ECRITURE ,")
//                             .replaceAll("(-VIRT)", ", VIRT")
//                             .replaceAll("(- CHEQ - )", ",CHEQ,")
//                             .replaceAll("(PPR)", " PPR,")
//                             .replaceAll("(RABAT)", "RABAT,")
//                             .replaceAll("-(NUM_PIECE.{9})", " ,$1, ");
                            
//                 row.set(i, cell);
//             }
//         }
//         return csvTable;
//     }
//     public String modifyCsvContent(String originalContent,Path processedFilePath) {
//             String modifiedContent = originalContent.replaceAll("", "");
//             modifiedContent = modifylongDates(modifiedContent);
//             modifiedContent = modifyDates(modifiedContent);
//             modifiedContent = modifyhalfDates(modifiedContent);
//             modifiedContent = modifyshortDates(modifiedContent);
            
//             return modifiedContent;
//         }

//     public String modifyDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
        
//         Pattern datePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[0-2])(" + yearPattern + ")");
//         Matcher dateMatcher = datePattern.matcher(content);
//         StringBuffer result = new StringBuffer();
//              while (dateMatcher.find()) {
//             String date = dateMatcher.group(1) + "/" + dateMatcher.group(2) + "/" + dateMatcher.group(3);
//             dateMatcher.appendReplacement(result, date);
//         }
//         dateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifyhalfDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern halfdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(-)(" + yearPattern + ")");
//         Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (halfdateMatcher.find()) {
//             String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3);
//             halfdateMatcher.appendReplacement(result, halfdate);
//         }
//         halfdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifyshortDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern shortdatePattern = Pattern.compile("(0?[0-9]|[0-9][1-2])(" + yearPattern + ")");
//         Matcher shortdateMatcher = shortdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (shortdateMatcher.find()) {
//             String shortdate = shortdateMatcher.group(1) + "/" + shortdateMatcher.group(2);
//             shortdateMatcher.appendReplacement(result, shortdate);
//         }
//         shortdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
//     public String modifylongDates(String content) {
//         Year currentYear = Year.now();
//         int year = currentYear.getValue();
//         String yearPattern = String.valueOf(year);
//         Pattern halfdatePattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(-)(0?[1-9]|1[0-2])(-)(" + yearPattern + ")");
//         Matcher halfdateMatcher = halfdatePattern.matcher(content);
        
//         StringBuffer result = new StringBuffer();
//         while (halfdateMatcher.find()) {
//             String halfdate = halfdateMatcher.group(1) + "/" + halfdateMatcher.group(3)+"/" +halfdateMatcher.group(5) ;
//             halfdateMatcher.appendReplacement(result, halfdate);
//         }
//         halfdateMatcher.appendTail(result);
        
//         return result.toString();
//     }
    

//     public void saveModifiedCsv(List<List<String>> csvTable, Path filePath) throws IOException {
//         String modifiedContent = convertTableToCsvContent(csvTable);
//         Files.write(filePath, modifiedContent.getBytes());
//     }

//     public String convertTableToCsvContent(List<List<String>> csvTable) {
//         StringBuilder csvContent = new StringBuilder();

//         for (List<String> row : csvTable) {
//             if (!row.isEmpty()) {
//                 for (String cell : row) {
//                     csvContent.append(escapeCsvCell(cell)).append(",");
//                 }
//                 csvContent.deleteCharAt(csvContent.length() - 1);
//                 csvContent.append("\n");
//             }
//         }
        

//         return csvContent.toString();
//     }
//     public void convertTableToCsvFile(List<List<String>> csvTable, Path filePath) throws IOException {
//     try (FileWriter fileWriter = new FileWriter(filePath.toString())) {
//         for (List<String> row : csvTable) {
//             if (!row.isEmpty()) {
//                 for (String cell : row) {
//                     fileWriter.append(escapeCsvCell(cell)).append(",");
//                 }
//                 fileWriter.append("\n");
//             }
//         }

//     }
// }

//     private String escapeCsvCell(String cell) {
//     return cell; }

//     public List<List<String>> extractCsvDataFromString(String csvContent) {
//         List<List<String>> csvData = new ArrayList<>();
//         String[] lines = csvContent.split("\n");

//         for (String line : lines) {
//             List<String> row = Arrays.asList(line.split(","));
//             csvData.add(row);
//         }

//         return csvData;
//     }
    // public byte[] getModifiedFileBytes(MultipartFile processedFile) throws IOException {
    //     String originalContent = new String(processedFile.getBytes(), Charset.defaultCharset());
    //     String modifiedContent = modifyCsvContent(originalContent,null);
    //     return modifiedContent.getBytes();
    // }
    
// }