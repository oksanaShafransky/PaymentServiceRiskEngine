package com.payment.service.riskengine.config;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.service.PaymentService;
import com.payment.service.riskengine.riskengine.RiskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    @Autowired
    private PaymentService paymentService;

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "payment_service_craft")
    public void receive(Payment payment, @Headers MessageHeaders headers) {
        logger.info("received payment='{}'", payment);
        headers.keySet().forEach(key -> {
            logger.info("{}: {}", key, headers.get(key));
        });
        if(RiskValidator.isPaymentValid(payment)) {
            boolean flag = paymentService.addPayment(payment);
            latch.countDown();
        }
    }

}