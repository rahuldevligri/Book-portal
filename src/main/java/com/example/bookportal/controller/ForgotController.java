package com.example.bookportal.controller;

import com.example.bookportal.dto.ForgotFormDTO;
import com.example.bookportal.entity.UserEntity;
import com.example.bookportal.service.ForgotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/forgot")
@RequiredArgsConstructor
public class ForgotController extends BaseController {

    private final ForgotService forgotService;

    /**
     * Displays forgot page
     */
    @GetMapping
    public String forgotPage(Model model) {
        if (!model.containsAttribute("forgotForm")) {
            model.addAttribute("forgotForm", new ForgotFormDTO());
        }
        return "forgot";
    }

    /**
     * Process email and show secret question modal
     */
    @PostMapping
    public String processForgot(
            @Valid @ModelAttribute("forgotForm") ForgotFormDTO form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "forgot";
        }

        String normalizedEmail = forgotService.normalizeEmail(form.getEmail());
        form.setEmail(normalizedEmail);

        UserEntity user = forgotService.findUserByEmail(normalizedEmail);

        if (user == null) {
            bindingResult.rejectValue("email", "forgot.email.notfound", null);
            return "forgot";
        }

        model.addAttribute("forgotForm", form);
        model.addAttribute("verifiedEmail", normalizedEmail);
        model.addAttribute("secretQuestion",
                forgotService.resolveQuestionText(user.getSecretQuestionId()));
        model.addAttribute("showSecretModal", true);

        return "forgot";
    }

    /**
     * Verify secret answer
     */
    @PostMapping("/verify-secret-answer")
    public String verifySecretAnswer(
            @RequestParam String email,
            @RequestParam String secretAnswer,
            Model model) {

        email = forgotService.normalizeEmail(email);

        prepareForgotModel(model, email);

        UserEntity user = forgotService.findUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", forgotService.getEmailInvalidMessage());
            return "forgot";
        }

        model.addAttribute("secretQuestion",
                forgotService.resolveQuestionText(user.getSecretQuestionId()));

        if (!forgotService.verifySecretAnswer(user, secretAnswer)) {
            model.addAttribute("secretError",
                    forgotService.getSecretAnswerErrorMessage(secretAnswer));
            model.addAttribute("showSecretModal", true);
            return "forgot";
        }

        model.addAttribute("showResetModal", true);
        return "forgot";
    }

    /**
     * Reset password
     */
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        email = forgotService.normalizeEmail(email);

        prepareForgotModel(model, email);

        UserEntity user = forgotService.findUserByEmail(email);

        if (user == null) {
            model.addAttribute("error", forgotService.getEmailInvalidMessage());
            return "forgot";
        }

        String newPasswordError = forgotService.validateNewPassword(newPassword);
        String confirmPasswordError =
                forgotService.validateConfirmPassword(confirmPassword, newPassword);

        if (newPasswordError != null || confirmPasswordError != null) {

            if (newPasswordError != null) {
                model.addAttribute("newPasswordError", newPasswordError);
            }

            if (confirmPasswordError != null) {
                model.addAttribute("confirmPasswordError", confirmPasswordError);
            }

            model.addAttribute("showResetModal", true);
            return "forgot";
        }

        try {
            forgotService.resetPassword(email, newPassword);
            return "redirect:/?resetSuccess=true";
        } catch (Exception ex) {
            model.addAttribute("resetError", ex.getMessage());
            model.addAttribute("showResetModal", true);
            return "forgot";
        }
    }

    /**
     * Prepare common model attributes
     */
    private void prepareForgotModel(Model model, String email) {

        ForgotFormDTO form = new ForgotFormDTO();
        form.setEmail(email);

        model.addAttribute("forgotForm", form);
        model.addAttribute("verifiedEmail", email);
    }
}