package com.charter.customer_rewards.service;

import com.charter.customer_rewards.dto.RewardResponseDTO;
import com.charter.customer_rewards.exception.CustomerNotFoundException;
import com.charter.customer_rewards.model.Transaction;
import com.charter.customer_rewards.repository.CustomerRepository;
import com.charter.customer_rewards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Calculates reward points from a customer's transactions.
 */
@Service
public class RewardService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    /**
     * Creates the service with its customer and transaction repositories.
     *
     * @param transactionRepository repository used to retrieve transactions
     * @param customerRepository repository used to verify customers
     */
    @Autowired
    public RewardService(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves and calculates the monthly and total reward points for a customer.
     *
     * @param customerId identifier of the customer
     * @return reward summary for the customer
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public RewardResponseDTO getRewardSummary(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        return calculateRewards(transactions, customerId);
    }

    /**
     * Calculates points for one transaction according to the reward tiers.
     *
     * @param amount transaction amount
     * @return earned points, rounded down to whole points
     */
    public int calculatePoints(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            return 0;
        }

        int points = 0;
        if (amount > 100) {
            points += (int) Math.floor((amount - 100) * 2);
            points += 50;
        } else if (amount > 50) {
            points += (int) Math.floor(amount - 50);
        }
        return Math.max(points, 0);
    }

    /**
     * Aggregates transaction points by calendar month and for the full period.
     *
     * @param transactions transactions to include
     * @param customerId customer identifier to include in the response
     * @return calculated reward summary
     */
    private RewardResponseDTO calculateRewards(List<Transaction> transactions, Long customerId) {
        Map<String, Integer> monthlyPoints = new TreeMap<>();
        int totalPoints = 0;

        for (Transaction t : transactions) {
            if (t == null || t.getAmount() == null || t.getDate() == null) {
                continue;
            }

            int points = calculatePoints(t.getAmount());
            String month = YearMonth.from(t.getDate()).toString();
            monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
            totalPoints += points;
        }

        RewardResponseDTO dto = new RewardResponseDTO();
        dto.setCustomerId(customerId);
        dto.setMonthlyPoints(new LinkedHashMap<>(monthlyPoints));
        dto.setTotalPoints(totalPoints);
        return dto;
    }

}
