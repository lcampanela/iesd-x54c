package iesd.kafka.msg.publisher;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducePE_Main extends Thread {

	public static void main(String[] args) {
		System.out.println("Starting ProducePaymentEvents...");

		final Boolean FORGET_PRODUCTION = false;
		final Boolean SYNC_PRODUCTION = true;
		final String TOPIC_NAME = "IESDv2021-Topic";
		final String GROUP_NAME = "IESD2021sv-Group";

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, GROUP_NAME);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(props);

		int msgNumber = 1;
		while (true) {
			System.out.println("#...");
			String msg = "HelloWorld-" + msgNumber;
			long startTime = System.currentTimeMillis();
			Future<RecordMetadata> future;
			ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(TOPIC_NAME, msgNumber,
					msg);
			if (FORGET_PRODUCTION) {
				producer.send(producerRecord, new MsgPublishingCallback(startTime, msgNumber, msg));
			} else {
				if (SYNC_PRODUCTION) {
					try {
						future = producer.send(producerRecord);
						future.get();
						System.out.println("Sent message: (" + msgNumber + ", " + msg + ")");
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else { // ASYNC_PRODUCTION
					class DemoProducerCallback implements Callback {
						@Override
						public void onCompletion(RecordMetadata recordMetadata, Exception e) {
							if (e != null) {
								e.printStackTrace();
							}
						}
					}
					producer.send(producerRecord, new DemoProducerCallback());
				}
			}
			System.out.println("msgNumber = " + msgNumber + " sent...");
			++msgNumber;
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				// producer.close();
			}
		}
	}

}