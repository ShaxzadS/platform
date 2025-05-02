package com.example.smartrecipe.repositories;

import com.example.smartrecipe.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.products WHERE u.email = :email")
    User findByEmailWithProducts(@Param("email") String email);

    @EntityGraph(attributePaths = {"products"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithProducts(@Param("id") Long id);

    Optional<User> findByEmail(String email);
}