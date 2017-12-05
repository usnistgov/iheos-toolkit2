package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.server.PropertyServiceManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class A43Sender {
	static Logger logger = Logger.getLogger(A43Sender.class);
	private static String[] A43 = new String[4];
	private static DateFormat EVN2_TIMESTAMP = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
	private static DateFormat MSG7_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	
	static {
		A43[0] = "MSH|^~\\&|$MSH3$|$MSH4$|$MSH5$|$MSH6$|$MSG7$||ADT^A43^ADT_A43|HL7MSG00001|P|2.5|";
		A43[1] = "EVN|A43|$EVN2$";
		A43[2] = "PID|||$NewXADPID$";
		A43[3] = "MRG|$OldXADPID$";
	}

	public A43Sender() {
		logger.info("Initialized " + A43Sender.class);
	}

	public void send(String server, int port, String newXADPIDString, String oldXADPIDString, String newLocalPIDString,
			String oldLocalPIDString) throws IOException, AdtMessageParseException, AdtMessageRejectedException {
		// String templateFile =
		// A01Sender.class.getResource("/adt/A43.txt").getFile();
		// logger.info("Loading template from " + templateFile);

		String[] msg = new String[4];
		msg[0] = A43[0];
		msg[1] = A43[1];
		msg[2] = A43[2];
		msg[3] = A43[3];
		
		Date now = new Date();

		PropertyServiceManager pm = new PropertyServiceManager();
		msg[0] = msg[0].replace("$MSH3$", pm.getMSH3());
		msg[0] = msg[0].replace("$MSH4$", pm.getMSH4());
		msg[0] = msg[0].replace("$MSH5$", pm.getMSH5());
		msg[0] = msg[0].replace("$MSH6$", pm.getMSH6());
		msg[0] = msg[0].replace("$MSG7$", MSG7_DATE_FORMAT.format(now));
		
		msg[1] = msg[1].replace("$EVN2$", EVN2_TIMESTAMP.format(now));

		msg[2] = msg[2].replace("$NewXADPID$", newXADPIDString);
		
		if(newLocalPIDString != null) {
			msg[2] = msg[2].concat("~");
			msg[2] = msg[2].concat(newLocalPIDString);
		}
		
		msg[2] = msg[2].concat("|| |"); /*Empty string to meet PID-5 ' ' requirement*/
		
		msg[3] = msg[3].replace("$OldXADPID$", oldXADPIDString);
		
		if(oldLocalPIDString != null) {
			msg[3] = msg[3].concat("~");
			msg[3] = msg[3].concat(oldLocalPIDString);
		}

		new AdtSender(msg, server, port).send();
	}
}
