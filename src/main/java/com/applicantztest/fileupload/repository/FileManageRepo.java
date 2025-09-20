package com.applicantztest.fileupload.repository;

import com.applicantztest.fileupload.model.FileManagement;
import com.applicantztest.fileupload.model.ProcessingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileManageRepo extends JpaRepository<FileManagement, Long> {

    List<FileManagement> findByFilename(String filename);

    List<FileManagement> findByFileType(String fileType);

    List<FileManagement> findByStatus(String status);

    List<FileManagement> findByProcessedAtAfter(LocalDateTime dateTime);

    List<FileManagement> findByProcessedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<FileManagement> findFirstByFilenameOrderByProcessedAtDesc(String filename);

    List<FileManagement> findByStatusOrderByProcessedAtDesc(String status);

    @Query("SELECT COUNT(f) FROM FileManagement f WHERE f.status = 'SUCCESS'")
    long countSuccessfulProcessing();

    @Query("SELECT COUNT(f) FROM FileManagement f WHERE f.status = 'FAILED'")
    long countFailedProcessing();

    @Query("SELECT COALESCE(SUM(f.lineCount), 0) FROM FileManagement f WHERE f.status = 'SUCCESS'")
    long getTotalLinesProcessed();

    @Query("SELECT COALESCE(SUM(f.wordCount), 0) FROM FileManagement f WHERE f.status = 'SUCCESS'")
    long getTotalWordsProcessed();

    @Query("SELECT f FROM FileManagement f WHERE f.lineCount > :lineCount AND f.status = 'SUCCESS'")
    List<FileManagement> findFilesWithLineCountGreaterThan(@Param("lineCount") Integer lineCount);

    @Query("SELECT f FROM FileManagement f WHERE f.wordCount > :wordCount AND f.status = 'SUCCESS'")
    List<FileManagement> findFilesWithWordCountGreaterThan(@Param("wordCount") Integer wordCount);

    @Query("DELETE FROM FileManagement f WHERE f.processedAt < :date AND f.status = :status")
    void deleteByProcessedAtBeforeAndStatus(@Param("date") LocalDateTime date,
            @Param("status") ProcessingStatus status);

    Optional<FileManagement> findByFilenameAndFileSizeAndStatus(String filename, long fileSize,
            ProcessingStatus status);

}
