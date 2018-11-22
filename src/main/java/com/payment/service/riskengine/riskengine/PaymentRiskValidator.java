package com.payment.service.riskengine.riskengine;

import com.payment.service.dto.beans.Payment;
import com.payment.service.riskengine.config.PaymentKafkaReceiver;

import java.util.concurrent.atomic.AtomicInteger;

public class PaymentRiskValidator {

    private static AtomicInteger PAYMENT_COUNTER = new AtomicInteger(0);

    public static boolean isPaymentValid(Payment payment){
        PAYMENT_COUNTER.getAndIncrement();
        if((PAYMENT_COUNTER.get()*100)%70==0){
            return false;
        }
        return true;
    }
}
