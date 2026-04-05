package com.example.bookportal.controller;

import com.example.bookportal.dto.UserInfoDTO;
import com.example.bookportal.dto.LoginFormDTO;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import com.example.bookportal.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for authentication operations.
 * <p>
 * Handles login, logout, and authentication-related endpoints.
 */
@Controller
public class AuthController extends BaseController {

    /**
     * Service for authentication operations.
     */
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Handles user login form submission.
     * 
     * @param loginForm     login form data
     * @param bindingResult validation result
     * @param model         model to populate view attributes
     * @param session       HTTP session
     * @return redirect or login page view
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginFormDTO loginForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginForm", loginForm);
            return "index";
        }
        try {
            UserInfoDTO userInfo = authenticationService.login(loginForm.getUsername(), loginForm.getPassword());
            request.changeSessionId();
            session.setAttribute("userInfo", userInfo);
            return "redirect:/dashboard";
        } catch (Exception ex) {
            model.addAttribute("loginForm", loginForm);
            model.addAttribute("error", true);
            return "index";
        }
    }

    /**
     * Displays the login page.
     * 
     * @param model model to populate view attributes
     * @return view name for login page
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginFormDTO());
        return "index";
    }

    /**
     * Handles user logout (POST).
     * 
     * @param request HTTP request
     * @return redirect or logout view
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        return authenticationService.doLogout(request);
    }

    /**
     * Handles user logout (GET).
     * 
     * @param request HTTP request
     * @return redirect or logout view
     */
    @GetMapping("/logout")
    public String logoutGet(HttpServletRequest request) {
        return "redirect:/";
    }
}
