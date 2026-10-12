package com.charter.customer_rewards.repository;

import com.charter.customer_rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides persistence operations for customer transactions.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves all transactions belonging to a customer.
     *
     * @param customerId identifier of the customer
     * @return the customer's transactions
     */
    List<Transaction> findByCustomerId(Long customerId);
}