package iesd21.isos.isyiesd.cesqc.serqcclient.thread;

import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.QcData;
import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.ReadWriteQcData;
import iesd21.isos.isyiesd.cesqc.serqcclient.Qc.ReadWriteQcDataImplService;

import java.util.concurrent.Callable;

public class WritePointsAsyncTask implements Callable<Integer> {

	QcData qcData = null;
	int result = 0;
	String siteName;

	public WritePointsAsyncTask(String siteName) {
		this.siteName = siteName;
	}

	@Override
	public Integer call() {
		System.out.println("Start writing site: " + siteName);
		try {
			ReadWriteQcDataImplService client = new ReadWriteQcDataImplService();
			ReadWriteQcData writeService = client.getReadWriteQcDataImplPort();
			writeService.writeQcData("1@email", qcData);
			Thread.currentThread();
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("End write of site: " + siteName);
		return result;
	}
}
