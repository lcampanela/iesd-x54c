package iesd.asyncwebservices.server;

import javax.xml.ws.Endpoint;

public class SiteServer {

	public static void main(String[] args) {
		Endpoint ep = Endpoint.create(new ReadWriteQcDataImpl());
		System.out.println("Starting SiteServer...");
		ep.publish("http://localhost:2058/QcDataServer");
	}
}
