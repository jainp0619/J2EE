import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PubSubChat implements MessageListener {
	public static final String TOPIC = "topic/PJPubSubChatTopic";

	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicSubscriber subscriber;
	private TopicConnection topicConnection;
	private String username;

	// Initialize the instance variable
	public void initialize(TopicConnection con, TopicSession pubSess,
			TopicPublisher pub, TopicSubscriber sub, String uname) {
		this.topicConnection = con;
		this.pubSession = pubSess;
		this.publisher = pub;
		this.subscriber = sub;
		this.username = uname;
	}

	public PubSubChat(String uname, String pwd) throws JMSException,
	NamingException {
		// Obtain a JNDI connection
		Context initialContext = PubSubChat.getInitialContext();

		// Lookup a JMS Connection factory
		TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) initialContext
				.lookup("jms/GFConnectionFactory");

		// Create a JMS connection
		TopicConnection topicConnection = topicConnectionFactory
				.createTopicConnection();

		// Creating publisher and subscriber Session object
		TopicSession pubSession = topicConnection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = topicConnection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);

		// Looking up a JMS topic
		Topic topic = (Topic) initialContext.lookup(PubSubChat.TOPIC);

		// create a JMS publisher and subscriber
		TopicPublisher publisher = pubSession.createPublisher(topic);
		TopicSubscriber subscriber = subSession.createSubscriber(topic);

		// Initialize the Chat Application
		initialize(topicConnection, pubSession, publisher, subscriber, uname);

	}

	// Specifying the JNDI properties specific to the vendor
	public static Context getInitialContext() throws JMSException,
	NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial",
				"com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs",
				"com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "http://localhost:8080");
		Context context = new InitialContext(props);
		return context;
	}

	// Receive the message from topic subscriber
	@Override
	public void onMessage(Message msg) {
		try {
			System.out.println(((TextMessage) msg).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	protected void subscribeMessage() throws JMSException {
		// set a JMS message listener
		subscriber.setMessageListener(this);
		// Start the JMS connection; allows message to be delivered
		topicConnection.start();
	}

	protected void sendMessage(String text) throws JMSException {
		TextMessage message = pubSession.createTextMessage();
		message.setText("[" + username + "]" + " : " + text);
		publisher.publish(message);
	}

	// Close the JMS Connection
	public void close() throws JMSException {
		topicConnection.close();
	}

	// Run the chat client
	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				System.out.println("Username and Password is required");
			} else {
				// args[0]=username,args[1]=password
				PubSubChat chat = new PubSubChat(args[0], args[1]);

				// Read from command line
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));

				// Looped until the word "exit" is typed
				while (true) {
					String s = br.readLine();
					if (s.equalsIgnoreCase("exit")) {
						chat.close();
						System.exit(0);
					} else {
						chat.subscribeMessage();
						chat.sendMessage(s);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
