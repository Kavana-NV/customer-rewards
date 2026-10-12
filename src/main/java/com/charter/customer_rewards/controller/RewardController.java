package com.charter.customer_rewards.controller;


import com.charter.customer_rewards.dto.RewardResponseDTO;
import com.charter.customer_rewards.service.RewardService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes customer reward summaries over the REST API.
 */
@RestController
@RequestMapping("/rewards")
@Validated
public class RewardController {

    private final RewardService rewardService;

    /**
     * Creates the controller with its reward calculation service.
     *
     * @param rewardService service used to calculate reward summaries
     */
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Returns the reward summary for a customer.
     *
     * @param customerId positive identifier of the customer
     * @return the customer's monthly and total reward points
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardResponseDTO> getRewards(@PathVariable @Positive Long customerId) {
        RewardResponseDTO summary = rewardService.getRewardSummary(customerId);
        return ResponseEntity.ok(summary);
    }

}
