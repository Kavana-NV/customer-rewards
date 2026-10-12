package com.charter.customer_rewards.repository;

import com.charter.customer_rewards.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations for customers.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
