package com.payment.service.riskengine.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.dto.beans.Payment;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class PaymentDeserializer implements Deserializer {

    @Override
    public void close() {
    }

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Payment deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        Payment payment = null;
        try {
            payment = mapper.readValue(arg1, Payment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payment;
    }
}