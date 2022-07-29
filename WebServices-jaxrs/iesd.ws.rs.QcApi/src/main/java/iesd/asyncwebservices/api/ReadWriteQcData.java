package iesd.asyncwebservices.api;

import iesd.asyncwebservices.models.QcData;
import iesd.asyncwebservices.models.QcDataList;

public interface ReadWriteQcData {
	// @GET
	String ping();
	
	// @GET
	String ping(String msg);
	
	// @GET
    QcData readQcData(String eMail);

    // @POST
    int writeQcData(QcData qcData);

    // @PUT
    int updateQcData(QcData qcData);

    // @DELETE
    int deleteQcData(String eMail);

    // @GET
    QcDataList listAllQcData();
}
