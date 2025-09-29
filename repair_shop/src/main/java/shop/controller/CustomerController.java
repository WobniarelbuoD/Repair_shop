package shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import shop.dto.CustomerProfitDTO;
import shop.dto.ErrorResponse;
import shop.model.Customer;
import shop.repository.CustomerRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // Get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by code
    @GetMapping("/{code}")
    public ResponseEntity<?> getCustomer(@PathVariable String code) {
        Optional<Customer> customerOpt = customerRepository.findByCode(code);
        if (customerOpt.isPresent()) {
            return ResponseEntity.ok(customerOpt.get());
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Customer not found with code: " + code, 404));
        }
    }

    // Create a new customer
    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        if (customer.getCode() == null || customer.getCode().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Customer code must be provided", 400));
        }
        if (customerRepository.findByCode(customer.getCode()).isPresent()) {
            return ResponseEntity.status(409)
                .body(new ErrorResponse("Customer with this code already exists", 409));
        }

        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    // Update customer by code
    @PutMapping("/{code}")
    public ResponseEntity<?> updateCustomer(@PathVariable String code, @RequestBody Customer updatedCustomer) {
        Optional<Customer> optionalCustomer = customerRepository.findByCode(code);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (updatedCustomer.getName() != null) {
                customer.setName(updatedCustomer.getName());
            }
            // Update other fields as needed
            customerRepository.save(customer);
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Customer not found with code: " + code, 404));
        }
    }

    // Delete customer by code
    @DeleteMapping("/{code}")
    public ResponseEntity<ErrorResponse> deleteCustomer(@PathVariable String code) {
        Optional<Customer> customerOpt = customerRepository.findByCode(code);
        if (customerOpt.isPresent()) {
            customerRepository.delete(customerOpt.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404)
                .body(new ErrorResponse("Customer not found with code: " + code, 404));
        }
    }
    
    //get top clients
    @GetMapping("/top-profit/{count}")
    public ResponseEntity<List<CustomerProfitDTO>> getTopCustomers(@PathVariable int count) {
        Pageable pageable = PageRequest.of(0, count);
        List<Object[]> results = customerRepository.findTopCustomersByProfit(pageable);
        List<CustomerProfitDTO> dtos = results.stream()
            .map(obj -> {
                Customer c = (Customer) obj[0];
                Double profit = (Double) obj[1];
                return new CustomerProfitDTO(c.getCode(), c.getName(), profit);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}