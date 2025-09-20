package com.applicantztest.fileupload.service;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.applicantztest.fileupload.config.DatabaseConfig;
import com.applicantztest.fileupload.config.DatabaseConfig.FileUploadProperties;
import com.applicantztest.fileupload.dto.ValidationResult;
import com.applicantztest.fileupload.repository.FileManageRepo;
import com.applicantztest.fileupload.model.ProcessingStatus;


@Service
public class FileValidationService {

    private static final Logger logger = LoggerFactory.getLogger(FileValidationService.class);

    private final DatabaseConfig.FileUploadProperties fileUploadProperties;
    private final FileManageRepo repository;

    // Allowed file types
    private static final List<String> ALLOWED_TYPES = Arrays.asList("txt", "csv");

    // Maximum file size in bytes (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileValidationService(FileUploadProperties fileUploadProperties, FileManageRepo repository) {
        this.fileUploadProperties = fileUploadProperties;
        this.repository = repository;
    }

    public boolean isAllowedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("File validation failed: File is null or empty");
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            logger.warn("File validation failed: Filename is null or empty");
            return false;
        }

        // Get file extension
        String fileExtension = FilenameUtils.getExtension(filename).toLowerCase();

        if (fileExtension.isEmpty()) {
            logger.warn("File validation failed: No file extension found for file: {}", filename);
            return false;
        }

        // Get allowed file types from properties
        List<String> allowedTypes = fileUploadProperties.getAllowedFileTypes();
        if (allowedTypes == null || allowedTypes.isEmpty()) {
            allowedTypes = ALLOWED_TYPES;
            logger.info("Using allowed file types: {}", allowedTypes);
        }

        boolean isAllowed = allowedTypes.contains(fileExtension);

        if (isAllowed) {
            logger.info("File validation successful: {} with extension '{}' is allowed", filename, fileExtension);
        } else {
            logger.warn("File validation failed: {} with extension '{}' is not allowed. Allowed types: {}",
                    filename, fileExtension, allowedTypes);
        }

        return isAllowed;
    }

    public boolean isValidFileSize(MultipartFile file) {
        if (file == null) {
            logger.warn("File size validation failed: File is null");
            return false;
        }

        long fileSize = file.getSize();
        boolean isValid = fileSize > 0 && fileSize <= MAX_FILE_SIZE;

        if (isValid) {
            logger.info("File size validation successful: {} bytes (limit: {} bytes)", fileSize, MAX_FILE_SIZE);
        } else {
            logger.warn("File size validation failed: {} bytes exceeds limit of {} bytes", fileSize, MAX_FILE_SIZE);
        }

        return isValid;
    }

    public boolean isValidFileContent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("File content validation failed: File is null or empty");
            return false;
        }

        try {

            byte[] content = file.getBytes();

            if (content.length == 0) {
                logger.warn("File content validation failed: File content is empty");
                return false;
            }

            // Check contains valid text characters
            String filename = file.getOriginalFilename();
            String fileExtension = FilenameUtils.getExtension(filename).toLowerCase();

            if ("txt".equals(fileExtension) || "csv".equals(fileExtension)) {

                String contentStr = new String(content);
                long nonPrintableCount = contentStr.chars()
                        .filter(c -> c < 32 && c != 9 && c != 10 && c != 13)
                        .count();

                // If more than 10% of characters are non-printable, consider it suspicious
                double nonPrintableRatio = (double) nonPrintableCount / contentStr.length();
                if (nonPrintableRatio > 0.1) {
                    logger.warn(
                            "File content validation failed: High ratio of non-printable characters ({}%) in text file: {}",
                            nonPrintableRatio * 100, filename);
                    return false;
                }
            }

            logger.info("File content validation successful for file: {}", filename);
            return true;

        } catch (Exception e) {
            logger.error("File content validation failed due to exception for file: {}",
                    file.getOriginalFilename(), e);
            return false;
        }
    }

    public ValidationResult validateFile(MultipartFile file) {
        logger.info("Starting comprehensive file validation for file: {}",
                file != null ? file.getOriginalFilename() : "null");

        ValidationResult result = new ValidationResult();

        if (!isAllowedFile(file)) {
            result.setValid(false);
            result.addError("File type not allowed. Only .txt and .csv files are accepted.");
            return result;
        }

        if (!isValidFileSize(file)) {
            result.setValid(false);
            result.addError("File size exceeds the maximum limit of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB.");
            return result;
        }

        if (!isValidFileContent(file)) {
            result.setValid(false);
            result.addError("File content appears to be invalid or corrupted.");
            return result;
        }

        if (file != null && repository.findByFilenameAndFileSizeAndStatus(file.getOriginalFilename(), file.getSize(),
                ProcessingStatus.SUCCESS).isPresent()) {
            result.setValid(false);
            result.addError("A file with the same name and size has already been successfully processed.");
            return result;
        }

        result.setValid(true);
        logger.info("File validation completed successfully for file: {}", 
                file != null ? file.getOriginalFilename() : "null");
        return result;
    }
}
