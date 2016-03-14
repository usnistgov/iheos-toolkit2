package gov.nist.toolkit.valregmsg.registry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;



public class Properties {
	java.util.Properties properties = null;
	private final static Logger logger = Logger.getLogger(Properties.class);
	private static Properties properties_object = null;

	static {
		BasicConfigurator.configure();
	}
	
	private Properties() {  init(); }
	
	public static Properties loader() {
		if (properties_object == null) properties_object = new Properties();
		return properties_object;
	}
	
	void init() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("xds.properties");
		if (is == null) { logger.fatal("Cannot load xds.properties" ); return; }
		properties = new java.util.Properties();
		try {
			properties.load(is);
		}
		catch (Exception e) {
			logger.fatal(exception_details(e));
		}
	}

	public String getString(String name) {
		return properties.getProperty(name);
	}
	
	public boolean getBoolean(String name) {
		return (properties.getProperty(name).equals("false")) ? false : true;
	}

	public static String exception_details(Exception e) {
		if (e == null) 
			return "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);

		return "Exception thrown: " + e.getClass().getName() + "\n" + e.getMessage() + "\n" + new String(baos.toByteArray());
	}

}
