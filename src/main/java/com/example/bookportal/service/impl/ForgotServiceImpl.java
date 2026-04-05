package com.example.bookportal.service.impl;

import com.example.bookportal.entity.UserEntity;
import com.example.bookportal.repository.UserRepository;
import com.example.bookportal.service.ForgotService;
import com.example.bookportal.service.SecretQuestionService;
import com.example.bookportal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotServiceImpl implements ForgotService {

    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

    private final UserRepository userRepository;
    private final SecretQuestionService secretQuestionService;
    private final UserService userService;
    private final MessageSource messageSource;

    @Override
    public UserEntity findUserByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);
        return userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
    }

    @Override
    public String normalizeEmail(String email) {
        return email == null ? null : email.trim();
    }

    @Override
    public String resolveQuestionText(Long secretQuestionId) {
        return secretQuestionService.findById(secretQuestionId)
                .map(q -> messageSource.getMessage(
                        q.getQuestion(),
                        null,
                        LocaleContextHolder.getLocale()))
                .orElse(messageSource.getMessage(
                        "secret.question.notfound",
                        null,
                        LocaleContextHolder.getLocale()));
    }

    @Override
    public boolean verifySecretAnswer(UserEntity user, String secretAnswer) {

        if (secretAnswer == null || secretAnswer.trim().isEmpty()) {
            return false;
        }

        String expected =
                user.getSecretAnswer() == null ? "" : user.getSecretAnswer().trim();

        String provided = secretAnswer.trim();

        return expected.equalsIgnoreCase(provided);
    }

    @Override
    public String getSecretAnswerErrorMessage(String secretAnswer) {

        if (secretAnswer == null || secretAnswer.trim().isEmpty()) {
            return messageSource.getMessage(
                    "NotBlank.secretAnswer",
                    null,
                    LocaleContextHolder.getLocale());
        }

        return messageSource.getMessage(
                "Invalid.secretAnswer",
                null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public String getEmailInvalidMessage() {
        return messageSource.getMessage(
                "forgot.email.invalid",
                null,
                LocaleContextHolder.getLocale());
    }

    @Override
    public String validateNewPassword(String newPassword) {

        var locale = LocaleContextHolder.getLocale();

        if (newPassword == null || newPassword.isBlank()) {
            return messageSource.getMessage("NotBlank.newPassword", null, locale);
        }

        if (!newPassword.matches(PASSWORD_REGEX)) {
            return messageSource.getMessage("password.rule", null, locale);
        }

        return null;
    }

    @Override
    public String validateConfirmPassword(String confirmPassword, String newPassword) {

        var locale = LocaleContextHolder.getLocale();

        if (confirmPassword == null || confirmPassword.isBlank()) {
            return messageSource.getMessage(
                    "NotBlank.confirmPassword",
                    null,
                    locale);
        }

        if (newPassword != null &&
                !newPassword.isBlank() &&
                !confirmPassword.equals(newPassword)) {

            return messageSource.getMessage(
                    "password.mismatch",
                    null,
                    locale);
        }

        return null;
    }

    @Override
    public void resetPassword(String email, String newPassword) throws Exception {
        userService.resetPasswordByEmail(email, newPassword);
    }
}