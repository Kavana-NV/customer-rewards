package com.charter.customer_rewards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Represents a persisted reward-point summary for a customer.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long customerId;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "monthly_points", columnDefinition = "json")
    private Map<String, Integer> monthlyPoints;
    private int totalPoints;

}