package com.example.bookportal.controller;

import com.example.bookportal.dto.RegisterForm;
import com.example.bookportal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterForm form,
                           RedirectAttributes redirectAttributes) {

        try {
            userService.register(form);

            // ✅ Success message (shown once)
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful. Please login."
            );

            // Redirect to login page
            return "redirect:/";

        } catch (Exception e) {

            // ❌ Error message (shown on register page)
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/register";
        }
    }
}
