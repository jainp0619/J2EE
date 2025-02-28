import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.NamingException;

public class P2PReceiver implements MessageListener{
	public static final String QUEUE = "queue/PJP2PQueue";

	public static void main(String[] args) throws JMSException,
	NamingException, InterruptedException {
		System.out.println("----Entering JMS P2PReceiver----");

		// Obtain a JNDI connection
		Context initialContext = P2PSender.getInitialContext();
		System.out.println("Initial Context created..");

		// Lookup a JMS Connection factory
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext
				.lookup("jms/GFQueueConnectionFactory");
		System.out.println("Lookup of Connection Factory complete..");

		// Looking up a JMS Queue
		Queue queue = (Queue) initialContext.lookup(P2PReceiver.QUEUE);
		System.out.println("Lookup of Queue done...");

		// Create a JMS connection
		QueueConnection queueConnection = queueConnectionFactory
				.createQueueConnection();
		System.out.println("Connection available...");

		// Creating Session object
		QueueSession queueSession = queueConnection.createQueueSession(false,
				Session.AUTO_ACKNOWLEDGE);
		System.out.println("Session is ready...");

		// Creating Receiver and setting a JMS message listener
		queueSession.createReceiver(queue).setMessageListener(
				new P2PReceiver());
		System.out.println("P2PReceiver is ready to receive...");

		// Starting the Connection
		queueConnection.start();

		while (true) {
			Thread.sleep(1);
		}

	}

	@Override
	public void onMessage(Message msg) {
		try {
			System.out.println("Incoming msgs for P2PReceiver: "
					+ ((TextMessage) msg).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
