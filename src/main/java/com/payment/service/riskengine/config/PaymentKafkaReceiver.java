package com.payment.service.riskengine.config;

import com.payment.service.dto.beans.FailedPayment;
import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.service.FailedPaymentService;
import com.payment.service.dto.service.PaymentService;
import com.payment.service.dto.utils.PaymentStatus;
import com.payment.service.dto.utils.RiskLevel;
import com.payment.service.riskengine.riskengine.PaymentRiskValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentKafkaReceiver {

    private static final Logger logger = LoggerFactory.getLogger(PaymentKafkaReceiver.class);
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private FailedPaymentService failedPaymentService;

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    /**
     * The method receive a payment from the producer.
     * If the payment is valid, it will be inserted into the database with NO_RISK and SUCCEEDED status.
     * Otherwise, it will be inserted to the failed payment table for tracking.
     * @param payment
     * @param headers
     */
    @KafkaListener(topics = "${spring.kafka.topic}")
    public void receive(Payment payment, @Headers MessageHeaders headers) {
        logger.info("received payment='{}'", payment);
        headers.keySet().forEach(key -> {
            logger.info("{}: {}", key, headers.get(key));
        });
        boolean flag = false;
        if(PaymentRiskValidator.isPaymentValid(payment)) {
            payment.setPaymentstatus(PaymentStatus.SUCCEEDED.name());
            payment.setRisk(RiskLevel.NORISK.name());
            flag = paymentService.addPayment(payment);
            if(!flag){
                payment.setPaymentstatus(PaymentStatus.FAILED.name());
            }
            latch.countDown();
        } else {
            FailedPayment failedPayment = new FailedPayment();
            failedPayment.copyFromPayment(payment);
            failedPayment.setPaymentstatus(PaymentStatus.FAILED.name().toString());
            failedPayment.setRisk(RiskLevel.HIGH.name().toString());
            failedPaymentService.addPayment(failedPayment);
        }
    }

}