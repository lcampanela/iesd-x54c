package iesd.kafka.msg.api;

import net.jini.entry.AbstractEntry;

public class QueueMetaData extends AbstractEntry {

	private static final long serialVersionUID = 1L;
	
	public String _queueId;
	public Integer _produceIndice;
	public Integer _consumeIndice;

	public QueueMetaData() {
	}

	public QueueMetaData(String queueId) {
		this(queueId, new Integer(0), new Integer(0));
	}

	public QueueMetaData(String queueId, Integer produceIndice, Integer consumeIndice) {
		this._queueId = queueId;
		this._produceIndice = produceIndice;
		this._consumeIndice = consumeIndice;
	}

	public boolean isEmpty() {
		return _produceIndice.equals(_consumeIndice);
	}

	public int getSize() {
		return _produceIndice.intValue() - _consumeIndice.intValue();
	}

	public Integer getProduceIndice() {
		return _produceIndice;
	}

	public Integer getConsumeIndice() {
		return _consumeIndice;
	}

	public void incrementConsumeIndice() {
		_consumeIndice = new Integer(_consumeIndice.intValue() + 1);
	}

	public void incrementProduceIndice() {
		_produceIndice = new Integer(_produceIndice.intValue() + 1);
	}

	public String toString() {
		return "(_queueId, _produceIndice, _consumeIndice) = (" + this._queueId + ", " + this._produceIndice
				+ ", " + this._consumeIndice + ")";
	}
}