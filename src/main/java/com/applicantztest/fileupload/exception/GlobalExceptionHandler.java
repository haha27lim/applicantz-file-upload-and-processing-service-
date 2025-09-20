package com.applicantztest.fileupload.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileValidationException.class)
    public String handleFileValidationException(FileValidationException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.warn("File validation failed: {}", ex.getDetailedMessage(), ex);

        logRequestContext(request, "FileValidationException");

        // Add error message
        String userMessage = "File validation failed: " + ex.getMessage();
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/";
    }

    @ExceptionHandler(FileManageException.class)
    public String handleFileProcessingException(FileManageException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.error("File processing failed: {}", ex.getDetailedMessage(), ex);

        logRequestContext(request, "FileProcessingException");

        // Add error message
        String userMessage = "File processing failed: " + ex.getMessage();
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.warn("File upload size exceeded: {}", ex.getMessage());

        logRequestContext(request, "MaxUploadSizeExceededException");

        // Add error message
        String userMessage = "File size exceeds the maximum limit of 10MB. Please choose a smaller file.";
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.warn("Invalid argument provided: {}", ex.getMessage(), ex);

        logRequestContext(request, "IllegalArgumentException");

        // Add error message
        String userMessage = "Invalid input provided. Please check your file and try again.";
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);

        logRequestContext(request, "RuntimeException");

        // Add error message
        String userMessage = "An unexpected error occurred. Please try again later.";
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        logRequestContext(request, "Exception");

        // Add error message
        String userMessage = "An unexpected system error occurred. Please contact support if the problem persists.";
        redirectAttributes.addFlashAttribute("error", userMessage);

        return "redirect:/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex)
            throws NoResourceFoundException {
        if (ex.getMessage().contains("favicon.ico") || ex.getMessage().contains(".well-known")) {
            return ResponseEntity.notFound().build();
        }
        throw ex;
    }

    private void logRequestContext(HttpServletRequest request, String exceptionType) {
        logger.debug("Exception context - Type: {}, URI: {}, Method: {}, User-Agent: {}, Remote-Addr: {}",
                exceptionType,
                request.getRequestURI(),
                request.getMethod(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr());
    }

    private Map<String, Object> createErrorResponse(HttpStatus status, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
}
