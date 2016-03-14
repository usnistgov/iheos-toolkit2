package gov.nist.toolkit.adt;


import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class AdtSender {
    static Logger logger = Logger.getLogger(AdtSender.class);
    String templateFile = null;
    InputStream templateInputStream = null;
    String server;
    int port;

    public AdtSender(String templateFile, String server, int port) {
        this.templateFile = templateFile;
        this.server = server;
        this.port = port;
    }

    public AdtSender(InputStream templateInputStream, String server, int port) {
        this.templateInputStream = templateInputStream;
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


        String template;

        if (templateInputStream != null)
            template = Io.getStringFromInputStream(templateInputStream);
        else
            template = Io.stringFromFile(new File(templateFile));


        template = template.replace("$pid$", pid);


        out.print(template);

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


