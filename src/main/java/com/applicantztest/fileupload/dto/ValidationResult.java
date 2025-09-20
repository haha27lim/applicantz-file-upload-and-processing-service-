package com.applicantztest.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    private boolean valid = true;
    private StringBuilder errors = new StringBuilder();


    public String getErrors() {
        return errors.toString();
    }

    public void addError(String error) {
        if (errors.length() > 0) {
            errors.append("; ");
        }
        errors.append(error);
    }

    public boolean hasErrors() {
        return errors.length() > 0;
    }

}
