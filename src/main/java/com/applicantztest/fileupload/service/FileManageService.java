package com.applicantztest.fileupload.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.applicantztest.fileupload.dto.ProcessingResult;
import com.applicantztest.fileupload.dto.ProcessingStat;
import com.applicantztest.fileupload.model.FileManagement;
import com.applicantztest.fileupload.repository.FileManageRepo;

@Service
public class FileManageService {

    private static final Logger logger = LoggerFactory.getLogger(FileManageService.class);

    private final FileManageRepo fileManageRepo;

    private static final Pattern WORD_PATTERN = Pattern.compile("\\s+");

    public FileManageService(FileManageRepo fileManageRepo) {
        this.fileManageRepo = fileManageRepo;
    }

    public ProcessingResult processFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("File processing failed: File is null or empty");
            return new ProcessingResult(0, 0, "File is null or empty");
        }

        String filename = file.getOriginalFilename();
        logger.info("Starting file processing for: {}", filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            int lineCount = 0;
            int wordCount = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                int wordsInLine = countWordsInLine(line);
                wordCount += wordsInLine;

                if (lineCount % 1000 == 0) {
                    logger.debug("Processed {} lines, {} words so far for file: {}",
                            lineCount, wordCount, filename);
                }
            }

            logger.info("File processing completed for: {}. Lines: {}, Words: {}",
                    filename, lineCount, wordCount);

            return new ProcessingResult(lineCount, wordCount);

        } catch (IOException e) {
            logger.error("IOException occurred while processing file: {}", filename, e);
            return new ProcessingResult(0, 0, "Error reading file: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing file: {}", filename, e);
            return new ProcessingResult(0, 0, "Unexpected error: " + e.getMessage());
        }
    }

    private int countWordsInLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return 0;
        }

        // Trim the line and split by whitespace
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return 0;
        }

        // Split by one or more whitespace char
        String[] words = WORD_PATTERN.split(trimmedLine);

        // Filter out empty strings
        int wordCount = 0;
        for (String word : words) {
            String trimmedWord = word.trim();

            if (!trimmedWord.isEmpty() && !isSingleSymbol(trimmedWord)) {
                wordCount++;
            }
        }

        return wordCount;
    }

    private boolean isSingleSymbol(String word) {
        if (word.isEmpty()) {
            return true;
        }

        for (char c : word.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public FileManagement saveToDatabase(FileManagement result) {
        if (result == null) {
            logger.error("Cannot save to database: Result is null");
            throw new IllegalArgumentException("FileProcessingResult cannot be null");
        }

        try {
            logger.debug("Saving processing result to database: {}", result);
            FileManagement savedResult = fileManageRepo.save(result);
            logger.info("Successfully saved processing result to database with Id: {}", savedResult.getId());
            return savedResult;
        } catch (Exception exception) {
            logger.error("Failed to save processing result to database: {}", result, exception);
            throw new RuntimeException("Failed to save processing result to database", exception);
        }
    }

    public FileManagement processAndSaveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("Cannot process and save: File is null or empty");
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String filename = file.getOriginalFilename();
        String fileType = FilenameUtils.getExtension(filename).toLowerCase();
        long fileSize = file.getSize();

        logger.info("Processing and saving file: {} (type: {}, size: {} bytes)",
                filename, fileType, fileSize);

        try {
            byte[] content = file.getBytes();

            // Process the file to get line and word counts
            ProcessingResult processingResult = processFile(file);

            FileManagement result;

            if (processingResult.hasError()) {
                // Create failed result
                result = new FileManagement(filename, fileType, fileSize,
                        processingResult.getErrorMessage());
                logger.warn("File processing failed for: {}. Error: {}",
                        filename, processingResult.getErrorMessage());
            } else {
                // Create successful result
                result = new FileManagement(filename, fileType, fileSize,
                        processingResult.getLineCount(),
                        processingResult.getWordCount(), content);
                logger.info("File processing successful for: {}. Lines: {}, Words: {}",
                        filename, processingResult.getLineCount(), processingResult.getWordCount());
            }

            // Save to database
            FileManagement savedResult = saveToDatabase(result);
            logger.info("File processing result saved to database with Id: {}", savedResult.getId());

            return savedResult;

        } catch (Exception exception) {
            logger.error("Error occurred while processing and saving file: {}", filename, exception);

            FileManagement errorResult = new FileManagement(filename, fileType, fileSize,
                    "Processing error: " + exception.getMessage());

            try {
                return saveToDatabase(errorResult);
            } catch (Exception saveException) {
                logger.error("Failed to save error result to database for file: {}", filename, saveException);
                throw new RuntimeException("Failed to process file and save result", saveException);
            }
        }
    }

    public byte[] getFileContent(FileManagement file) throws IOException {
        if (file.getContent() == null) {
            throw new FileNotFoundException("File content not found for: " + file.getFilename());
        }
        return file.getContent();
    }

    public ProcessingStat getProcessingStatistics() {
        try {
            long totalFiles = fileManageRepo.count();
            long successfulFiles = fileManageRepo.countSuccessfulProcessing();
            long failedFiles = fileManageRepo.countFailedProcessing();
            long totalLines = fileManageRepo.getTotalLinesProcessed();
            long totalWords = fileManageRepo.getTotalWordsProcessed();

            ProcessingStat stats = new ProcessingStat(
                    totalFiles, successfulFiles, failedFiles, totalLines, totalWords);

            logger.info("Retrieved processing statistics: {}", stats);
            return stats;

        } catch (Exception exception) {
            logger.error("Error retrieving processing statistics", exception);
            return new ProcessingStat(0, 0, 0, 0, 0);
        }
    }

    public boolean deleteFileFromDatabase(Long id) {
        try {
            if (!fileManageRepo.existsById(id)) {
                logger.warn("File with id {} not found in database", id);
                return false;
            }

            fileManageRepo.deleteById(id);
            logger.info("Successfully deleted file with id {} from database", id);
            return true;

        } catch (Exception e) {
            logger.error("Error deleting file with id {} from database", id, e);
            throw new RuntimeException("Failed to delete file from database", e);
        }
    }

    public void deleteAll() {
        try {
            fileManageRepo.deleteAll();
            logger.info("Successfully deleted all files from database");
        } catch (Exception e) {
            logger.error("Error deleting all files from database", e);
            throw new RuntimeException("Failed to delete all files from database", e);
        }
    }
}
