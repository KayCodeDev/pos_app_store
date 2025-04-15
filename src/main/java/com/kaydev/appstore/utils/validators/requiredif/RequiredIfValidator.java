package com.kaydev.appstore.utils.validators.requiredif;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredIfValidator implements ConstraintValidator<RequiredIf, Object> {

    private String trigger;
    private String required;
    private String match;

    @Override
    public void initialize(RequiredIf constraintAnnotation) {
        this.trigger = constraintAnnotation.trigger();
        this.required = constraintAnnotation.required();
        this.match = constraintAnnotation.match();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object triggerValue = getValue(value, trigger);
        Object requiredValue = getValue(value, required);

        // Add your validation logic here
        if (triggerValue != null && triggerValue.equals(match)) {
            return requiredValue != null;
        }

        return true;
    }

    private Object getValue(Object value, String fieldName) {

        try {
            return value.getClass().getMethod("get" + capitalize(fieldName)).invoke(value);
        } catch (Exception e) {
            return null;
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}