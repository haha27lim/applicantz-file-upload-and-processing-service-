package com.applicantztest.fileupload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.List;

@Configuration
public class DatabaseConfig {

    // for file upload properties
    @ConfigurationProperties(prefix = "app")
    public static class FileUploadProperties {
        
        private List<String> allowedFileTypes;
        private String uploadDir;

        public List<String> getAllowedFileTypes() {
            return allowedFileTypes;
        }

        public void setAllowedFileTypes(List<String> allowedFileTypes) {
            this.allowedFileTypes = allowedFileTypes;
        }

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "app")
    public FileUploadProperties fileUploadProperties() {
        return new FileUploadProperties();
    }

    //for multipart resolver to handle file uploads.
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

}
