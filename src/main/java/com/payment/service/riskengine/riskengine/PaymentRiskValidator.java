package com.payment.service.riskengine.riskengine;

import com.payment.service.dto.beans.Payment;
import com.payment.service.riskengine.config.PaymentKafkaReceiver;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class calculates if the payment is valid
 */
public class PaymentRiskValidator {

    private static Random random = new Random();
    /**
     * This method assumes uniform distribution of random number
     * to get 70% of payments
     * @param payment
     * @return
     */
    public static boolean isPaymentValid(Payment payment){
        int n = random.nextInt(10) / 7;
        if(n>0) {
            return false;
        }
        return true;
    }


}
