package iesd.asyncwebservices.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import iesd.asyncwebservices.models.QcData;

@WebService(name = "ReadWriteQcData", targetNamespace = "http://iesd.asyncwebservices.qcdata")
@SOAPBinding(style = Style.DOCUMENT)
public interface ReadWriteQcData {
	@WebMethod
	QcData readQcData(String eMail);

	@WebMethod
	int writeQcData(String eMail, QcData qcData);

	@WebMethod
	int deleteQcData(String eMail);

	@WebMethod
	List<QcData> listAllQcData();
}
