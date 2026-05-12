package com.group_one.food_delivery_app.controllers;

import com.group_one.food_delivery_app.Interfaces.IUserService;
import com.group_one.food_delivery_app.models.UserModel;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String performLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               RedirectAttributes redirectAttributes) {
        UserModel user = userService.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userName", user.getUserName() != null ? user.getUserName() : user.getFirstName());
            session.setAttribute("userRole", user.getRole());
            
            // Redirect based on role
            String role = (user.getRole() != null) ? user.getRole().toUpperCase() : "USER";
            if (role.equals("ADMIN") || role.equals("STAFF")) {
                return "redirect:/backend/dashboard";
            }
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        return "redirect:/auth/login";
    }

    @GetMapping("/register") // sign up
    public String register() {
        return "auth/signup";
    }

    @PostMapping("/register")
    public String performRegister(UserModel user, RedirectAttributes redirectAttributes) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUST"); // default role is Customer
        }
        // Handle email uniqueness ideally, but for now simple setup
        UserModel existingUser = userService.findByEmail(user.getEmail());
        if(existingUser != null) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/auth/register";
        }
        userService.AddData(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);

    if (session != null) {
        session.invalidate();
    }

    return "redirect:/auth/login";
}
}
