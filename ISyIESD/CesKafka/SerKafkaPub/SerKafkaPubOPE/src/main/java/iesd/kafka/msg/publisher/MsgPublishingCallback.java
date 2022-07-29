package iesd.kafka.msg.publisher;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MsgPublishingCallback implements Callback {
	private final long startTime;
	private final int key;
	private final String message;

	public MsgPublishingCallback(long startTime, int key, String message) {
        this.startTime = startTime = System.currentTimeMillis();
        this.key = key;
        this.message = message;
    }

	public void onCompletion(RecordMetadata metadata, Exception exception) {
		System.out.println("MsgPublishingCallback.onCompletion()...");
		long elapsedTime = System.currentTimeMillis() - startTime;
		if (metadata != null) {
			System.out.println("message(" + key + ", " + message + ") sent to partition(" + metadata.partition() + "), "
					+ "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
		} else {
			exception.printStackTrace();
		}
	}
}
