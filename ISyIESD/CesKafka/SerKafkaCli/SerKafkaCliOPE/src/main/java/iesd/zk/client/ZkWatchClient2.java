package iesd.zk.client;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkWatchClient2 implements Watcher {

	protected static final Logger LOG = Logger.getLogger(ZkWatchClient2.class);
	final int TIME_OUT = 10000;

	public final String ZOOKEEPER_SERVER = "siserver0.local:2181,siserver1.local:2181,siserver2.local:2181";
	public ZooKeeper _zkClient;
	String content;
	String znodeRoot = "/";
	String znodePath = "/iesd";
	String znodePath_1 = "/iesd/coordination";

	CountDownLatch rendezvousPoint = new CountDownLatch(1);

	public static void main(String[] args) {
		
		LOG.info("Starting Zookeeper Client...");
		CountDownLatch waitForever = new CountDownLatch(1);

		new ZkWatchClient2();
		
		try {
			waitForever.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		LOG.info("Stoping Zookeeper Client...");
	}

	public ZkWatchClient2() {
		try {
			_zkClient = new ZooKeeper(ZOOKEEPER_SERVER, TIME_OUT, this);

			System.out.println("Waiting for client SyncConnected...");
			rendezvousPoint.await();
			
			_zkClient.exists(znodePath, this);
			_zkClient.exists(znodePath_1, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void process(WatchedEvent watchedEvent) {
		rendezvousPoint.countDown();

		switch (watchedEvent.getType()) {
		case NodeChildrenChanged:
			System.out.println("WatchedEvent:NodeChildrenChanged: " + watchedEvent.getPath());
			break;
		case NodeCreated:
			System.out.println(watchedEvent.toString());
			break;
		case NodeDataChanged:
			System.out.println("WatchedEvent: NodeChildrenChanged" + watchedEvent.getPath());
			break;
		case NodeDeleted:
			System.out.println("WatchedEvent:NodeDeleted: " + watchedEvent.getPath());
			break;
		default:
			System.out.println("WatchedEvent: UNKNOWN EVENT..." + watchedEvent.toString());
		}
		try {
			_zkClient.exists(znodePath, this);
			_zkClient.exists(znodePath_1, this);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
