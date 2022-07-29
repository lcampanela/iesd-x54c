package iesd21.isos.isyiesd.cesqc.serqcserver.api;

import iesd21.isos.isyiesd.cesqc.serqcserver.models.QcData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.util.List;

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
