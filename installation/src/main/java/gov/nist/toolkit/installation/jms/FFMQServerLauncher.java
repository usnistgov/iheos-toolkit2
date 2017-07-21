package gov.nist.toolkit.installation.jms;

import net.timewalker.ffmq3.FFMQConstants;
import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.listeners.ClientListener;
import net.timewalker.ffmq3.listeners.tcp.io.TcpListener;
import net.timewalker.ffmq3.local.FFMQEngine;
import net.timewalker.ffmq3.utils.Settings;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;


class MessageProducerThread implements Runnable 
{

	private static Logger log = Logger.getLogger(MessageProducerThread.class.getName()); 

	private String queueName = "valQueue";
  
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {

        javax.jms.Connection connection = null;
        javax.jms.Session session = null;
        
        try {
	        Hashtable<String,String> env = new Hashtable<String, String>();
	        env.put(Context.INITIAL_CONTEXT_FACTORY, FFMQConstants.JNDI_CONTEXT_FACTORY);
	        env.put(Context.PROVIDER_URL, "tcp://localhost:10002"); // FFMQ server
	        Context context = new InitialContext(env);
	
	        // Lookup a connection factory in the context
	        javax.jms.ConnectionFactory factory = (ConnectionFactory) context.lookup(FFMQConstants.JNDI_CONNECTION_FACTORY_NAME);
	
	        connection = factory.createConnection();
	                

			session = connection.createSession(true, javax.jms.Session.SESSION_TRANSACTED);
	
	        
            javax.jms.Queue queue = null;
            queue = session.createQueue(queueName); 
		    javax.jms.MessageProducer producer = session.createProducer(queue);


            // Now that all setup is complete, start the Connection and send the
            // message.
            connection.start();
        
        
			SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
			  // lets create the namespace model of the Article element
    	    OMNamespace ns = soapFactory.createOMNamespace("http://ihexds.nist.gov/test/1.0/testnamespace", "article");
    	    // now create the Article element with the above namespace
    	    OMElement articleElement = soapFactory.createOMElement("Article", ns);


			// javax.jms.ObjectMessage objMessage = session.createObjectMessage(articleElement);

			javax.jms.MapMessage mapMessage = session.createMapMessage();

			mapMessage.setObject("OMElement", articleElement.toString());


			javax.jms.Destination replyToDestination; 
			replyToDestination =  session.createTemporaryQueue();

			mapMessage.setJMSReplyTo(replyToDestination);
		    
            mapMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

			log.info("sending...");

            producer.send(mapMessage);
			session.commit();

			consumeResponse(connection, session, replyToDestination);


                      
            
        } catch (Exception ex) {
            log.info("\n *************** Error inserting into the queue \n");
            log.warning(ex.toString());
            ex.printStackTrace();
        } 

 }
    
private void consumeResponse(javax.jms.Connection connection, Session session, Destination replyToDestination) throws JMSException, InterruptedException {

		log.info("Entering consumeResponse" + replyToDestination.toString());

		if (replyToDestination==null) {
			log.warning("replyToDestination is null!");
			return;
		}

        MessageConsumer consumer = null;

	try {

            consumer = session.createConsumer(replyToDestination);

            // Wait for a message
            Message message = consumer.receive(1000*10);

            if (message instanceof MapMessage) {
				  String status = (String)((MapMessage)message).getObject("status");

					log.info("**** got status code <"+ status +">from the response ****");

			}
			else {
				log.info("message is null?" + ((message==null)?"true":"false"));
			}

        } catch (Exception ex) { 
            log.warning("**** consumeResponse" + ex.toString());
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
}


/**
 TwoWayMessage
	based on
 * Embedded FFMQ sample
 */
class FFMQServerLauncher implements Runnable
{

	private static Logger log = Logger.getLogger(FFMQServerLauncher.class.getName()); 

    private FFMQEngine engine;
 
	private String queueName = "valQueue";


    
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
		ClientListener tcpListener = null;
        try
        {
            // Create engine settings
            Settings settings = createEngineSettings();
            
            // Create the engine itself
            engine = new FFMQEngine("myLocalEngineName", settings);
            //   -> myLocalEngineName will be the engine name.
            //       - It should be unique in a given JVM
            //       - This is the name to be used by local clients to establish
            //         an internal JVM connection (high performance)
            //         Use the following URL for clients :   vm://myLocalEngineName
            //
            
            // Deploy the engine
            System.out.println("Deploying engine : "+engine.getName());
            engine.deploy();
            //  - The FFMQ engine is not functional until deployed.
            //  - The deploy operation re-activates all persistent queues
            //    and recovers them if the engine was not properly closed.
            //    (May take some time for large queues)

            // Adding a TCP based client listener
            System.out.println("Starting listener ...");
            tcpListener = new TcpListener(engine,"0.0.0.0",10002,settings,null);
            tcpListener.start();



		MessageProducerThread producer = new MessageProducerThread();
		producer.run();


		
            
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
		finally 
		{
		  if (tcpListener!=null && tcpListener.isStarted()) {
            // Stopping the listener
            System.out.println("Stopping listener ...");
            tcpListener.stop();
            
            // Undeploy the engine
            System.out.println("Undeploying engine ...");
            engine.undeploy();
            //   - It is important to properly shutdown the engine 
            //     before stopping the JVM to make sure current transactions 
            //     are nicely completed and storages properly closed.
            
            System.out.println("Done.");
		 }

		}
    }
    
    private Settings createEngineSettings()
    {
        // Various ways of creating engine settings
        
        // 1 - From a properties file
        Properties externalProperties = new Properties();
//        try
//        {
//            FileInputStream in = new FileInputStream(getClass().getResource("ffmq-server.properties").getFile());
//            externalProperties.load(in);
//            in.close();
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException("Cannot load external properties",e);
//        }
//        Settings settings = new Settings(externalProperties);
        
        // 2 - Explicit Java code
        Settings settings = new Settings();

        settings.setStringProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, ".");
        settings.setStringProperty(FFMQCoreSettings.BRIDGE_DEFINITIONS_DIR, ".");
        settings.setStringProperty(FFMQCoreSettings.TEMPLATES_DIR, ".");
        settings.setStringProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, ".");
//        ...
        
        return settings;
    }


    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.setProperty("FFMQ_BASE", "..");
       try { 



        FFMQServerLauncher server = new FFMQServerLauncher();
		server.run();


		} catch (Throwable t) 
		{
			t.printStackTrace();
		}

    }
}