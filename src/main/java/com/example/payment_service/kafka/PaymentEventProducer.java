package com.example.payment_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentProcessedEvent(
            PaymentProcessedEvent event) {

        kafkaTemplate.send(
                "payment-processed",
                event.getOrderId().toString(),
                event
        );

        System.out.println();
        System.out.println("======================================");
        System.out.println("PAYMENT_PROCESSED published");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("======================================");
        System.out.println();
    }
}