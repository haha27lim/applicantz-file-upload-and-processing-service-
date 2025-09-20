package com.applicantztest.fileupload.exception;

public class FileValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String filename;
    private final String validationType;
    private final String expectedValue;
    private final String actualValue;

    public FileValidationException(String message) {
        super(message);
        this.filename = null;
        this.validationType = null;
        this.expectedValue = null;
        this.actualValue = null;
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
        this.filename = null;
        this.validationType = null;
        this.expectedValue = null;
        this.actualValue = null;
    }

    public FileValidationException(String message, String filename, String validationType) {
        super(message);
        this.filename = filename;
        this.validationType = validationType;
        this.expectedValue = null;
        this.actualValue = null;
    }

    public FileValidationException(String message, String filename, String validationType,
            String expectedValue, String actualValue) {
        super(message);
        this.filename = filename;
        this.validationType = validationType;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }

    public String getFilename() {
        return filename;
    }

    public String getValidationType() {
        return validationType;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());

        if (filename != null) {
            sb.append(" [File: ").append(filename).append("]");
        }

        if (validationType != null) {
            sb.append(" [Validation: ").append(validationType).append("]");
        }

        if (expectedValue != null && actualValue != null) {
            sb.append(" [Expected: ").append(expectedValue)
                    .append(", Actual: ").append(actualValue).append("]");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "FileValidationException{" +
                "message='" + getMessage() + '\'' +
                ", filename='" + filename + '\'' +
                ", validationType='" + validationType + '\'' +
                ", expectedValue='" + expectedValue + '\'' +
                ", actualValue='" + actualValue + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}
