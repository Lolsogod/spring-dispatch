package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.Para;

import java.time.LocalDate;
import java.util.List;

public interface ParaRepository extends JpaRepository<Para, Long> {
    List<Para> findAllByNum(Integer num);
    List<Para> findAllByNumAndDate(Integer num, LocalDate date);
    List<Para> findAllByTeacher_UserIdOrderByDateAsc(Long id);
    Integer countAllByTeacher_UserIdAndStateEquals(Long id, String userID);
}