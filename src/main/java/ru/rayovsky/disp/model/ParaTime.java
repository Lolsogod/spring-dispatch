package ru.rayovsky.disp.model;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "paraTime")
public class ParaTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paraTimeId;
    private int num;
    private LocalTime startTime;
    private LocalTime endTime;


    public ParaTime(int num, LocalTime startTime, LocalTime endTime) {
        this.num = num;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ParaTime() {
        super();
    }

    public int getNum() {
        return num;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
