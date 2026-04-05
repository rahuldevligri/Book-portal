package com.example.bookportal.service;

import com.example.bookportal.entity.UserEntity;

public interface ForgotService {

    UserEntity findUserByEmail(String email);

    String normalizeEmail(String email);

    String resolveQuestionText(Long secretQuestionId);

    boolean verifySecretAnswer(UserEntity user, String secretAnswer);

    String getSecretAnswerErrorMessage(String secretAnswer);

    String getEmailInvalidMessage();

    String validateNewPassword(String newPassword);

    String validateConfirmPassword(String confirmPassword, String newPassword);

    void resetPassword(String email, String newPassword) throws Exception;
}