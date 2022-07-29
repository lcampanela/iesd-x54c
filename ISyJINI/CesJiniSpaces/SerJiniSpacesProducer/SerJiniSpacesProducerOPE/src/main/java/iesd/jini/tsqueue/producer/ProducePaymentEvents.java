package iesd.jini.tsqueue.producer;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import iesd.jini.tsqueue.api.PaymentEvent;
import iesd.jini.tsqueue.api.QueueElement;
import iesd.jini.tsqueue.api.QueueMetaData;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

public class ProducePaymentEvents {

    private static JavaSpace javaSpace;
    private static TransactionManager transactionManager;
    private static Transaction transaction;
    private static final long TIME_OUT_SECONDS = 5 * 1000;
    private static final long PAYMENT_EVENTS_SIMULATION_TIME = 5000;
    private static QueueMetaData templateQueueMetaData;
    private static QueueMetaData queueMetaData;

    private static String QUEUE_ID = "QueuePaymentEvents";

    public static void main(String[] args) throws LeaseDeniedException, RemoteException, TransactionException,
            UnusableEntryException, InterruptedException {
        System.out.println("Starting ProducePaymentEvents Main...");

        try {
            URI configFolder = new URI(ProducePaymentEvents.class.getClass()
                    .getResource("/" + ConfigureJiniFramework.JINI_CONFDIR).toString());

            File sourceDirectory = new File(configFolder);
            File dstDirectory = new File(ConfigureJiniFramework.JINI_RUNTIME_BASEPATH);

            FileUtils.copyDirectoryToDirectory(sourceDirectory, dstDirectory);

            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/all.policy");

            // System.setProperty("java.rmi.server.hostname",
            // ConfigureJiniFramework.getIpv4NonLoopbackAddress());
            System.setProperty("java.rmi.server.useCodebaseOnly", "false");

        } catch (SocketException ex) {
            Logger.getLogger(ProducePaymentEvents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(ProducePaymentEvents.class.getName()).log(Level.SEVERE, null, ex);
        }

        FindService.InitFindService();
        transactionManager = (TransactionManager) FindService.ServiceLookup(TransactionManager.class);
        javaSpace = (JavaSpace) FindService.ServiceLookup(JavaSpace.class);

        // Initialize the payemnt queue
        templateQueueMetaData = new QueueMetaData(QUEUE_ID, null, null);
        queueMetaData = (QueueMetaData) javaSpace.read(templateQueueMetaData, null, TIME_OUT_SECONDS);
        if (queueMetaData == null) {
            queueMetaData = new QueueMetaData(QUEUE_ID, new Integer(0), new Integer(0));
            System.out.println("Initializing queueMetaData = " + queueMetaData.toString());
            javaSpace.write(queueMetaData, null, Lease.FOREVER);
        } else {
            System.out.println("queueMetaData already initialized...");
        }
        int sequenceNumber = 0;
        while (true) {
            // creates a new transaction
            transaction = TransactionFactory.create(transactionManager, Lease.FOREVER).transaction;
            queueMetaData = (QueueMetaData) javaSpace.take(templateQueueMetaData, transaction, TIME_OUT_SECONDS);
            // assumes the queue "QueuePaymentEvents" will be in the tuple space
            while (queueMetaData == null) {
                queueMetaData = (QueueMetaData) javaSpace.take(templateQueueMetaData, transaction, TIME_OUT_SECONDS);
                System.out.print("p");
            }
            System.out.println("[" + sequenceNumber + "] " + "Obtained queueMetaData: " + queueMetaData.toString());

            // Obtain the indice to put the new element in the queue
            Integer produceIndice = queueMetaData.getProduceIndice();

            // creates a new payEvent and embeds it in a new queueElement
            PaymentEvent paymentEvent = new PaymentEvent("CLIENT_ID-" + sequenceNumber++, "CMIO_ID-101", "CMSP_ID-505",
                    LocalDateTime.now().toString(), "1.5");
            QueueElement queueElement = new QueueElement(QUEUE_ID, produceIndice, paymentEvent);
            javaSpace.write(queueElement, transaction, Lease.FOREVER);
            System.out.println("Produced queue element..." + queueElement.toString());

            // update queue metadata
            queueMetaData.incrementProduceIndice();
            javaSpace.write(queueMetaData, transaction, Lease.FOREVER);
            System.out.println("[" + sequenceNumber + "] " + "Updated queueMetaData: " + queueMetaData.toString());

            // closes the current transaction
            transaction.commit();
            Thread.sleep(PAYMENT_EVENTS_SIMULATION_TIME);
        }
    }
}
