package com.example.softwaretesting.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {
    private final CustomerRepository customerRepository;
    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request){
        // 1. PhoneNumber is Taken
        // 2. If Taken it's belongs to the same customer
        // - 2.1 if yes return
        // - 2.2 throw an exception
        // 3. Save Customer
        String phoneNumber = request.getCustomer().getPhoneNumber();

        Optional<Customer> customerOptional = customerRepository
                .selectCustomerByPhoneNumber(phoneNumber);

        if (customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            if(customer.getName().equals(request.getCustomer().getName())){
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is Taken", phoneNumber));

        }
        if(request.getCustomer().getId() == null){
            request.getCustomer().setId(UUID.randomUUID());
        }

        customerRepository.save(request.getCustomer());

    }
}
