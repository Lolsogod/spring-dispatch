package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.ScheduleHelper;

public interface ScheduleHelperRepository extends JpaRepository<ScheduleHelper, Long> {
}