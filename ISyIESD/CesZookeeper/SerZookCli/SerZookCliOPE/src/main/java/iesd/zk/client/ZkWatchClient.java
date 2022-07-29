package iesd.zk.client;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkWatchClient implements Watcher {

	protected static final Logger LOG = Logger.getLogger(ZkWatchClient.class);
	final int TIME_OUT = 10000;

	public final String ZOOKEEPER_SERVER = "siserver0.local:2181,siserver1.local:2181,siserver2.local:2181";
	public ZooKeeper _zkClient;
	String content;
	String znodeRoot = "/";
	String znodePath = "/iesd";
	String znodePath_1 = "/iesd/coordination";
	boolean sessionEstablished = false;

	public static void main(String[] args) {
		LOG.info("Starting Zookeeper Client...");

		new ZkWatchClient();

		Object keepAlive = new Object();
		synchronized (keepAlive) {
			try {
				keepAlive.wait();
			} catch (InterruptedException e) {
			}
		}
		LOG.info("Stoping Zookeeper Client...");
	}

	public ZkWatchClient() {
		try {
			_zkClient = new ZooKeeper(ZOOKEEPER_SERVER, TIME_OUT, this);
			while (!sessionEstablished) {
				System.out.print(".");
				Thread.sleep(1000);
			}
			_zkClient.exists(znodePath, this);
			_zkClient.exists(znodePath_1, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void process(WatchedEvent watchedEvent) {
		sessionEstablished = true;
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
