package com.applicantztest.fileupload.exception;

public class FileManageException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String filename;
    private final String operation;

    
    public FileManageException(String message) {
        super(message);
        this.filename = null;
        this.operation = null;
    }

    public FileManageException(String message, Throwable cause) {
        super(message, cause);
        this.filename = null;
        this.operation = null;
    }

    public FileManageException(String message, String filename, String operation) {
        super(message);
        this.filename = filename;
        this.operation = operation;
    }

    public FileManageException(String message, String filename, String operation, Throwable cause) {
        super(message, cause);
        this.filename = filename;
        this.operation = operation;
    }

    public String getFilename() {
        return filename;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());

        if (filename != null) {
            sb.append(" [File: ").append(filename).append("]");
        }

        if (operation != null) {
            sb.append(" [Operation: ").append(operation).append("]");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "FileManageException{" +
                "message='" + getMessage() + '\'' +
                ", filename='" + filename + '\'' +
                ", operation='" + operation + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}
