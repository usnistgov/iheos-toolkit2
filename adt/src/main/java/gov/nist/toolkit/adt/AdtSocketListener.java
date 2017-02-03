package gov.nist.toolkit.adt;


import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * This should only be invoked from ListenerFactory.
 */
public class AdtSocketListener implements Runnable{
    static Logger logger = Logger.getLogger(AdtSocketListener.class);
    private static String[] ackTemplate = new String[2];
    static {
        ackTemplate[0] = "MSH|^~\\&|LABADT|DH|EPICADT|DH|$timestamp$||ACK^A01^ACK |HL7ACK00001|P|2.3";
        ackTemplate[1] = "MSA|AA|HL7MSG00001";
    }

    ServerSocket server = null;
    ThreadPoolItem threadPoolItem;

    /** Creates a new instance of AdtSocketListener */
    public AdtSocketListener(ThreadPoolItem threadPoolItem) {
        this.threadPoolItem = threadPoolItem;
    }

    public void run() {
        try {
            server = new ServerSocket(threadPoolItem.port);
            if (threadPoolItem.timeoutInMilli != 0) {
                server.setSoTimeout(threadPoolItem.timeoutInMilli);
            }
            listenSocket();
        } catch (Exception e) {
            logger.error("*********************************************\n\nListen on socket " + threadPoolItem.port + " failed - " + e.getMessage() + "\n\n********************************************");
        } finally {
            try {
                server.close();
            } catch (Exception e1) {

            }
            server = null;
        }
    }

    public void listenSocket() {
        Socket socket;
        logger.info("Now listening on port: " + threadPoolItem.port);
        try {
            while (true) {
                try {
//                    logger.debug("accept - timeout is " + server.getSoTimeout());
                    socket = server.accept();
                    handle(socket);
                } catch (SocketTimeoutException e) {
//                    logger.debug("SocketTimeoutException on port " + threadPoolItem.port);
                    if (Thread.interrupted())
                        throw new InterruptedException("");
                }
            }
        } catch (InterruptedException e) {
            // This is the signal to shutdown the patientIdentityFeed
            logger.debug("Interrupted (port " + threadPoolItem.port + ")");
            threadPoolItem.release();
            logger.info("Available ports are " + ListenerFactory.availablePorts());
            return;
        } catch (IOException e) {
            logger.fatal("Error while listening for connection.", e);
            return;
        } catch (Exception e) {
            logger.fatal("Exception waiting for socket accept", e);
            return;
        } catch (Throwable e) {
            logger.fatal("Throwable waiting for socket accept", e);
            return;
        }


    }

    void handle(Socket socket) {
        boolean sendError = false;
        try {
            logger.debug("Have incoming request");
            InputStream is = null;
            OutputStream os = null;
            PrintWriter writer = null;
            //BufferedReader in = null;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                writer = new PrintWriter(os);
                // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
//                sendError = true;
                logger.fatal("Error creating InputStream/OutputStream/PrintWriter", e);
                return;
            }
            StringBuffer input = new StringBuffer();
            byte[] lastTwo = { 0x00, 0x00 };

            try {
                int i = is.read();
                while (i != -1) {
                    char c = (char) i;
                    input.append(c);
                    lastTwo[0] = lastTwo[1];
                    lastTwo[1] = (byte) i;

                    if (lastTwo[0] == 0x1c && lastTwo[1] == 0x0d)
                        break;
                    i = is.read();
                }
            } catch (IOException e) {
                sendError = true;
                logger.error(e);
            }
            try {
                InetAddress address = socket.getInetAddress();

                String addressString = address.getHostAddress();
                logger.info("Connection from: " + addressString);
            } catch (Exception e) {
                sendError = true;
                logger.error(e);
            }

            AdtMessage message = null;
            try {
                message = new AdtMessage(input.toString());
                switch (message.isValid()) {
                    case ERROR_INCORRECT_BEGINNING:
                        sendError = true;
                        break;
                    case ERROR_INCORRECT_ENDING:
                        sendError = true;
                        break;
                    case ERROR_INCORRECT_MESSAGE_TYPE:
                        sendError = true;
                        break;
                    case ERROR_INCORRECT_NUMBER_OF_LINES:
                        sendError = true;
                        break;
                    case NO_ERROR:
                        break;
                }

                try {
                    String patientId = message.getPatientId();
                    String patientName = message.getPatientName();
                    logger.info("Incoming PatientID = " + patientId + "  Patient Name = " + patientName + " SimId = " + threadPoolItem.simId);
                    threadPoolItem.pifCallback.addPatient(threadPoolItem.simId, patientId);
//                    Adt.addPatientId(threadPoolItem.simId, patientId);
                } catch (AdtMessageParseException e) {
                    sendError = true;
                    logger.error(e);
                } catch (Exception e) {
                    sendError = true;
                    logger.fatal(ExceptionUtil.exception_details(e));
                }
                if(sendError)
                    writer.write(message.getNack());
                else {
                    StringBuilder buf = new StringBuilder();

                    for (int i=0; i<ackTemplate.length; i++) {
                        buf.append(ackTemplate[i].trim()).append("\r\b");
                    }
                    String adtAckFile = AdtSocketListener.class.getResource("/adt/ACK.txt").getFile();
                    logger.info("Loading template from " + adtAckFile);

                    writer.write(0x0b);
                    writer.write(buf.toString());
                    writer.write(0x1c);
                    writer.write(0x0d);
                    //writer.write(message.getAck());
                }
                writer.flush();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                logger.error("Problem closing socket connection (already closed?)", e);
            }

        } catch (Exception e)   {
            logger.error("Cannot save anything (log or adt).  Cannot send ACK response.", e);
        }
    }
}
