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


}
