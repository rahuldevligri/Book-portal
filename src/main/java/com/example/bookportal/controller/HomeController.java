package com.example.bookportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the home page.
 * <p>
 * Handles requests to the root URL and displays the main landing page.
 */
@Controller
@RequestMapping("/")
@Validated
public class HomeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Displays the home page.
     * 
     * @param model model to populate view attributes
     * @return view name for home page
     */
    @GetMapping
    public String home(Model model) {
        logger.info("Accessed home page");
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new com.example.bookportal.dto.LoginFormDTO());
        }
        return "index";
    }

    /**
     * Displays the access denied page.
     * 
     * @return view name for access denied
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
