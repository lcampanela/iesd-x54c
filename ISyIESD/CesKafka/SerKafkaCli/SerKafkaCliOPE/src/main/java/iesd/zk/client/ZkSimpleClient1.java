package iesd.zk.client;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZkSimpleClient1 implements Watcher {

    protected final Logger LOG = Logger.getLogger(ZkSimpleClient1.class);

    final String ZOOKEEPER_SERVER = "siserver0:2181,siserver1:2181,siserver2:2181,siserver3:2181,siserver4:2181";
    final int TIME_OUT = 10000;
    ZooKeeper _zkClient;
    List<ACL> acl = null;
    Stat stat = null;
    boolean sessionEstablished = false;
    List<String> children;

    public ZkSimpleClient1 () throws UnsupportedEncodingException {
        LOG.info("Starting Zookeeper Client...");
        byte[] content;
        String znodeRoot = "/";
        String znodePath = "/iesd";
        String znodePath_1 = "/iesd/coordination";
        byte[] znodeContent = "HelloWorld...".getBytes();
        byte[] znodeContent_1 = "The zookeeper system".getBytes();
        
        try {
            _zkClient = new ZooKeeper(ZOOKEEPER_SERVER, TIME_OUT, this);
            while (!sessionEstablished) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            _zkClient.create(znodePath, znodeContent, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            _zkClient.create(znodePath_1, znodeContent_1, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            
            content = _zkClient.getData(znodePath, true, stat);
            System.out.println("The content of znode: " + znodePath + " is " + new String(content));
            _zkClient.setData(znodePath, "Olá Mundo...".getBytes(), 0);
            content = _zkClient.getData(znodePath, true, stat);
            System.out.println("After changed znode: " + znodePath + " is " + new String(content));
            
            content = _zkClient.getData(znodePath_1, true, stat);
            System.out.println("The content of znode: " + znodePath_1 + " is " + new String(content));

            children = _zkClient.getChildren(znodeRoot, true);
            System.out.println("The  children of : " + znodeRoot + " is " + children.toString());
            
            System.out.println("Sleeping 20 seconds...");
            Thread.sleep(20000);
            _zkClient.delete(znodePath_1, 0);
            _zkClient.delete(znodePath, 1);
        }
        catch ( Exception e ) {
			e.printStackTrace();
        } finally {
        	try {
				_zkClient.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        LOG.info("Stoping Zookeeper Client...");
    }
    
	public static void main(String[] args) {
		try {
			new ZkSimpleClient1();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
	        System.out.println("WatchedEvent:NodeCreated: " + watchedEvent.toString());
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
	}
}
