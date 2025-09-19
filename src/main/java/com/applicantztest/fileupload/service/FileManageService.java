package com.applicantztest.fileupload.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.applicantztest.fileupload.repository.FileManageRepo;

@Service
public class FileManageService {

    private static final Logger logger = LoggerFactory.getLogger(FileManageService.class);

    private final FileManageRepo fileManageRepo;

    private static final Pattern WORD_PATTERN = Pattern.compile("\\s+");

    public FileManageService(FileManageRepo fileManageRepo) {
        this.fileManageRepo = fileManageRepo;
    }
}
