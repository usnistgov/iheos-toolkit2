package gov.nist.toolkit.adt;

import java.io.IOException;

/**
 * Created by bill on 9/3/15.
 */
public class A01Sender {

    static public void send(String server, int port, String patientId) throws IOException {
        String templateFile = new A01Sender().getClass().getResource("/adt/A01.txt").getFile();
        new AdtSender(templateFile, server, port).send(patientId);
    }
}
