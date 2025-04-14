package com.kaydev.appstore.utils.validators.requiredif;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredIfValidator.class)
public @interface RequiredIf {
    String message() default "Dependent value is required when trigger is present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String trigger();

    String required();

    String match();
}
