package com.classpathio.orders.service;

import com.classpathio.orders.model.Role;
import com.classpathio.orders.model.User;
import com.classpathio.orders.repository.RoleRepository;
import com.classpathio.orders.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// UserService.java
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(User user, Role.RoleName roleName) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    // Create and save the missing role
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void resetPassword(String token, String newPassword) {
        User user = this.retrieveUserByToken(token);

        // Update the user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

    }

    public void processForgotPassword(String email, String resetToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Save the reset token in the user entity
        user.setResetToken(resetToken);
        userRepository.save(user);

    }

    public User retrieveUserByToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
        return user;
    }
}
