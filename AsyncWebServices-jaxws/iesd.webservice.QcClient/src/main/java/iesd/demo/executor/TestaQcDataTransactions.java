package iesd.demo.executor;

import java.util.List;

import iesd.demo.jaxws.client.readwriteservices.QcData;
import iesd.demo.jaxws.client.readwriteservices.ReadWriteQcData;
import iesd.demo.jaxws.client.readwriteservices.ReadWriteQcDataImplService;

public class TestaQcDataTransactions {

	public static void main(String[] args) {
		QcData qcData;
		
        System.out.println("TestaQcDataTransactions...");
		ReadWriteQcDataImplService services = new ReadWriteQcDataImplService();
		ReadWriteQcData port = services.getReadWriteQcDataImplPort();

			qcData = new QcData();
			// escreve a maria e o pedro
			// maria
			String email = "carla@pembox.net";
			qcData.setEmail(email);
			qcData.setClientId(440);
			qcData.setBalance(12500);
			qcData.setTag(5555);
			port.writeQcData(email, qcData);
			// pedro
			email = "pedro@pembox.net";
			qcData.setEmail(email);
			qcData.setClientId(32430);
			qcData.setBalance(25650);
			qcData.setTag(5555);
			port.writeQcData(email, qcData);

			qcData = port.readQcData(email);
			System.out.println("Resultado do readEcData(): " + qcData);

			port.deleteQcData("carla@pembox.net");


			// obtem todos os elementos
			List<QcData> elementos =  port.listAllQcData();
			
/* Terá que acrescentar à classe QcData gerada no proxy, o seguinte método; pode copiar
 * do projeto iesd.webserviceQcApi:
 * 
 *    @Override
 *    public String toString(){
 *       return "email: "+ email + ", balance: " + balance + ", tag: " + tag +  ", clientId: " + clientId;
 *    }
 */
			elementos.forEach(System.out::println); // from Java 8
    }
}
