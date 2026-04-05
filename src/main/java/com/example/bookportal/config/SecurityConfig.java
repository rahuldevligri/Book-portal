package com.example.bookportal.config;

import com.example.bookportal.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        private static final PathPatternRequestMatcher API_PATH_MATCHER = PathPatternRequestMatcher.pathPattern("/api/**");

        private final JwtAuthFilter jwtAuthFilter;

        /**
         * Constructs a SecurityConfig with the provided JwtAuthFilter.
         * 
         * @param jwtAuthFilter the JwtAuthFilter to use
         */
        public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
        }

        /**
         * Configures the security filter chain for HTTP requests.
         * 
         * @param http the HttpSecurity object
         * @return the configured SecurityFilterChain
         * @throws Exception if an error occurs during configuration
         */
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers(API_PATH_MATCHER))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/logout",
                                                                "/register",
                                                                "/forgot",
                                                                "/forgot/verify-secret-answer",
                                                                "/forgot/reset-password",
                                                                "/access-denied",
                                                                "/error",
                                                                "/favicon.ico",
                                                                "/**/*.css",
                                                                "/**/*.js",
                                                                "/**/*.ico",
                                                                "/**/*.png",
                                                                "/**/*.jpg",
                                                                "/**/*.gif",
                                                                "/style.css",
                                                                "/images/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                                                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false))
                                .exceptionHandling(ex -> ex
                                                .defaultAuthenticationEntryPointFor(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                                                API_PATH_MATCHER)
                                                .authenticationEntryPoint((request, response, authException) -> response
                                                                .sendRedirect("/"))
                                                .defaultAccessDeniedHandlerFor(apiAccessDeniedHandler(),
                                                                API_PATH_MATCHER)
                                                .accessDeniedHandler((request, response, accessDeniedException) -> response
                                                                .sendRedirect("/access-denied")))
                                .rememberMe(rm -> rm
                                                .key("book-portal-remember-me-key")
                                                .tokenValiditySeconds(1800) // 30 minutes
                                                .rememberMeParameter("remember-me"))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID", "remember-me"))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        AccessDeniedHandler apiAccessDeniedHandler() {
                return (request, response, accessDeniedException) -> response.sendError(HttpStatus.FORBIDDEN.value(),
                                "Forbidden");
        }

        /**
         * Creates and configures the PasswordEncoder bean.
         * 
         * @return the configured PasswordEncoder
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
