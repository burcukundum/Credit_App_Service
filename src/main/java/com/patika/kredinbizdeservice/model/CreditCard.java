package com.patika.kredinbizdeservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Credit Cards")
public class CreditCard implements Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "annual_fee")
    private BigDecimal annualFee;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

}


