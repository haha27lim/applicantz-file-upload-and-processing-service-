package com.applicantztest.fileupload.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_management")
public class FileManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Filename cannot be blank")
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotBlank(message = "File type cannot be blank")
    @Column(name = "file_type", nullable = false)
    private String fileType;

    @NotNull(message = "File size cannot be null")
    @PositiveOrZero(message = "File size must be positive or zero")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotNull(message = "Line count cannot be null")
    @PositiveOrZero(message = "Line count must be positive or zero")
    @Column(name = "line_count", nullable = false)
    private Integer lineCount;

    @NotNull(message = "Word count cannot be null")
    @PositiveOrZero(message = "Word count must be positive or zero")
    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @NotNull(message = "Processing timestamp cannot be null")
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Processing status cannot be null")
    @Column(name = "status", nullable = false)
    private ProcessingStatus status;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;



    public FileManagement(String filename, String fileType, Long fileSize, Integer lineCount, Integer wordCount) {
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.lineCount = lineCount;
        this.wordCount = wordCount;
        this.status = ProcessingStatus.SUCCESS;
    }

    public FileManagement(String filename, String fileType, Long fileSize, String errorMessage) {
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.lineCount = 0;
        this.wordCount = 0;
        this.errorMessage = errorMessage;
        this.status = ProcessingStatus.FAILED;
    }

}
