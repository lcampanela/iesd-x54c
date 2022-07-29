package iesd.demo.main;

import iesd.asyncwebservices.models.QcData;
import iesd.asyncwebservices.models.QcDataList;
import iesd.jaxrs.proxy.QcDataRsProxy;

public class TestaQcDataTransactions {

	public static void main(String[] args) {
		QcDataRsProxy service;

		System.out.println("TestaQcDataTransactions...");
		service = new QcDataRsProxy();
		QcData qcData = new QcData();
		String[] emails = { "Paula@pembox.net", "Maria@pembox.net", "Luisa@pembox.net", "João@pembox.net",
				"Pedro@pembox.net" };
		qcData.setEmail("to be defined");
		qcData.setClientId(440);
		qcData.setBalance(12500);
		qcData.setTag(5555);

		System.out.println("========== /writeQcData");
		for (int i = 0; i < emails.length; i++) {
			String email = emails[i];
			qcData.setEmail(email);
			int result = service.writeQcData(qcData);
			System.out.println("writeQcData(qcData) -> email = " + email + ", result = " + result);
		}

		// Altera os atributos do elemento indice 2
		System.out.println("========== /updateQcData");
		qcData.setEmail(emails[2]); // Alteração dos dados da Luisa
		qcData.setClientId(355);
		qcData.setBalance(34800);
		qcData.setTag(1010);
		int result = service.updateQcData(qcData);
		System.out.println("writeQcData(qcData) -> email = " + qcData.getEmail() + ", result = " + result);

		// Obtem um elemento, readQcData
		System.out.println("========== /readQcData/{email}");
		qcData = service.readQcData("Maria@pembox.net");
		System.out.println("received qcData = " + qcData);

		// Lista os elementos
		System.out.println("========== /listAllQcData");
		QcDataList qcDataList = service.listAllQcData();
		System.out.println("listAllQcData:");
		qcDataList.getQcDataList().forEach(System.out::println);
	}
}
