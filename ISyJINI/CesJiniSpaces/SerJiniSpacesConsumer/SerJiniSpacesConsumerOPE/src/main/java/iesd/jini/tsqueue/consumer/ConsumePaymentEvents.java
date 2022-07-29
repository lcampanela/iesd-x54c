package iesd.jini.tsqueue.consumer;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import iesd.jini.tsqueue.api.QueueElement;
import iesd.jini.tsqueue.api.QueueMetaData;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

public class ConsumePaymentEvents {

    private static JavaSpace javaSpace = null;
    private static TransactionManager transactionManager;
    private static final long TIME_OUT_SECONDS = 5 * 1000;
    private static QueueMetaData templateQueueMetaData;
    private static String QUEUE_ID = "QueuePaymentEvents";
    private static QueueMetaData queueMetaData;

    public static void main(String[] args) throws LeaseDeniedException, RemoteException {
        System.out.println("Starting ConsumePaymentEvents Main...");
        try {
            URI configFolder = new URI(ConsumePaymentEvents.class.getClass()
                    .getResource("/" + ConfigureJiniFramework.JINI_CONFDIR).toString());

            File sourceDirectory = new File(configFolder);
            File dstDirectory = new File(ConfigureJiniFramework.JINI_RUNTIME_BASEPATH);

            FileUtils.copyDirectoryToDirectory(sourceDirectory, dstDirectory);

            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/all.policy");

            // System.setProperty("java.rmi.server.hostname",
            // ConfigureJiniFramework.getIpv4NonLoopbackAddress());
            System.setProperty("java.rmi.server.useCodebaseOnly", "false");

        } catch (SocketException ex) {
            Logger.getLogger(ConsumePaymentEvents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(ConsumePaymentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }

        FindService.InitFindService();
        transactionManager = (TransactionManager) FindService.ServiceLookup(TransactionManager.class);
        javaSpace = (JavaSpace) FindService.ServiceLookup(JavaSpace.class);

        // Initialize the queue metadata template
        templateQueueMetaData = new QueueMetaData(QUEUE_ID, null, null);

        // obtain PaymentEvents from the paymentEvent, forever
        int sequenceNumber = 0;
        while (true) {
            try {
                // creates a new transaction
                Transaction transaction = TransactionFactory.create(transactionManager, Lease.FOREVER).transaction;
                System.out.println("transaction:" + transaction.toString());

                queueMetaData = (QueueMetaData) javaSpace.take(templateQueueMetaData, transaction, TIME_OUT_SECONDS);
                while (queueMetaData == null) {
                    queueMetaData = (QueueMetaData) javaSpace.take(templateQueueMetaData, transaction, TIME_OUT_SECONDS);
                    System.out.print("c");
                }
                System.out.println("[" + sequenceNumber++ + "] " + "Obtained queueMetaData: " + queueMetaData.toString());
                if (queueMetaData._consumeIndice < queueMetaData._produceIndice) {

                    // Obtain the indice to put the new element in the queue
                    Integer consumeIndice = queueMetaData.getConsumeIndice();

                    // creates a new queue element and puts there the paymentElement
                    QueueElement templateQueueElement = new QueueElement(QUEUE_ID, consumeIndice);

                    // Obtain a queue element when available
                    QueueElement queueElement = (QueueElement) javaSpace.take(templateQueueElement, transaction,
                            TIME_OUT_SECONDS);
                    if (queueElement != null) {
                        // shows the payment element attributes
                        System.out.println(
                                "[" + consumeIndice + "] " + "queueElement obtained: " + queueElement.toString());

                        // update queue metadata
                        queueMetaData.incrementConsumeIndice();
                        javaSpace.write(queueMetaData, transaction, Lease.FOREVER);

                        // closes the current transaction
                    }
                }
                transaction.commit();
                Thread.sleep(TIME_OUT_SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
