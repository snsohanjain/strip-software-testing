package com.example.softwaretesting.payment;

import com.example.softwaretesting.customer.Customer;
import com.example.softwaretesting.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository,paymentRepository,cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given

        UUID customerId = UUID.randomUUID();
        // ...Customer Exits
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.USD;

        // ...Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(null,null,
                        new BigDecimal("100.00"),Currency.USD,
                        "Card123","Donation"
                        )
        );
        // ...Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId,paymentRequest);

        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue).isEqualToIgnoringGivenFields(
                paymentRequest.getPayment(),"customerId");
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given

        UUID customerId = UUID.randomUUID();
        // ...Customer Exits
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.USD;

        // ...Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(null,null,
                        new BigDecimal("100.00"),Currency.USD,
                        "Card123","Donation"
                )
        );
        // ...Card is Not charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining(
                        "Card not debited for customer " + customerId);
        // Then
        then(paymentRepository).shouldHaveNoInteractions();

    }

    @Test
    void itShouldNotChargeAndThrowWhenCurrencyNotSupported() {
        // Given

        UUID customerId = UUID.randomUUID();
        // ...Customer Exits
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));
        Currency currency = Currency.USD;

        // ...Euros
        Currency eur = Currency.EUR;
        // ...Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        eur,
                        "Card123",
                        "Donation"
                )
        );
        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Currency [" + eur + "] not supported");

        // Then
        // ...No interaction with CardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();
        // ...No interaction with PaymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrownWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // Customer Not Found in DB
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id [" + customerId + "] not found");
        //....No Interaction with PaymentCharger not PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}