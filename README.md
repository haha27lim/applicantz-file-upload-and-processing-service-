# File Upload and Processing Service

A Spring Boot application that allows users for uploading '.txt' and '.csv' files, validating, processing, storing files to an 'in-memory database'.

## Features
- File Upload: Browse file selection or drag&drop
- File Validation: Supports .text and .csv files (max 10MB) and reject duplicates
- File Processing: Counts lines and words in uploaded files
- Database storage: Saves results to H2 in-memory database (simulated database)
- Processing History: View all processed files history
- Statistics: Dashboard show processed statistics

--

## Usage Guide
1. Open the upload page by navigating to http://localhost:8080
2. - Click "Browse Files" button, or
   - Drag and drop a file into the upload area
3. **Supported Formats**: .txt and .csv files (max 10MB)
4. Click "Upload and Process" button
5. See processing results with line and word counts
6. Duplicate files are rejected with a clear error message.
7. Click 'View History' to see all processed files
8. Under history page, able to download or delete selected file.
