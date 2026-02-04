package com.example.bookportal.controller;

import com.example.bookportal.dto.ChangePasswordForm;
import com.example.bookportal.dto.EditProfileForm;
import com.example.bookportal.entity.User;
import com.example.bookportal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {

        model.addAttribute("username", auth.getName());
        model.addAttribute(
                "today",
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
        );

        return "dashboard";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            Authentication auth,
            ChangePasswordForm form,
            HttpServletRequest request) {

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        userService.changePassword(auth.getName(), form);

        // ✅ LOGOUT USER AFTER PASSWORD CHANGE
        request.getSession().invalidate();

        return "redirect:/?passwordChanged";
    }

    @GetMapping("/user-options")
    public String userOptions(Authentication auth, Model model) {

        model.addAttribute("username", auth.getName());
        model.addAttribute("today",
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        return "user-options";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Principal principal) {

        User user = userService.findByUsername(principal.getName());

        EditProfileForm form = new EditProfileForm();
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        form.setSecretQuestion(user.getSecretQuestion());
        form.setSecretAnswer(user.getSecretAnswer());

        // split full name safely
        if (user.getFullName() != null) {
            String[] parts = user.getFullName().split(" ", 2);
            form.setFirstName(parts[0]);
            if (parts.length > 1) {
                form.setLastName(parts[1]);
            }
        }

        model.addAttribute("editProfileForm", form);
        return "edit-profile";
    }

    @PostMapping("/edit-profile")
    public String updateProfile(
            @ModelAttribute EditProfileForm form,
            RedirectAttributes redirectAttributes) {

        userService.updateProfileFromForm(form);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        return "redirect:/edit-profile";
    }

}

