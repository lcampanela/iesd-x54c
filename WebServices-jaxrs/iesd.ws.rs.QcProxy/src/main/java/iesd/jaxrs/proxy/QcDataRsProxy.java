package iesd.jaxrs.proxy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import iesd.asyncwebservices.api.ReadWriteQcData;
import iesd.asyncwebservices.models.QcData;
import iesd.asyncwebservices.models.QcDataList;

/**
 *
 * @author MDEOS
 */
public class QcDataRsProxy implements ReadWriteQcData {

    private final static String DEFAULT_BASE_URL = "http://localhost:2058/iesd/QcDataRS";
    private final String BASE_URL;

    private final Client client;

    public QcDataRsProxy() {
        this(DEFAULT_BASE_URL);
    }
    
    public QcDataRsProxy(String baseURL) {
        BASE_URL = baseURL;
        client = ClientBuilder.newClient();
    }

    @Override
    public String ping() {
        WebTarget webTarget = client.target(BASE_URL + "/ping");
        Response invocationPingResource = webTarget.request().get();
        return invocationPingResource.readEntity(String.class);
    }

    @Override
    public String ping(String msg) {
        WebTarget webTarget = client.target(BASE_URL + "/ping").path("maria");
        Response invocationPingResource = webTarget.request().get();
        return invocationPingResource.readEntity(String.class);
    }

    @Override
    public int updateQcData(QcData qcData) {
        WebTarget webTarget = client.target(BASE_URL + "/updateQcData");
        Response invocationWriteQcDataResource = webTarget.request()
                .put(Entity.entity(qcData, MediaType.APPLICATION_XML), Response.class);

        return invocationWriteQcDataResource.readEntity(Integer.class);
    }

    @Override
    public QcData readQcData(String eMail) {
        WebTarget webTarget = client.target(BASE_URL+"/readQcData/"+eMail);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.get();
        QcData qcData = response.readEntity(QcData.class);
        return qcData;
    }

    @Override
    public int writeQcData(QcData qcData) {
        WebTarget webTarget = client.target(BASE_URL + "/writeQcData");
        Response invocationWriteQcDataResource = webTarget.request()
                .post(Entity.entity(qcData, MediaType.APPLICATION_XML), Response.class);

        return invocationWriteQcDataResource.readEntity(Integer.class);
    }

    @Override
    public int deleteQcData(String eMail) {
        WebTarget webTarget = client.target(BASE_URL+"/deleteQcData/"+eMail);
        Response invocationWriteQcDataResource = webTarget.request()
                .delete(Response.class);

        return invocationWriteQcDataResource.readEntity(Integer.class);
    }

    @Override
    public QcDataList listAllQcData() {
        WebTarget webTarget = client.target(BASE_URL+"/listAllQcData");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        QcDataList qcDataList = response.readEntity(QcDataList.class);
        return qcDataList; 
    }
}
