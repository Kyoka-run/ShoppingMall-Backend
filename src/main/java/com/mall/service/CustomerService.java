package com.mall.service;

import com.mall.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer registerCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void deleteCustomer(Long id);
    Customer updateCustomer(Long id, Customer updatedCustomer);
}
