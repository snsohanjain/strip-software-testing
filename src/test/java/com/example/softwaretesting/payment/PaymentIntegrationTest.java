package com.example.softwaretesting.payment;

import com.example.softwaretesting.customer.Customer;
import com.example.softwaretesting.customer.CustomerRegistrationController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessFully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId,"Sohan","9876543210");

        ResultActions customerRegResultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customer)))
        );
        System.out.println(customerRegResultActions);
        // When

        // Then
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }catch (JsonProcessingException e){
            fail("Failed to convert Object to Json");
            return null;
        }
    }
}
