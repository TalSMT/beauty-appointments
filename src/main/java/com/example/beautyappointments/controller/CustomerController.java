package com.example.beautyappointments.controller;


import com.example.beautyappointments.entity.Appointment;
import com.example.beautyappointments.entity.Customer;
import com.example.beautyappointments.repository.CustomerRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.beautyappointments.entity.Customer;



@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        System.out.println(" hit customer");
        System.out.println(customer);

        return repository.save(customer);
    }

    @GetMapping
    public List<Customer> getAll() {
        return repository.findAll();
    }




}