package com.patika.kredinbizdeservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Banks")

public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "bank")
    private List<HouseLoan> houseLoans;

    @OneToMany(mappedBy = "bank")
    private List<ConsumerLoan> consumerLoans;

    @OneToMany(mappedBy = "bank")
    private List<VehicleLoan> vehicleLoans;

    @OneToMany(mappedBy = "bank")
    private List<CreditCard> creditCards;


}
