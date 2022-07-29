package iesd21.isos.isyiesd.cesqc.serqcserver;

import iesd21.isos.isyiesd.cesqc.serqcserver.api.ReadWriteQcData;
import iesd21.isos.isyiesd.cesqc.serqcserver.models.QcData;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "iesd21.isos.isyiesd.cesqc.serqcserver.api.ReadWriteQcData")
public class ReadWriteQcDataImpl implements ReadWriteQcData {

	static Map<String, Integer> listEmails = new HashMap<String, Integer>();
	static List<QcData> listQcData = new ArrayList<QcData>();

	@Override
	public QcData readQcData(String eMail) {
		QcData qcData = null;
		System.out.println("Server: read " + eMail);
		if (listEmails.containsKey(eMail)) {
			System.out.println("listEmails[intex]; index = " + listEmails.get(eMail).intValue());
			qcData = listQcData.get(listEmails.get(eMail).intValue());
		}
		return qcData;
	}

	@Override
	public int writeQcData(String eMail, QcData qc_Data) {
		System.out.println("Server: write " + eMail);
		if (!listEmails.containsKey(eMail)) {
			if (listQcData.add(qc_Data)) {
				listEmails.put(eMail, listQcData.indexOf(qc_Data));
				System.out.println("listEmails[intex]; index = " + listEmails.get(eMail).intValue());
			} else
				return 0; // error
		} else
			return 0; // error
		return 1; // succecss
	}

	@Override
	public int deleteQcData(String eMail) {
		if (listEmails.containsKey(eMail)) {
			System.out.println("listEmails[intex]; index = " + listEmails.get(eMail).intValue());
			listQcData.remove(listEmails.get(eMail).intValue());
			listEmails.remove(eMail); // the index needs to be also removed
			return 1;
		}
		return 0;
	}

	@Override
	public List<QcData> listAllQcData() {
		// TODO Auto-generated method stub
		return listQcData;
	}
}