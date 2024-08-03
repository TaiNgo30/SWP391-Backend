package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.CustomerResponse;
import com.wha.warehousemanagement.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT new com.wha.warehousemanagement.dtos.responses.CustomerResponse(c.id, c.name, c.email, c.phone, c.address) FROM Customer c ORDER BY c.id desc")
    List<CustomerResponse> getAllCustomerResponses();
}
