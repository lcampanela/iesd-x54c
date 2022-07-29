package iesd.kafka.msg.api;

import net.jini.entry.AbstractEntry;

public class QueueElement extends AbstractEntry {

	private static final long serialVersionUID = 1L;
	
	public String _queueId;
	public Integer _elementIndice; 
	public Object _elementData;

	public QueueElement() {
	}

	public QueueElement(String elementId, Integer elementIndice) {
		this(elementId, elementIndice, null);
	}

	public QueueElement(String queueId, Integer elementIndice, Object elementData) {
		this._queueId = queueId;
		this._elementIndice = elementIndice;
		this._elementData = elementData;
	}
	
	public String toString() {
		return "(_queueId, _elementIndice, _elementIndice) = (" + this._queueId + ", " + this._elementIndice.toString()
				+ ", " + this._elementData.toString() + ")";
	}
}
