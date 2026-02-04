package com.example.bookportal.controller;

import com.example.bookportal.dto.ForgotForm;
import com.example.bookportal.entity.User;
import com.example.bookportal.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ForgotController {

    private final UserRepository userRepository;

    public ForgotController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // STEP 1: Show Forgot Page
    @GetMapping("/forgot")
    public String forgotPage(Model model) {
        model.addAttribute("forgotForm", new ForgotForm());
        return "forgot";   // old forgot.html
    }

    @PostMapping("/forgot")
    public String processForgot(ForgotForm form, Model model) {

        User user = userRepository.findByEmail(form.getEmail()).orElse(null);

        if (user == null) {
            model.addAttribute("error", "No account found with this email");
            return "forgot";
        }

        // IMPORTANT: send full user
        model.addAttribute("user", user);

        // old-style login info page
        return "login-info";
    }
}
