package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.Expl;

import java.util.List;

public interface ExplRepository extends JpaRepository<Expl, Long> {
    public Expl findByPara_Id(Long id);
    public List<Expl> findAllByPara_Teacher_UserId(Long id);
}