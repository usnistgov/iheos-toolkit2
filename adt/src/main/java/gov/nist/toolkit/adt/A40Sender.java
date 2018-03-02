package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.PropertyServiceManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class A40Sender {
	static Logger logger = Logger.getLogger(A40Sender.class);
	private static String[] A40 = new String[4];
	private static DateFormat EVN2_TIMESTAMP = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
	private static DateFormat MSG7_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	
	static {
		A40[0] = "MSH|^~\\&|$MSH3$|$MSH4$|$MSH5$|$MSH6$|$MSG7$||ADT^A40^ADT_A39|HL7MSG00001|P|2.3.1|";
		A40[1] = "EVN|A40|$EVN2$";
		A40[2] = "PID|||$NewXADPID$||APPLESEED^JOHN^A^III^^^A|";
		A40[3] = "MRG|$OldXADPID$|";
	}

	public A40Sender() {
		logger.info("Initialized " + A40Sender.class);
	}

	public void send(String server, int port, String newXADPIDString, String oldXADPIDString) 
			throws IOException, AdtMessageParseException, AdtMessageRejectedException {
		// String templateFile =
		// A40Sender.class.getResource("/adt/A40.txt").getFile();
		// logger.info("Loading template from " + templateFile);

		String[] msg = new String[4];
		msg[0] = A40[0];
		msg[1] = A40[1];
		msg[2] = A40[2];
		msg[3] = A40[3];
		
		Date now = new Date();

		PropertyServiceManager pm = new PropertyServiceManager();
		msg[0] = msg[0].replace("$MSH3$", pm.getMSH3());
		msg[0] = msg[0].replace("$MSH4$", pm.getMSH4());
		msg[0] = msg[0].replace("$MSH5$", pm.getMSH5());
		msg[0] = msg[0].replace("$MSH6$", pm.getMSH6());
		msg[0] = msg[0].replace("$MSG7$", MSG7_DATE_FORMAT.format(now));
		
		msg[1] = msg[1].replace("$EVN2$", EVN2_TIMESTAMP.format(now));

		msg[2] = msg[2].replace("$NewXADPID$", newXADPIDString);
		
		msg[3] = msg[3].replace("$OldXADPID$", oldXADPIDString);
		
		new AdtSender(msg, server, port).send();
	}
}
