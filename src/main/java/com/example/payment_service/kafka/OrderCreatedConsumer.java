package com.example.payment_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service"
    )
    public void consume(OrderCreatedEvent event) {

        System.out.println();
        System.out.println("====================================");
        System.out.println("PAYMENT-SERVICE received ORDER_CREATED");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("UserId  : " + event.getUserId());
        System.out.println("Amount  : " + event.getTotalAmount());
        System.out.println("====================================");
    }
}