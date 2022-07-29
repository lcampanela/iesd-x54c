package iesd.asyncwebservices.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QcData {
	String email;
	double balance;
	int tag;
	int clientId;

	QcData() {
		this.email = null;
		this.balance = 0;
		this.tag = 0;
		this.clientId = 0;
	}

	QcData(String email, double balance, int tag, int clientId) {
		this.email = email;
		this.balance = balance;
		this.tag = tag;
		this.clientId = clientId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
    @Override
    public String toString(){
        return "email: "+ email + ", balance: " + balance + ", tag: " + tag +  ", clientId: " + clientId;
     }
}
