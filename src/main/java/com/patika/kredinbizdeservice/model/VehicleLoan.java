package com.patika.kredinbizdeservice.model;

import com.patika.kredinbizdeservice.enums.LoanType;
import com.patika.kredinbizdeservice.enums.VehicleStatusType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Vehicle Loan")
public class VehicleLoan extends Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    private final LoanType loanType = LoanType.ARAC_KREDISI;

    @Column(name = "vehicle_status")
    @Enumerated(EnumType.STRING)
    private VehicleStatusType vehicleStatus;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;


    public VehicleLoan(BigDecimal amount, Integer installment, Double interestRate, VehicleStatusType vehicleStatus) {
        super(amount, installment, interestRate);
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public void calculate(BigDecimal amount, int installment) {

    }
}
