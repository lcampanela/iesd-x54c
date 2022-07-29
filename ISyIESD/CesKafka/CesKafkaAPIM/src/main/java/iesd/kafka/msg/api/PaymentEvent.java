package iesd.kafka.msg.api;

import java.io.Serializable;

public class PaymentEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	public String _clientId;
	public String _cmioId; // Collaborative Mobility Infrastructure Operators (CMIO)
	public String _cmpsId; // Collaborative Mobility Services Providers (CMSP)
	public String _dateTime;
	public String _payAmount;

	public PaymentEvent() {
	}

	public PaymentEvent(String clientId, String cmioId, String cmpsId, String dateTime, String payAmount) {
		this._clientId = clientId;
		this._cmioId = cmioId;
		this._cmpsId = cmpsId;
		this._dateTime = dateTime;
		this._payAmount = payAmount;
	}
	public String toString() {
		return "(_clientId, _cmioId, _cmpsId, _dateTime, _payAmount) = (" + this._clientId + ", " + this._cmioId + ", "
				+ ", " + this._cmpsId + ", " + this._dateTime + ", " + this._payAmount + ")";
	}
}
