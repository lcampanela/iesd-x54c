package iesd.demo.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import iesd.demo.jaxws.client.readwriteservices.QcData;

import iesd.demo.thread.ReadPointsAsyncTask;

public class QcSiteDispatcher {

	static final int N = 3;
	private static QcData qcData;

	private static List<FutureTask<QcData>> futureTasksQcData = new ArrayList<FutureTask<QcData>>();

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(N);

		int i;
		for (i = 0; i < N; i++) {
			futureTasksQcData.add((FutureTask<QcData>) executor.submit(new ReadPointsAsyncTask("440@email", "Site_" + (i + 1), i + 1)));
		}
		// Wait for ReadPointsAsyncTask results
		int answer = 0;
		while (answer < N) {
			FutureTask<QcData> futureTaskQcData = futureTasksQcData.get(answer);
			if (futureTaskQcData.isCancelled()) {
				answer++;
				System.out.println("Canceled: [" + answer + "] " + futureTaskQcData.toString());
			} else if (futureTaskQcData.isDone()) {
				qcData = futureTaskQcData.get();
				System.out.println("Done [" + answer + "] " + futureTaskQcData.toString());
				if (qcData != null) {
					System.out.println("Returned -> QcData.balance.get() = " + qcData.getBalance());
				}
				answer++;
			}
		}
		System.out.println("Recebidas " + answer + " respostas");
		System.out.println("allFinished()= " + allFinished(futureTasksQcData));
		executor.shutdown();
	}

	// Retorna true se terminaram todas as tasks em futureTasksQcData,
	// possivelmente por falha
	private static boolean allFinished(List<FutureTask<QcData>> futureTasksQcData)
			throws InterruptedException, ExecutionException {
		int terminated = 0;
		Iterator<FutureTask<QcData>> it = futureTasksQcData.iterator();
		FutureTask<QcData> futureTaskQcData;
		while (it.hasNext()) {
			futureTaskQcData = (FutureTask<QcData>) it.next();
			// uma task pode ter terminado ou ter sido cancelada
			if (futureTaskQcData.isDone() || futureTaskQcData.isCancelled()) {
				terminated++;
			}
		}
		return terminated == N; // retorna true quando todas as tasks terminaram
	}
}
