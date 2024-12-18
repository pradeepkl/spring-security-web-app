package com.classpathio.orders.controller;

import com.classpathio.orders.model.Role;
import com.classpathio.orders.model.User;
import com.classpathio.orders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

// UserController.java
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.RoleName.values());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam("roleName") Role.RoleName roleName, Model model) {
        userService.registerUser(user, roleName);
        model.addAttribute("activationLink", "/activate?email=" + user.getEmail());
        return "registration-success";
    }

    @GetMapping("/activate")
    public String activateUser(@RequestParam("email") String email) {
        userService.enableUser(email);
        return "activation-success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {

        // Generate a unique reset token and store it in the database
        String resetToken = UUID.randomUUID().toString();
        this.userService.processForgotPassword(email, resetToken);
        // Create the password reset link
        String resetLink = "/reset-password?token=" + resetToken;

        model.addAttribute("resetLink", "/reset-password?token=" + resetToken);
        return "forgot-password-success";

    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        User user = userService.retrieveUserByToken(token);

        model.addAttribute("token", token);
        System.out.println("Came inside the controller ::::::::::");
        System.out.println("User ::::::::::" + user);
        System.out.println("Token ::::::::::" + token);
        if (user != null) {
            model.addAttribute("token", token);
            return "reset-new-password";
        } else {
            // Handle invalid or expired token
            return "redirect:/error";
        }
    }


    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        userService.resetPassword(token, password);
        return "reset-password-success";
    }
}
