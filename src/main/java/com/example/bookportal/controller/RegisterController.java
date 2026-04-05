package com.example.bookportal.controller;

import com.example.bookportal.dto.RegisterFormDTO;
import com.example.bookportal.exception.ValidationException;
import com.example.bookportal.service.SecretQuestionService;
import com.example.bookportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for user registration operations.
 * <p>
 * Handles registration form display, validation, and user creation.
 */
@Controller
@RequestMapping("/register")
public class RegisterController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private SecretQuestionService secretQuestionService;
    @Autowired
    private MessageSource messageSource;

    /**
     * Displays the user registration page.
     * 
     * @param model model to populate view attributes
     * @return view name for registration page
     */
    @GetMapping
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterFormDTO());
        model.addAttribute("secretQuestions", secretQuestionService.findAll());
        logger.info("Navigated to register page");
        return "register";
    }

    /**
     * Handles the submission of the registration form.
     * 
     * @param form          registration form data
     * @param bindingResult validation result
     * @param model         model to populate view attributes
     * @return redirect or registration form view
     */
    @PostMapping
    public String register(@Valid @ModelAttribute("registerForm") RegisterFormDTO form,
            BindingResult bindingResult,
            Model model) {
        logger.info("Register attempt: username={}, email={}", form.getUsername(), form.getEmail());
        if (bindingResult.hasErrors()) {
            logger.warn("Validation failed: {}", bindingResult.getAllErrors());
            model.addAttribute("registerForm", form);
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("secretQuestions", secretQuestionService.findAll());
            return "register";
        }
        try {
            userService.register(form);
            logger.info("Registration successful for username={}", form.getUsername());
            return "redirect:/?registered=true";
        } catch (ValidationException ex) {
            logger.warn("Registration validation failed: {}", ex.getMessage());
            model.addAttribute("registerForm", form);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("secretQuestions", secretQuestionService.findAll());
            return "register";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            logger.warn("Duplicate registration attempt: {}", ex.getMessage());
            model.addAttribute("registerForm", form);
            model.addAttribute("error",
                    messageSource.getMessage("register.error", null, LocaleContextHolder.getLocale()));
            model.addAttribute("secretQuestions", secretQuestionService.findAll());
            return "register";
        } catch (Exception ex) {
            logger.error("Registration failed: {}", ex.getMessage(), ex);
            model.addAttribute("registerForm", form);
            model.addAttribute("error",
                    messageSource.getMessage("register.error", null, LocaleContextHolder.getLocale()));
            model.addAttribute("secretQuestions", secretQuestionService.findAll());
            return "register";
        }
    }
}
