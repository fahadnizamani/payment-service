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

        // -----------------------------------------
        // STEP 1: Simulate payment
        // -----------------------------------------

        boolean paymentSuccess = true;

        if (!paymentSuccess) {

            System.out.println("PAYMENT FAILED");

            publishPaymentEvent(event, "FAILED");

            return;
        }

        System.out.println("PAYMENT SUCCESSFUL");

        // -----------------------------------------
        // STEP 2: Simulate inventory reservation
        // -----------------------------------------

        System.out.println();
        System.out.println("======================================");
        System.out.println("INVENTORY RESERVATION STARTED");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("======================================");

        /*
         * Saga simulation:
         *
         * Every 5th order fails inventory.
         *
         * 81 -> SUCCESS
         * 82 -> SUCCESS
         * 83 -> SUCCESS
         * 84 -> SUCCESS
         * 85 -> FAILURE -> COMPENSATION
         */

        boolean inventorySuccess =
                event.getOrderId() % 5 != 0;

        if (inventorySuccess) {

            // -----------------------------------------
            // STEP 3A: Inventory succeeded
            // -----------------------------------------

            System.out.println();
            System.out.println("======================================");
            System.out.println("INVENTORY RESERVATION SUCCESSFUL");
            System.out.println("OrderId : " + event.getOrderId());
            System.out.println("======================================");

            publishPaymentEvent(event, "COMPLETED");

            System.out.println();
            System.out.println("======================================");
            System.out.println("SAGA COMPLETED");
            System.out.println("OrderId : " + event.getOrderId());
            System.out.println("======================================");

        } else {

            // -----------------------------------------
            // STEP 3B: Inventory failed
            // -----------------------------------------

            System.out.println();
            System.out.println("======================================");
            System.out.println("INVENTORY RESERVATION FAILED");
            System.out.println("OrderId : " + event.getOrderId());
            System.out.println("======================================");

            // -----------------------------------------
            // STEP 4: Compensation
            // -----------------------------------------

            compensatePayment(event);

            // Payment itself did NOT fail.
            // Inventory failed after payment succeeded.
            publishPaymentEvent(event, "INVENTORY_FAILED");

            System.out.println();
            System.out.println("======================================");
            System.out.println("SAGA COMPENSATION COMPLETED");
            System.out.println("OrderId : " + event.getOrderId());
            System.out.println("======================================");
        }

        System.out.println();
        System.out.println("======================================");
        System.out.println("PAYMENT PROCESSING FINISHED");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("======================================");
        System.out.println();
    }

    private void compensatePayment(OrderCreatedEvent event) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("SAGA COMPENSATION STARTED");
        System.out.println("Refunding payment...");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("======================================");

        /*
         * Simulate payment refund.
         *
         * In a real application this would call
         * the payment provider to refund the payment.
         */

        System.out.println("PAYMENT REFUND SUCCESSFUL");

        System.out.println();
        System.out.println("======================================");
        System.out.println("SAGA COMPENSATION FINISHED");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("======================================");
    }

    private void publishPaymentEvent(
            OrderCreatedEvent event,
            String paymentStatus) {

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
        System.out.println("PAYMENT_PROCESSED published");
        System.out.println("OrderId       : " + event.getOrderId());
        System.out.println("PaymentStatus : " + paymentStatus);
    }
}