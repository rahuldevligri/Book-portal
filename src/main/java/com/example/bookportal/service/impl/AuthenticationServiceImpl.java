package com.example.bookportal.service.impl;

import com.example.bookportal.dto.UserInfoDTO;
import com.example.bookportal.entity.UserEntity;
import com.example.bookportal.repository.UserRepository;
import com.example.bookportal.repository.ApiAuthorizationRepository;
import com.example.bookportal.security.JwtUtil;
import com.example.bookportal.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ApiAuthorizationRepository apiAuthorizationRepository;

    /**
     * Authenticates a user with the given username and password.
     *
     * @param userName the username
     * @param password the password
     * @return user info DTO if authentication is successful
     */
    @Override
    public UserInfoDTO login(String userName, String password) {
        String normalizedUserName = userName == null ? null : userName.trim();
        UserEntity user = userRepository.findByUserNameIgnoreCase(normalizedUserName)
                .orElseThrow(() -> new RuntimeException("UserEntity not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        try {
            Set<String> authorizedApiUrls = apiAuthorizationRepository.findApiUrlsByUserId(user.getId());
            String token = jwtUtil.generateToken(user.getId());

            UserInfoDTO userInfo = new UserInfoDTO();
            userInfo.setToken(token);
            userInfo.setId(user.getId());
            userInfo.setUserName(user.getUserName());
            userInfo.setFirstName(user.getFirstName());
            userInfo.setLastName(user.getLastName());
            userInfo.setUserTypeId(user.getUserTypeId());
            userInfo.setAuthorizedApiUrls(authorizedApiUrls);
            return userInfo;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to build authenticated session data", ex);
        }
    }

    /**
     * Logs out the user associated with the given HTTP request.
     *
     * @param request the HTTP servlet request
     * @return logout status or message
     */
    @Override
    public String doLogout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
