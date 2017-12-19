package gov.nist.toolkit.adt;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.installation.PropertyServiceManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;

/**
 *
 */
public class A01Sender {
    static Logger logger = Logger.getLogger(A01Sender.class);
    private static String[] A01 = new String[4];
    static {
        A01[0] = "MSH|^~\\&|$MSH3$|$MSH4$|$MSH5$|$MSH6$|201301011226||ADT^A01|HL7MSG00001|P|2.3|";
        A01[1] = "EVN||20090224104145-0600";
        A01[2] = "PID|||$pid$||APPLESEED^JOHN^A^III||19710101|M||C|1^CATALYZE STREET^^MADISON^WI^53005-1020|GL|(414)379-1212|(414)271-3434||S||MRN$MRN$^2^M10|123456789|987654^NC|";
        A01[3] = "PV1||I";
    }

    static public void send(String server, int port, String patientId) throws IOException {
//        String templateFile = A01Sender.class.getResource("/adt/A01.txt").getFile();
//        logger.info("Loading template from " + templateFile);

        Date date = new Date();
        String mrn = Installation.asFilenameBase(date);
        mrn = mrn.replaceAll("_", "").substring(8);

        String[] msg = new String[4];
        msg[0] = A01[0];
        msg[1] = A01[1];
        msg[2] = A01[2];
        msg[3] = A01[3];

        PropertyServiceManager pm = new PropertyServiceManager();
        msg[0] = msg[0].replace("$MSH3$", pm.getMSH3());
        msg[0] = msg[0].replace("$MSH4$", pm.getMSH4());
        msg[0] = msg[0].replace("$MSH5$", pm.getMSH5());
        msg[0] = msg[0].replace("$MSH6$", pm.getMSH6());
        msg[2] = msg[2].replace("$MRN$", mrn);

        new AdtSender(msg, server, port).send(patientId);
    }
}
