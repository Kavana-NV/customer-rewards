package com.charter.customer_rewards;

import com.charter.customer_rewards.model.Customer;
import com.charter.customer_rewards.model.Transaction;
import com.charter.customer_rewards.repository.CustomerRepository;
import com.charter.customer_rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for reward API responses and error handling.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RewardControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    /**
     * Creates MockMvc using the application web context for each test.
     */
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Verifies correct monthly and total points for multiple customers.
     */
    @Test
    void testGetRewardsSuccessCaseForMultipleCustomers() throws Exception {
        Customer customer1 = customerRepository.save(new Customer(null, "John"));
        Customer customer2 = customerRepository.save(new Customer(null, "Kiran"));

        transactionRepository.saveAll(List.of(
                new Transaction(null, customer1.getId(), 120.0, LocalDate.of(2024, 1, 10)),
                new Transaction(null, customer1.getId(), 75.0, LocalDate.of(2024, 2, 5)),
                new Transaction(null, customer1.getId(), 90.0, LocalDate.of(2024, 2, 20)),
                new Transaction(null, customer2.getId(), 200.0, LocalDate.of(2024, 3, 1)),
                new Transaction(null, customer2.getId(), 80.0, LocalDate.of(2024, 3, 15))
        ));

        mockMvc.perform(get("/rewards/{customerId}", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer1.getId()))
                .andExpect(jsonPath("$.totalPoints").value(155))
                .andExpect(jsonPath("$.monthlyPoints['2024-01']").value(90))
                .andExpect(jsonPath("$.monthlyPoints['2024-02']").value(65));

        mockMvc.perform(get("/rewards/{customerId}", customer2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer2.getId()))
                .andExpect(jsonPath("$.totalPoints").value(280))
                .andExpect(jsonPath("$.monthlyPoints['2024-03']").value(280));
    }

    /**
     * Verifies customers without transactions receive an empty summary.
     */
    @Test
    void testGetRewardsForCustomerWithNoTransactions() throws Exception {
        Customer customer = customerRepository.save(new Customer(null, "Sagar"));

        mockMvc.perform(get("/rewards/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.totalPoints").value(0))
                .andExpect(jsonPath("$.monthlyPoints").isEmpty());
    }

    /**
     * Verifies unknown customers return a structured not-found response.
     */
    @Test
    void testGetRewardsInvalidCustomer() throws Exception {
        long missingCustomerId = customerRepository.findAll().stream()
                .map(Customer::getId)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;

        mockMvc.perform(get("/rewards/{customerId}", missingCustomerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found with id: " + missingCustomerId))
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * Verifies non-positive customer IDs return a structured bad-request response.
     */
    @Test
    void testGetRewardsRejectsInvalidPathVariable() throws Exception {
        mockMvc.perform(get("/rewards/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.status").value(400));
    }
}
