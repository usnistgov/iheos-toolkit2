package gov.nist.toolkit.adt;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AdtSender {
    private static Logger logger = Logger.getLogger(AdtSender.class);
    private String server;
    private int port;
    private String[] message;

    public AdtSender(String[] message, String server, int port) {
        this.message = message;
        this.server = server;
        this.port = port;
    }

    public void send(String pid) throws IOException {
        logger.debug("Sending Patient ID " + pid + " to " + server + ":" + port + "...");
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(server, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
            logger.debug("...no errors");
        } catch (UnknownHostException e) {
            logger.error("Don't know about host: " + server);
            throw e;
//            return;
        } catch (IOException e) {
            logger.error("Couldn't get I/O for "
                    + "the connection to: " + server, e);
            throw e;
//            return;
        }

        char c;

        c = 0x0b;
        out.print(c);

        StringBuilder buf = new StringBuilder();

        for (int i=0; i<message.length; i++) {
            buf.append(message[i].trim()).append("\r");
        }

        out.print(buf.toString().replace("$pid$", pid));

        c = 0x1c;
        out.print(c);

        c = 0x0d;
        out.print(c);
        out.flush();

        in.readLine();

        out.close();
        in.close();
        echoSocket.close();
    }

}


