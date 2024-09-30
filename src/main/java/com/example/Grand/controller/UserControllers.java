package com.example.Grand.controller;

import com.example.Grand.models.User;
import org.springframework.ui.Model;

import com.example.Grand.repositories.UserRepository;
import com.example.Grand.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserControllers {
    private final UserServices userServices;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
    @PostMapping("/registration")
    public String CreateUser(User user, Model model) {
        if (!userServices.createUser(user)){
            model.addAttribute("errorMessage", "User with email already exists: " + user.getEmail());
            return "registration";
        }

        return "redirect:/login";
    }
}
