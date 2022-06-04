package ru.rayovsky.disp.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="para")
public class Para {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;
    private String audit;
    private LocalDate date;

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    private Integer num;

    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }

    private LocalDateTime checkDate;

    public void setState(String state) {
        this.state = state;
    }

    private String state;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User teacher;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Para(String type, String audit, LocalDate date, Integer num, String state, User teacher, Subject subject) {
        this.type = type;
        this.audit = audit;
        this.date = date;
        this.num = num;
        this.state = state;
        this.teacher = teacher;
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public User getTeacher() {
        return teacher;
    }

    public Para() {
        super();
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAudit() {
        return audit;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getNum() {
        return num;
    }

    public String getState() {
        return state;
    }
}
