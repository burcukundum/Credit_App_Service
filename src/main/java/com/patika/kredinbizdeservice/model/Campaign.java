package com.patika.kredinbizdeservice.model;

import com.patika.kredinbizdeservice.enums.SectorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_date")
    private LocalDate createDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "update_date")
    private LocalDate updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector")
    private SectorType sector;

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;
}