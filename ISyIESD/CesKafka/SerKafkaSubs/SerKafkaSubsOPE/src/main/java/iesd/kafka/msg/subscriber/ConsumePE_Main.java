package iesd.kafka.msg.subscriber;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public class ConsumePE_Main {

	static final String TOPIC_NAME = "IESDv2021-Topic";
	static final String GROUP_ID = "IESD2021sv-G0"; // default group ID
	
	public static void main(String[] args) {
		System.out.println("Starting ConsumePaymentEvents Main...");
        KafkaConsumer<String,String> kafkaConsumer;

        String groupId = GROUP_ID;
        if (args.length == 1) 
        	groupId = groupId.concat(args[0]);
        System.out.println("groupId = " + groupId);
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        //Figure out where to start processing messages from
        kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME));
        //Start processing messages; Poll Loop
        try {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                for (ConsumerRecord<String, String> record : records)
                    System.out.println(record.value());
                kafkaConsumer.commitSync();
            }
        }catch(WakeupException ex){
        	ex.printStackTrace();;
        }finally{
            kafkaConsumer.close();
            System.out.println("KafkaConsumer closed...");
        }
    }
}