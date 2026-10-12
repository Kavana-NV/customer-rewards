package com.charter.customer_rewards;

import com.charter.customer_rewards.dto.RewardResponseDTO;
import com.charter.customer_rewards.exception.CustomerNotFoundException;
import com.charter.customer_rewards.model.Customer;
import com.charter.customer_rewards.model.Transaction;
import com.charter.customer_rewards.repository.CustomerRepository;
import com.charter.customer_rewards.repository.TransactionRepository;
import com.charter.customer_rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for reward calculation and customer summary behavior.
 */
@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardService service;

    /**
     * Verifies reward tiers and treatment of non-positive amounts.
     */
    @Test
    void testCalculatePoints() {
        assertEquals(90, service.calculatePoints(120));
        assertEquals(45, service.calculatePoints(95));
        assertEquals(0, service.calculatePoints(40));
        assertEquals(0, service.calculatePoints(-10));
    }

    /**
     * Verifies multiple customers receive isolated points across transaction months.
     */
    @Test
    void testGetRewardSummaryForMultipleCustomersWithMultipleTransactions() {
        Customer customer1 = new Customer(1L, "John");
        Customer customer2 = new Customer(2L, "Kiran");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(transactionRepository.findByCustomerId(1L)).thenReturn(List.of(
                new Transaction(1L, 1L, 120.0, LocalDate.of(2024, 1, 10)),
                new Transaction(2L, 1L, 75.0, LocalDate.of(2024, 2, 5)),
                new Transaction(3L, 1L, 90.0, LocalDate.of(2024, 2, 20))
        ));

        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer2));
        when(transactionRepository.findByCustomerId(2L)).thenReturn(List.of(
                new Transaction(4L, 2L, 200.0, LocalDate.of(2024, 3, 1)),
                new Transaction(5L, 2L, 80.0, LocalDate.of(2024, 3, 15))
        ));

        RewardResponseDTO customer1Summary = service.getRewardSummary(1L);
        assertEquals(1L, customer1Summary.getCustomerId());
        assertEquals(155, customer1Summary.getTotalPoints());
        assertEquals(90, customer1Summary.getMonthlyPoints().get("2024-01"));
        assertEquals(65, customer1Summary.getMonthlyPoints().get("2024-02"));

        RewardResponseDTO customer2Summary = service.getRewardSummary(2L);
        assertEquals(2L, customer2Summary.getCustomerId());
        assertEquals(280, customer2Summary.getTotalPoints());
        assertEquals(280, customer2Summary.getMonthlyPoints().get("2024-03"));
    }

    /**
     * Verifies a customer without transactions receives an empty zero-point summary.
     */
    @Test
    void testGetRewardSummaryForCustomerWithNoTransactions() {
        Customer customer = new Customer(3L, "Sagar");
        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerId(3L)).thenReturn(Collections.emptyList());

        RewardResponseDTO summary = service.getRewardSummary(3L);
        assertEquals(3L, summary.getCustomerId());
        assertEquals(0, summary.getTotalPoints());
        assertTrue(summary.getMonthlyPoints().isEmpty());
    }

    /**
     * Verifies a missing customer triggers the expected domain exception.
     */
    @Test
    void testGetRewardSummaryThrowsForInvalidCustomer() {
        when(customerRepository.findById(100L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> service.getRewardSummary(100L));

        assertEquals("Customer not found with id: 100", exception.getMessage());
    }
}
