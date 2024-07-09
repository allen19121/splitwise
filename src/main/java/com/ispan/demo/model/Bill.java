package com.ispan.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bill")
@Getter
@Setter
@NoArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Double amount;

    private String description;

    @ManyToMany
    @JoinTable(
        name = "bill_users",
        joinColumns = @JoinColumn(name = "bill_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<Users> participants = new ArrayList<>();

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    private Photos photo;

    @Transient
    private Integer numberOfPeople;

    @Transient
    private Double perPersonAmount;

    @ManyToOne
    @JoinColumn(name = "payer_id")
    private Users payer; // 先付錢的人

    public Map<Users, Double> getParticipantAmounts() {
        Map<Users, Double> participantAmounts = new HashMap<>();
        if (participants != null && !participants.isEmpty()) {
            Double perPersonAmount = amount / participants.size();
            for (Users participant : participants) {
                Double roundedAmount = new BigDecimal(perPersonAmount)
                    .setScale(0, RoundingMode.CEILING) // 無條件進位
                    .doubleValue();
                participantAmounts.put(participant, roundedAmount);
            }
        }
        return participantAmounts;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", numberOfPeople=" + numberOfPeople +
                ", perPersonAmount=" + perPersonAmount +
                ", payer=" + (payer != null ? payer.getNickname() : "null") +
                ", photo=" + (photo != null ? photo.getId() : "null") +
                '}';
    }
}
