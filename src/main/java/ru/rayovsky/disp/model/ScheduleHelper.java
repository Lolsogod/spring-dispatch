package ru.rayovsky.disp.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedule_helper")
public class ScheduleHelper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long helper_id;
    private LocalDate updateDate;

    public ScheduleHelper() {
        super();
    }

    public ScheduleHelper(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }
}