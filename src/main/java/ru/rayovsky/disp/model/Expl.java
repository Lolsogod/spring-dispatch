package ru.rayovsky.disp.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="expl")
public class Expl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long explId;
    @OneToOne
    @JoinColumn(name = "id")
    private Para para;
    private String reason;
    private LocalDate writingDate;
    public Expl(Para para, String reason, LocalDate writingDate) {
        this.para = para;
        this.reason = reason;
        this.writingDate = writingDate;
    }

    public Expl() {
        super();
    }

    public Long getExplId() {
        return explId;
    }

    public Para getPara() {
        return para;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getWritingDate() {
        return writingDate;
    }
}
