package com.kaydev.appstore.utils.validators.existin;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistInValidator implements ConstraintValidator<ExistIn, String> {

    private List<String> values;

    @Override
    public void initialize(ExistIn constraintAnnotation) {
        values = Arrays.asList(constraintAnnotation.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || values.contains(value);
    }
}