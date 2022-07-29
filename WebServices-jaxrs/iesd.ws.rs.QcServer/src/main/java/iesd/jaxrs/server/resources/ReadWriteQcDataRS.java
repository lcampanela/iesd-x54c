package iesd.jaxrs.server.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import iesd.asyncwebservices.api.ReadWriteQcData;
import iesd.asyncwebservices.models.QcData;
import iesd.asyncwebservices.models.QcDataList;

@Path("/QcDataRS")
public class ReadWriteQcDataRS implements ReadWriteQcData {

    @Context
    private UriInfo context;
    static Map<String, Integer> listEmails = new HashMap<String, Integer>();
    static QcDataList _qcDataList = new QcDataList();

    @GET
    @Path("/ping")
    @Produces({MediaType.TEXT_PLAIN})
    public String ping() {
        System.out.println("context.getAbsolutePath() = " + context.getAbsolutePath());
        return "ReadWriteQcDataRS.ping() is alive...";
    }

    @GET
    @Path("ping/{msg}")
    @Produces("text/plain")
    public String ping(@PathParam("msg") String msg) {
        System.out.println("context.getAbsolutePath() = " + context.getAbsolutePath());
        System.out.println("msg = " + msg);
        return "ReadWriteQcDataRS.ping(String msg) is alive...msg = " + msg;
    }

    @GET
    @Path("/readQcData/{email}")
    @Produces(MediaType.APPLICATION_XML)
    @Override
    public QcData readQcData(@PathParam("email") String eMail) {
        List<QcData> qcDataList = _qcDataList.getQcDataList();
        QcData qcData = null;
        System.out.println("readQcData()... eMail =  " + eMail);
        if (listEmails.containsKey(eMail)) {
            qcData = qcDataList.get(listEmails.get(eMail).intValue());
        }
        return qcData;
    }

    @POST
    @Path("/writeQcData")
    @Consumes({MediaType.APPLICATION_XML})
    @Override
    public int writeQcData(QcData qcData) {
        String eMail = qcData.getEmail();
        List<QcData> qcDataList = _qcDataList.getQcDataList();
        System.out.println("writeQcData()... eMail =  " + eMail);
        if (!listEmails.containsKey(eMail)) {
            if (qcDataList.add(qcData)) {
                listEmails.put(eMail, qcDataList.indexOf(qcData));
                _qcDataList.setQcDataList(qcDataList);
            } else {
                return 0; // error
            }
        } else {
            return 0; // error
        }
        return 1; // success
    }

    @PUT
    @Path("/updateQcData")
    @Consumes({MediaType.APPLICATION_XML})
    public int updateQcData(QcData qcData) {
        String eMail = qcData.getEmail();
        List<QcData> qcDataList = _qcDataList.getQcDataList();
        System.out.println("updateQcData()... eMail =  " + eMail);
        if (listEmails.containsKey(eMail)) {
            qcDataList.set(listEmails.get(eMail).intValue(), qcData);
            _qcDataList.setQcDataList(qcDataList);
            return 1; // success
        } else {
            return 0; // error
        }
    }

    @DELETE
    @Path("/deleteQcData/{email}")
    @Produces({MediaType.TEXT_XML})
    @Override
    public int deleteQcData(@PathParam("email") String eMail) {
        List<QcData> qcDataList = _qcDataList.getQcDataList();
        System.out.println("deleteQcData()... eMail =  " + eMail);
        if (listEmails.containsKey(eMail)) {
            qcDataList.remove(listEmails.get(eMail).intValue());
            _qcDataList.setQcDataList(qcDataList);
            return 1;
        }
        return 0;
    }

    @GET
    @Path("/listAllQcData")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}) // pode usar também "application/xml"
    @Override
    public QcDataList listAllQcData() {
        List<QcData> qcDataList = _qcDataList.getQcDataList();
        System.out.println("listAllQcData()...");
        qcDataList.forEach(item -> System.out.println(item));
        return _qcDataList;
    }
}
