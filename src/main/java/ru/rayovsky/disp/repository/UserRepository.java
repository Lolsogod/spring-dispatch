package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}