package com.example.payment_service.kafka;

import com.example.payment_service.service.PaymentService;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final PaymentService paymentService;

    public OrderCreatedConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            )
    )
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

        paymentService.processPayment(event);
    }

    @DltHandler
    public void handleDlt(OrderCreatedEvent event) {

        System.out.println();
        System.out.println("====================================");
        System.out.println("ORDER_CREATED MOVED TO DLT");
        System.out.println("OrderId : " + event.getOrderId());
        System.out.println("UserId  : " + event.getUserId());
        System.out.println("Amount  : " + event.getTotalAmount());
        System.out.println("====================================");
    }
}