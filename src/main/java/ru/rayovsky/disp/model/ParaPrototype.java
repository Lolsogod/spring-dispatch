package ru.rayovsky.disp.model;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "para_prototype")
public class ParaPrototype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User teacher;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    private String type;
    private String audit;
    private DayOfWeek day;
    private int num;
    private Boolean active;


    public ParaPrototype() {
        super();
    }

    public ParaPrototype(User teacher, Subject subject, String type, String audit, DayOfWeek day, int num, Boolean active) {
        this.teacher = teacher;
        this.subject = subject;
        this.type = type;
        this.audit = audit;
        this.day = day;
        this.num = num;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public User getTeacher() {
        return teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public String getAudit() {
        return audit;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public int getNum() {
        return num;
    }

    public Boolean getActive() {
        return active;
    }
}