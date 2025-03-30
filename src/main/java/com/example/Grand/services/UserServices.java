package com.example.Grand.services;

import com.example.Grand.models.User;
import com.example.Grand.models.enums.Role;
import com.example.Grand.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class   UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;





    @Transactional
    public User getUserWithRolesAndProducts(String email) {
        return userRepository.findByEmailWithProducts(email);
    }

    @Transactional
    public boolean createUser(User user) {
        try {
            String email = user.getEmail();
            if (userRepository.findByEmailWithProducts(email) != null) return false;
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.getRoles().add(Role.ROLE_USER);
            log.info("Saving new user with email: {}", email);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.error("Error saving user: ", e);
            return false;
        }
    }
    public List<User> list(){
        return userRepository.findAll();
    }
    @Transactional
    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null){
            if (user.isActive()){
                user.setActive(false);
                log.info("Ban user with id = {}; email: {} ", user.getId(),user.getEmail());
            }else {
                user.setActive(true);
                log.info("Unban user with id = {}; email: {} ", user.getId(),user.getEmail());
            }
        }
        userRepository.save(user);
    }
    @Transactional
    public void changeUserRoles(User user, Map<String, String> form) {
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Transactional
    public User getUserWithProductsById(Long id) {
        return userRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }


    public User getUserByPrincipal(Principal principal) {
        return (User) userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}