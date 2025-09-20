package com.applicantztest.fileupload.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.applicantztest.fileupload.dto.ProcessingStat;
import com.applicantztest.fileupload.dto.ValidationResult;
import com.applicantztest.fileupload.model.FileManagement;
import com.applicantztest.fileupload.repository.FileManageRepo;
import com.applicantztest.fileupload.service.FileManageService;
import com.applicantztest.fileupload.service.FileValidationService;

@Controller
@RequestMapping("/")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileManageService fileManageService;
    private final FileValidationService fileValidationService;
    private final FileManageRepo repository;

    public FileUploadController(FileManageService fileManageService, FileValidationService fileValidationService,
            FileManageRepo repository) {
        this.fileManageService = fileManageService;
        this.fileValidationService = fileValidationService;
        this.repository = repository;
    }

    @GetMapping("/")
    public String showUploadForm(Model model) {
        logger.info("Displaying file upload form");

        // Add statistics
        ProcessingStat stats = fileManageService.getProcessingStatistics();
        model.addAttribute("statistics", stats);

        return "upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        logger.info("Received file upload request for file: {}",
                file != null ? file.getOriginalFilename() : "null");

        try {
            // Validate the uploaded file
            ValidationResult validationResult = fileValidationService.validateFile(file);

            if (!validationResult.isValid()) {
                logger.warn("File validation failed: {}", validationResult.getErrors());
                redirectAttributes.addFlashAttribute("error",
                        "File validation failed: " + validationResult.getErrors());
                return "redirect:/";
            }

            // Process and save the file
            FileManagement result = fileManageService.processAndSaveFile(file);

            logger.info("File processing completed for: {}. Result ID: {}", file.getOriginalFilename(), result.getId());

            // Add result
            redirectAttributes.addFlashAttribute("result", result);
            redirectAttributes.addFlashAttribute("success", "File processed successfully!");

            return "redirect:/result/" + result.getId();

        } catch (Exception e) {
            logger.error("Error occurred during file upload processing", e);
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred while processing the file: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/result/{id}")
    public String showResult(@PathVariable Long id, Model model) {
        logger.info("Displaying result for processing ID: {}", id);

        try {
            Optional<FileManagement> resultOptional = repository.findById(id);

            if (resultOptional.isPresent()) {
                FileManagement result = resultOptional.get();
                model.addAttribute("result", result);
                logger.info("Retrieved processing result for ID: {}", id);
                return "result";
            } else {
                logger.warn("Processing result not found for ID: {}", id);
                model.addAttribute("error", "Processing result not found for ID: " + id);
                return "error";
            }

        } catch (Exception e) {
            logger.error("Error retrieving processing result for ID: {}", id, e);
            model.addAttribute("error", "Could not retrieve processing result: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        logger.info("Displaying processing history");

        try {
            // Get all results
            List<FileManagement> allResults = repository.findAll();
            model.addAttribute("results", allResults);

            // Get processing statistics
            ProcessingStat stats = fileManageService.getProcessingStatistics();
            model.addAttribute("statistics", stats);

            return "history";

        } catch (Exception e) {
            logger.error("Error retrieving processing history", e);
            model.addAttribute("error", "Could not retrieve processing history");
            return "error";
        }
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ProcessingStat getStatistics() {
        logger.info("API request for processing statistics");
        return fileManageService.getProcessingStatistics();
    }

    @PostMapping("/delete/{id}")
    public String deleteFile(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Received delete request for file ID: {}", id);

        try {
            boolean deleted = fileManageService.deleteFileFromDatabase(id);

            if (deleted) {
                logger.info("Successfully deleted file with ID: {}", id);
                redirectAttributes.addFlashAttribute("success", "File deleted successfully");
            } else {
                logger.warn("File with ID {} not found", id);
                redirectAttributes.addFlashAttribute("error", "File not found");
            }

        } catch (Exception e) {
            logger.error("Error deleting file with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Error deleting file: " + e.getMessage());
        }

        return "redirect:/history";
    }

    @PostMapping("/delete-all")
    public String deleteAllFiles(RedirectAttributes redirectAttributes) {
        logger.info("Received request to delete all files");

        try {
            fileManageService.deleteAll();
            logger.info("Successfully deleted all files");
            redirectAttributes.addFlashAttribute("success", "All files deleted successfully");

        } catch (Exception e) {
            logger.error("Error deleting all files", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting files: " + e.getMessage());
        }

        return "redirect:/history";
    }

    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, Model model) {
        logger.error("Unhandled exception in controller", e);
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }
}
