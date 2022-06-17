package ru.rayovsky.disp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayovsky.disp.model.ParaTime;

public interface ParaTimeRepository extends JpaRepository<ParaTime, Long> {
    //без опшинала потому что данные полюбому есть и никто их не меняет
    ParaTime findByNum(int num);
}