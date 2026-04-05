package com.example.bookportal.validation;

import com.example.bookportal.dto.ChangePasswordFormDTO;
import com.example.bookportal.dto.RegisterFormDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link PasswordMatches} annotation.
 * Ensures password and confirmation fields match for supported DTOs.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    /**
     * Validates that password and confirmation fields match across supported forms.
     *
     * @param obj     the object to validate (RegisterFormDTO or
     *                ChangePasswordFormDTO)
     * @param context validation context
     * @return true if passwords match, false otherwise
     */
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegisterFormDTO registerForm) {
            return validateMatch(registerForm.getPassword(), registerForm.getConfirmPassword(), "confirmPassword",
                    context);
        }
        if (obj instanceof ChangePasswordFormDTO changePasswordForm) {
            return validateMatch(changePasswordForm.getNewPassword(), changePasswordForm.getConfirmPassword(),
                    "confirmPassword", context);
        }
        return true;
    }

    /**
     * Helper method to check if two password fields match and add constraint
     * violation if not.
     *
     * @param value     the password value
     * @param confirm   the confirmation value
     * @param fieldName the field name for reporting violation
     * @param context   validation context
     * @return true if values match or are null, false otherwise
     */
    private boolean validateMatch(String value,
            String confirm,
            String fieldName,
            ConstraintValidatorContext context) {
        // Let @NotBlank/@Size constraints report missing values.
        if (value == null || confirm == null) {
            return true;
        }
        if (value.equals(confirm)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldName)
                .addConstraintViolation();
        return false;
    }
}
