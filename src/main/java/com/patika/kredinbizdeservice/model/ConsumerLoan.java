package com.patika.kredinbizdeservice.model;


import com.patika.kredinbizdeservice.enums.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Consumer Loan")

public class ConsumerLoan extends Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private final LoanType loanType = LoanType.IHTIYAC_KREDISI;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @Override
    public void calculate(BigDecimal amount, int installment) {

    }
}
