package gov.nist.toolkit.adt;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AdtSocketListener implements Runnable{
    static Logger logger = Logger.getLogger(AdtSocketListener.class);

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

        } finally {
            try {
                server.close();
            } catch (Exception e1) {

            }
            server = null;
        }
    }

    public void openServer() {
        try {
            server = new ServerSocket(threadPoolItem.port);
        } catch (IOException e) {
            logger.fatal("Error listening on port: " + threadPoolItem.port);
        }
    }
    public void closeServer() {
        try {
            server.close();
        } catch (IOException e) {
            logger.error("Problem closing server connection (already closed?)");
        }
    }

    public void listenSocket() {
        try {
            boolean sendError = false;
            Socket socket = null;
            logger.info("Now listening on port: " + threadPoolItem.port);
            try {
                while (true) {
                    try {
                        logger.info("accept - timeout is " + server.getSoTimeout());
                        socket = server.accept();
                        break;  // process the incoming request
                    } catch (SocketTimeoutException e) {
                        logger.info("SocketTimeoutException");
                        if (Thread.interrupted())
                            throw new InterruptedException("");
                    }
                }
            } catch (InterruptedException e) {
                // This is the signal to shutdown the listener
                logger.info("Interrupted (port " + threadPoolItem.port + ")");
                threadPoolItem.release();
                return;
            } catch (IOException e) {
                sendError = true;
                logger.fatal("Error while listening for connection.", e);
                return;
            } catch (Exception e) {
                logger.fatal("Exception waiting for socket accept", e);
                return;
            } catch (Throwable e) {
                logger.fatal("Throwable waiting for socket accept", e);
                return;
            }
            logger.info("Have incoming request");
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
                sendError = true;
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
                logger.fatal(e);
            }
            try {
                InetAddress address = socket.getInetAddress();

                String addressString = address.getHostAddress();
                System.out.println("Connection from: " + addressString);
            } catch (Exception e) {
                sendError = true;
                logger.fatal(e);
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
                    logger.info("Incoming PatientID = " + patientId + "  Patient Name = " + patientName);
                    Adt.addPatientId(threadPoolItem.simId, patientId);
                } catch (AdtMessageParseException e) {
                    sendError = true;
                    logger.error(e);
                }
                //         try {
                if(sendError == true)
                    writer.write(message.getNack());
                else
                    writer.write(message.getAck());
                writer.flush();
                //            writer.close();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                logger.error("Problem closing socket connection (already closed?)", e);
            }
        } catch (Exception e)   {
            logger.error("Cannot save anything (log or adt).  Cannot send ACK response.", e);
        }
    }

//    public static void main(String[] ignore) {
//        AdtSocketListener listener = new AdtSocketListener(8087);
//        listener.openServer();
//        while(true) {
//            try{
//                listener.listenSocket();
//            } catch (Exception e) {
//                System.out.println("Unknown error.  Contining to listen on port 8087.");
//                e.printStackTrace();
//                System.out.flush();
//            }
//        }
//    }
}
