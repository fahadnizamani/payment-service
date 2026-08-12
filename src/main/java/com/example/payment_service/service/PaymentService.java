package com.example.payment_service.service;

import com.example.payment_service.kafka.OrderCreatedEvent;
import com.example.payment_service.kafka.PaymentEventProducer;
import com.example.payment_service.kafka.PaymentProcessedEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentEventProducer paymentEventProducer) {
        this.paymentEventProducer = paymentEventProducer;
    }

    public void processPayment(OrderCreatedEvent event) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("PAYMENT PROCESSING STARTED");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("UserId  : " + event.getUserId());
        System.out.println("Amount  : " + event.getTotalAmount());
        System.out.println("======================================");

        /*
         * Simulate payment processing.
         *
         * We are intentionally NOT integrating
         * Stripe/PayPal/etc. yet.
         */

        String paymentStatus;

        if (event.getTotalAmount() != null
                && event.getTotalAmount().signum() > 0) {

            paymentStatus = "COMPLETED";

        } else {

            paymentStatus = "FAILED";
        }

        PaymentProcessedEvent paymentEvent =
                new PaymentProcessedEvent(
                        UUID.randomUUID().toString(),
                        "PAYMENT_PROCESSED",
                        1,
                        Instant.now().toString(),
                        event.getOrderId(),
                        event.getUserId(),
                        event.getTotalAmount(),
                        paymentStatus
                );

        paymentEventProducer.sendPaymentProcessedEvent(paymentEvent);

        System.out.println();
        System.out.println("======================================");
        System.out.println("PAYMENT PROCESSING FINISHED");
        System.out.println("OrderId       : " + event.getOrderId());
        System.out.println("PaymentStatus : " + paymentStatus);
        System.out.println("======================================");
        System.out.println();
    }
}