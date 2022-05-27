package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.ParaPrototype;

import java.time.DayOfWeek;
import java.util.List;

public interface ParaPrototypeRepository extends JpaRepository<ParaPrototype, Long> {
    ParaPrototype findByTeacher_UserIdAndDayAndActiveIsTrue(Long userID, DayOfWeek day);
    List<ParaPrototype> findAllByTeacher_UserIdAndDayAndActiveIsTrue(Long userID, DayOfWeek day);
}