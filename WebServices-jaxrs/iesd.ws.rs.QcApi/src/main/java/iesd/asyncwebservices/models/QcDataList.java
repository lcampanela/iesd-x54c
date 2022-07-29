package iesd.asyncwebservices.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "qcdatalist")
@XmlAccessorType(XmlAccessType.FIELD)
public class QcDataList {
	@XmlElement(name = "qcdata")
	private List<QcData> qcdatalist;

	public QcDataList() {
		qcdatalist = new ArrayList<QcData>();
	}
	
	public List<QcData> getQcDataList() {
		return qcdatalist;
	}

	public void setQcDataList(List<QcData> qcdatalist) {
		this.qcdatalist = qcdatalist;
	}
}
