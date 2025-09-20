package com.applicantztest.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResult {

    private int lineCount;
    private int wordCount;
    private String errorMessage;

    public ProcessingResult(int lineCount, int wordCount) {
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.errorMessage = null;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }

}
