package com.example.softwaretesting.customer;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        UUID id = UUID.randomUUID();
        String phoneNumber = "123456789";
        Customer customer = new Customer(id,"SOHAN", phoneNumber);
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isPresent().hasValueSatisfying( c -> {
            // METHOD 1
//            assertThat(c.getId()).isEqualTo(id);
//            assertThat(c.getName()).isEqualTo("SOHAN");
//            assertThat(c.getPhoneNumber()).isEqualTo("123456789");
            // METHOD 2
            assertThat(c).isEqualToComparingFieldByField(customer);
        });
    }
    @Test
    void itShouldNotSelectCustomerByPhoneNumberDoesNotExist() {
        // Given
        String phoneNumber = "123456789";
        // When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        // Then
        assertThat(optionalCustomer).isNotPresent();

    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,"SOHAN","123456789");
        // When
        underTest.save(customer);
        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer).isPresent().hasValueSatisfying( c -> {
            // METHOD 1
//            assertThat(c.getId()).isEqualTo(id);
//            assertThat(c.getName()).isEqualTo("SOHAN");
//            assertThat(c.getPhoneNumber()).isEqualTo("123456789");
            // METHOD 2
              assertThat(c).isEqualToComparingFieldByField(customer);
        });
    }
    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,null,"123456789");
        // When
        // Then
       assertThatThrownBy(() -> underTest.save(customer))
               .hasMessageContaining("not-null property references a null or transient value : com.example.softwaretesting.customer.Customer.name")
               .isInstanceOf(DataIntegrityViolationException.class);

    }
    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id,"Nithin",null);
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.example.softwaretesting.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);

    }

}