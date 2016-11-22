package gov.nist.toolkit.adt;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 *
 */
public class A01Sender {
    static Logger logger = Logger.getLogger(A01Sender.class);
    private static String[] A01 = new String[2];
    static {
        A01[0] = "MSH|^~\\&|EPICADT|DH|LABADT|DH|201301011226||ADT^A01|HL7MSG00001|P|2.3|";
        A01[1] = "PID|||$pid$||APPLESEED^JOHN^A^III||19710101|M||C|1^CATALYZE STREET^^MADISON^WI^53005-1020|GL|(414)379-1212|(414)271-3434||S||MRN12345001^2^M10|123456789|987654^NC|";
        A01[2] = "PV1||I";
    }

    static public void send(String server, int port, String patientId) throws IOException {
//        String templateFile = A01Sender.class.getResource("/adt/A01.txt").getFile();
//        logger.info("Loading template from " + templateFile);

        new AdtSender(A01, server, port).send(patientId);
    }
}
