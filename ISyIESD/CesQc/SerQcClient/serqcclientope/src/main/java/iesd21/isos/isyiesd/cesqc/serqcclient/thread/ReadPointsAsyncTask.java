package iesd21.isos.isyiesd.cesqc.serqcclient.thread;

import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.QcData;
import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.ReadWriteQcData;
import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.ReadWriteQcDataImplService;

import java.util.concurrent.Callable;

public class ReadPointsAsyncTask implements Callable<QcData> {

	QcData qcData = null;
	String eMail;
	String siteName;
	int siteId;

	public ReadPointsAsyncTask(String eMail, String siteName, int siteId) {
		this.eMail = eMail;
		this.siteName = siteName;
		this.siteId = siteId;
	}

	@Override
	public QcData call() throws InterruptedException {
		System.out.println("Start reading site: " + siteName + "[" + siteId + "]");
		if (siteId == 1) {
			Thread.sleep(5000);
			return null;
		}
		try {
			ReadWriteQcDataImplService client = new ReadWriteQcDataImplService();
			ReadWriteQcData readService = client.getReadWriteQcDataImplPort();
			qcData = readService.readQcData(eMail);
			Thread.sleep(2000 * siteId);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("End read of site: " + siteName + "  qcData.getBalance() = " + qcData.getBalance());
		return qcData;
	}
}
