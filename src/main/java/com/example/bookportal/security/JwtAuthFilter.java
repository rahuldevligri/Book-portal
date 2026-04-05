package com.example.bookportal.security;

import com.example.bookportal.dto.UserInfoDTO;
import com.example.bookportal.entity.UserEntity;
import com.example.bookportal.repository.ApiAuthorizationRepository;
import com.example.bookportal.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ApiAuthorizationRepository apiAuthorizationRepository;

    /**
     * Constructs a JwtAuthFilter with the provided dependencies.
     * 
     * @param jwtUtil                    the JwtUtil instance
     * @param userRepository             the UserRepository instance
     * @param apiAuthorizationRepository the ApiAuthorizationRepository instance
     */
    public JwtAuthFilter(JwtUtil jwtUtil,
            UserRepository userRepository,
            ApiAuthorizationRepository apiAuthorizationRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.apiAuthorizationRepository = apiAuthorizationRepository;
    }

    /**
     * Filters incoming requests to handle JWT authentication and authorization.
     * 
     * @param request     the HttpServletRequest
     * @param response    the HttpServletResponse
     * @param filterChain the FilterChain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        Object userInfoAttr = session != null ? session.getAttribute("userInfo") : null;
        UserInfoDTO userInfo = userInfoAttr instanceof UserInfoDTO ? (UserInfoDTO) userInfoAttr : null;

        if (userInfo != null) {
            Set<String> allowedUrls = userInfo.getAuthorizedApiUrls();
            if (!isAlwaysAllowedForAuthenticatedUser(path) && !isAllowedPath(path, allowedUrls)) {
                rejectAccess(request, response);
                return;
            }
            setAuthentication(userInfo, request);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                UserEntity user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return;
                }

                Set<String> allowedUrls = apiAuthorizationRepository.findApiUrlsByUserId(userId);
                if (!isAlwaysAllowedForAuthenticatedUser(path) && !isAllowedPath(path, allowedUrls)) {
                    rejectAccess(request, response);
                    return;
                }

                UserInfoDTO bearerUser = new UserInfoDTO();
                bearerUser.setId(user.getId());
                bearerUser.setUserName(user.getUserName());
                bearerUser.setFirstName(user.getFirstName());
                bearerUser.setLastName(user.getLastName());
                bearerUser.setUserTypeId(user.getUserTypeId());
                bearerUser.setAuthorizedApiUrls(allowedUrls);
                bearerUser.setToken(token);
                setAuthentication(bearerUser, request);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Sets the authentication in the SecurityContext based on the provided user
     * info.
     * 
     * @param userInfo the UserInfoDTO
     * @param request  the HttpServletRequest
     */
    private void setAuthentication(UserInfoDTO userInfo, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (userInfo.getAuthorizedApiUrls() != null) {
            for (String allowedUrl : userInfo.getAuthorizedApiUrls()) {
                if (allowedUrl != null && !allowedUrl.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("URL:" + allowedUrl));
                }
            }
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userInfo, null,
                authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Handles access rejection for unauthorized requests.
     * 
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    private void rejectAccess(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getServletPath().startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        response.sendRedirect("/access-denied");
    }

    /**
     * Checks if the given path is allowed based on the authorized URLs.
     * 
     * @param path        the request path
     * @param allowedUrls the set of allowed URLs
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedPath(String path, Set<String> allowedUrls) {
        if (allowedUrls == null || allowedUrls.isEmpty()) {
            return false;
        }
        return allowedUrls.stream().anyMatch(allowed -> {
            if (allowed.endsWith("/**")) {
                return path.startsWith(allowed.substring(0, allowed.length() - 3));
            }
            return path.equals(allowed);
        });
    }

    private boolean isAlwaysAllowedForAuthenticatedUser(String path) {
        return path.equals("/dashboard")
                || path.startsWith("/dashboard/")
                || path.equals("/user-options")
                || path.equals("/change-password")
                || path.equals("/edit-profile");
    }

    /**
     * Determines if the filter should not be applied to the given request.
     * 
     * @param request the HttpServletRequest
     * @return true if the filter should not be applied, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/")
            || path.equals("/login")
            || path.equals("/logout")
            || path.equals("/register")
            || path.equals("/forgot")
            || path.equals("/forgot/verify-secret-answer")
            || path.equals("/forgot/reset-password")
            || path.equals("/access-denied")
            || path.startsWith("/css/")
            || path.startsWith("/js/")
            || path.startsWith("/images/")
            || path.endsWith(".js")
            || path.endsWith(".ico")
            || path.endsWith(".css")
            || path.endsWith(".png")
            || path.endsWith(".jpg")
            || path.endsWith(".gif");
    }
}
