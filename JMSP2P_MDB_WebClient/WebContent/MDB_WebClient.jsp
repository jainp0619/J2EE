<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="javax.naming.*" %>
<%@ page import="javax.jms.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
	System.out.println("--Entering scriplet--");
	// Obtain a JNDI connection
		Context initialContext = new InitialContext();
		System.out.println("Initial Context created..");

		// Lookup a JMS Connection factory
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext
				.lookup("jms/GFQueueConnectionFactory");
		System.out.println("Lookup of Connection Factory complete..");

		// Looking up a JMS Queue
		Queue queue = (Queue) initialContext.lookup("queue/PJMDBQueue");
		System.out.println("Lookup of Queue done...");

		// Create a JMS connection
		QueueConnection queueConnection = queueConnectionFactory
				.createQueueConnection();
		System.out.println("Connection available...");

		// Creating Session object
		QueueSession queueSession = queueConnection.createQueueSession(false,
				Session.AUTO_ACKNOWLEDGE);
		System.out.println("Session is ready...");
		
		//Creating Sender
		QueueSender sender = queueSession.createSender(queue);
		System.out.println("Sender is ready to send...");
		
		//Sending Message
		TextMessage msg = queueSession.createTextMessage("Hello from MDB Web Client");
		sender.send(msg);
		System.out.println("Message sent..");
 %>>
</body>
</html>