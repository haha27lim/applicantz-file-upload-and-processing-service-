package com.applicantztest.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingStat {

    private long totalFiles;
    private long successfulFiles;
    private long failedFiles;
    private long totalLines;
    private long totalWords;

}
