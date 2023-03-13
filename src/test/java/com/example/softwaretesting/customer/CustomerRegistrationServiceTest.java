package com.example.softwaretesting.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // Given a phone number and a customer
        String phoneNumber = "987654321";
        Customer customer = new Customer(UUID.randomUUID(),"Nithin", phoneNumber);

        // ...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ...No Customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // Given a phone number and a customer
        String phoneNumber = "987654321";
        Customer customer = new Customer(null,"Nithin", phoneNumber);

        // ...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ...No Customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue)
                .isEqualToIgnoringGivenFields(customer,"id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerCustomerExists() {
        // Given a phone number and a customer
        String phoneNumber = "987654321";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,"Nithin", phoneNumber);

        // ...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ...an Existing Customer is Returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));
        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowPhoneNumberIsTaken() {
        // Given a phone number and a customer
        String phoneNumber = "987654321";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,"Nithin", phoneNumber);
        Customer customerTwo = new Customer(id,"Manoj", phoneNumber);

        // ...a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ...an Existing Customer is Returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customerTwo));
        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is Taken", phoneNumber));
        // Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }


}