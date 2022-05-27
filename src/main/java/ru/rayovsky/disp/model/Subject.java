package ru.rayovsky.disp.model;

import javax.persistence.*;

@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subjectId;
    private String name;

    public Subject(String name) {
        this.name = name;
    }

    public Subject() {
        super();
    }

    public long getSubjectId() {
        return subjectId;
    }

    public String getName() {
        return name;
    }
}
