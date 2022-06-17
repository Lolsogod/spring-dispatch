package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.Expl;

import java.util.List;
import java.util.Optional;

public interface ExplRepository extends JpaRepository<Expl, Long> {
    Optional<Expl> findByPara_Id(Long id);
    List<Expl> findAllByPara_Teacher_UserId(Long id);
}