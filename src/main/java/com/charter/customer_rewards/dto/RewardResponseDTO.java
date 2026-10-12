package com.charter.customer_rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Carries a customer's reward points grouped by month and as a total.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardResponseDTO {
    private Long customerId;
    private Map<String, Integer> monthlyPoints;
    private int totalPoints;

}
