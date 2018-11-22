package com.payment.service.riskengine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.payment.service.dto.beans.Payment;
import com.payment.service.riskengine.config.PaymentKafkaReceiver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class SpringKafkaReceiverTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringKafkaReceiverTest.class);

    private static String RECEIVER_TOPIC = "payment_service_craft";

    @Autowired
    private PaymentKafkaReceiver receiver;

    private KafkaTemplate<String, Payment> template;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, RECEIVER_TOPIC);

    @Before
    public void setUp() throws Exception {
        // set up the Kafka producer properties
        Map<String, Object> senderProperties =
                KafkaTestUtils.senderProps(embeddedKafka.getBrokersAsString());

        // create a Kafka producer factory
        ProducerFactory<String, Payment> producerFactory =
                new DefaultKafkaProducerFactory<String, Payment>(senderProperties);

        // create a Kafka template
        template = new KafkaTemplate<String, Payment>(producerFactory);
        // set the default topic to send to
        template.setDefaultTopic(RECEIVER_TOPIC);

        // wait until the partitions are assigned
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
                .getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    embeddedKafka.getPartitionsPerTopic());
        }
    }

    @Test
    public void testReceive() throws Exception {
        Payment payment = new Payment("1u24a8t5-5555-4321-93b6-6efee67dk823","USD",(float)4.2, "1u24a8t5-5555-4321-93b6-6efee67dk823","1u24a8t5-5555-4321-93b6-6efee67dk823","aaa","12345");
        template.sendDefault(payment);
        LOGGER.debug("test-sender sent message='{}'", payment);

        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        // check that the message was received
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    }
}
