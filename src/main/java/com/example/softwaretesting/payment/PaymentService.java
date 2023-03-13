package com.example.softwaretesting.payment;

import com.example.softwaretesting.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD,Currency.GBP);

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }


    void chargeCard(UUID customerId, PaymentRequest paymentRequest){

        // 1. Does Customer Exist if not throw
        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();

        if(!isCustomerFound){
            throw new IllegalStateException(String.format("customer with id [%s] not found", customerId));
        }
        // 2. Do We Support the Currency if Not Throw
        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream().anyMatch(c ->
                c.equals(paymentRequest.getPayment().getCurrency()));
        if(!isCurrencySupported){
            String message = String.format("Currency [%s] not supported", paymentRequest.getPayment().getCurrency());
            throw new IllegalStateException(message);
        }
        // 3. Charge Card

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        // 4. if not debit throw
        if(!cardPaymentCharge.isCardDebited()){
            throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }
        // 5. Insert Payment
        paymentRequest.getPayment().setCustomerId(customerId);
        paymentRepository.save(paymentRequest.getPayment());
        // 6. TODO : send sms


    }
}
