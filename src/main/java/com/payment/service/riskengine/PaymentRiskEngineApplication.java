package com.payment.service.riskengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableAutoConfiguration(exclude={KafkaAutoConfiguration.class})
public class PaymentRiskEngineApplication {


	public static void main(String[] args) {

		SpringApplication.run(PaymentRiskEngineApplication.class, args);
	}
}
