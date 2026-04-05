package com.example.bookportal.service;

import com.example.bookportal.dto.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    /**
     * Authenticates a user with the given username and password.
     *
     * @param userName the username
     * @param password the password
     * @return user info DTO if authentication is successful
     */
    UserInfoDTO login(String userName, String password);

    /**
     * Logs out the user associated with the given HTTP request.
     *
     * @param request the HTTP servlet request
     * @return logout status or message
     */
    String doLogout(HttpServletRequest request);
}
