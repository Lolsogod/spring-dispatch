package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(String role);
    List<User> findAllByRoleEquals(String role);
}