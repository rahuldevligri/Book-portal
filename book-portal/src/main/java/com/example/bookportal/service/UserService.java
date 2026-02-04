package com.example.bookportal.service;

import com.example.bookportal.dto.ChangePasswordForm;
import com.example.bookportal.dto.EditProfileForm;
import com.example.bookportal.dto.RegisterForm;
import com.example.bookportal.entity.User;
import com.example.bookportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= EXISTING LOGIC (UNCHANGED) =================

    public void register(RegisterForm form) {

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setFullName(form.getFirstName() + " " + form.getLastName());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setSecretAnswer(form.getSecretAnswer());
        user.setSecretQuestion(form.getSecretQuestion());

        userRepository.save(user);
    }

    public void changePassword(String username, ChangePasswordForm form) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    // ================= NEW METHODS (ADDED ONLY) =================

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));
    }

    public void updateProfileFromForm(EditProfileForm form) {

        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(form.getEmail());
        user.setFullName(form.getFirstName() + " " + form.getLastName());
        user.setSecretQuestion(form.getSecretQuestion());
        user.setSecretAnswer(form.getSecretAnswer());

        userRepository.save(user);
    }
}
