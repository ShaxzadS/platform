package com.example.smartrecipe.services;

import com.example.smartrecipe.models.User;
import com.example.smartrecipe.models.enums.Role;
import com.example.smartrecipe.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Service
//@Slf4j
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserServices.class);



    @Autowired
    public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(principal.getName());
    }

    // Остальной код остаётся без изменений
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

    public List<User> list() {
        return userRepository.findAll();
    }

    @Transactional
    public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
                log.info("Ban user with id = {}; email: {} ", user.getId(), user.getEmail());
            } else {
                user.setActive(true);
                log.info("Unban user with id = {}; email: {} ", user.getId(), user.getEmail());
            }
            userRepository.save(user);
        }
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

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }


    @Transactional
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    public boolean authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.error("User not found with email: {}", email);
            return false;
        }

        User user = userOptional.get();
        boolean isMatch = passwordEncoder.matches(password, user.getPassword());

        if (!isMatch) {
            log.error("Password mismatch for user: {}", email);
        }

        return isMatch;
    }
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
    public Optional<User> getProfile(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        String email = principal.getName();
        return userRepository.findByEmail(email);
    }


}