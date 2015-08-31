package gov.nist.toolkit.installation.jms;

import net.timewalker.ffmq3.FFMQConstants;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.logging.Logger;


class MessageConsumerThread implements Runnable 
{

	private static Logger log = Logger.getLogger(MessageConsumerThread.class.getName()); 

	private String queueName = "valQueue";
  
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
	        MessageConsumer consumer = null;
        //javax.jms.QueueSession session = null;
        javax.jms.Session session = null;
        //javax.jms.QueueConnection connection = null;
		javax.jms.Connection connection = null;


		try {
			Hashtable<String,String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, FFMQConstants.JNDI_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
            Context context = new InitialContext(env);

            // Lookup a connection factory in the context
            //javax.jms.QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(FFMQConstants.JNDI_QUEUE_CONNECTION_FACTORY_NAME);
			javax.jms.ConnectionFactory factory = (ConnectionFactory) context.lookup(FFMQConstants.JNDI_CONNECTION_FACTORY_NAME);


            //connection = factory.createQueueConnection();
			connection = factory.createConnection();


			session = connection.createSession(true, javax.jms.Session.SESSION_TRANSACTED);

            /* session = connection.createQueueSession(false,
                    javax.jms.Session.AUTO_ACKNOWLEDGE);
			*/

            connection.start();

            Destination source = session.createQueue(queueName);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(source);

            // Wait for a message
            Message message = consumer.receive(1000*10);

            if (message instanceof MapMessage) {

				String data = (String)((MapMessage)message).getObject("OMElement");

				if (data!=null) {
					log.info("**** success! ****");
					log.info(data);
				}
	
				Destination replyToDestination = message.getJMSReplyTo();

				log.info("sending a response to: " + replyToDestination);
	            javax.jms.MessageProducer producer = session.createProducer(replyToDestination);

            	javax.jms.MapMessage mapMsg = session.createMapMessage();
            	mapMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            	mapMsg.setObject("status", "success!");
				

				producer.send(mapMsg);
				session.commit();


			}
			else {
				log.warning(" receive failed - not a MapMessage ");
			}


        } catch (Exception ex) { 
            log.warning(ex.toString());
        } finally {
            if (consumer!=null) {
                try {
                    consumer.close();
                } catch (Exception ex) {}
            }
            if (session!=null) {
                try {
                    session.close();
                } catch (Exception ex) {}
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (Exception ex) {}
            }

        }


}


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.setProperty("FFMQ_BASE", "..");
       try { 




		MessageConsumerThread client = new MessageConsumerThread();
		client.run();





		} catch (Throwable t) 
		{
			t.printStackTrace();
		}

    }
}

