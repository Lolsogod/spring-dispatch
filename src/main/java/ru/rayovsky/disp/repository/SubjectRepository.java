package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}