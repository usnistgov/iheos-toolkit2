package gov.nist.toolkit.adt;


import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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

            // Parse incoming message
            String dateDir = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(new Date());
            String inputStr = input.toString().replace('\n', '\r');
            PipeParser pipeParser = new PipeParser();
            Message msg = pipeParser.parse(inputStr);
            Terser terser = new Terser(msg);
            String msh9 = getFieldString(terser, "/MSH", 9);
            threadPoolItem.pifCallback.addhl7v2Msg(threadPoolItem.simId, input.toString(), msh9, dateDir, true);

            // Process depending on MSH-9
            String responseString = null;
            switch (msh9) {

                // PIX Query IHE ITI TF-2a 3.9 (quick and dirty, no validation)
                case "QBP^Q23^QBP_Q21":
                    String pidIn = terser.get("/QPD-3-1");
                    String mrn = generateId(pidIn);
                    String qpd = getSegmentString(terser, "/QPD");

                    String out = "MSH|^~\\&|||||||RSP^K23^RSP_K23|HL7RSP00001|P|2.5\r" +
                            "MSA|AA|HL7MSG00001\r" +
                            "QAK||OK\r" +
                            qpd + "\r" +
                            "PID|||" + mrn + "^^^&1.2.3&ISO||\r";
                    Message outMsg = pipeParser.parse(out);
                    Terser  outTerser = new Terser(outMsg);
                    outTerser.set("/MSH-3", terser.get("/MSH-5"));
                    outTerser.set("/MSH-4", terser.get("/MSH-6"));
                    outTerser.set("/MSH-5", terser.get("/MSH-3"));
                    outTerser.set("/MSH-6", terser.get("/MSH-4"));
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
                    outTerser.set("/MSH-7", df.format(new Date()));

                    outTerser.set("/MSA-2", terser.get("/MSH-10"));

                    outTerser.set("/QAK-1", terser.get("/QPD-2"));

                    String outMsgStr = outMsg.encode();
                    responseString = outMsgStr.replaceAll("\r", "\r\n");
                    writer.write(responseString);
                    writer.flush();
                    socket.shutdownOutput();
                    socket.close();
                    threadPoolItem.pifCallback.addhl7v2Msg(threadPoolItem.simId, responseString, msh9, dateDir, false);
                    break;


                // Original ADT msg processing
                default:
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
                        if (sendError) {
                            responseString = message.getNack().toString();
                            writer.write(message.getNack());
                        }
                        else {
                            StringBuilder buf = new StringBuilder();

                            for (int i = 0; i < ackTemplate.length; i++) {
                                buf.append(ackTemplate[i].trim()).append("\r\b");
                            }
                            String adtAckFile = AdtSocketListener.class.getResource("/adt/ACK.txt").getFile();
                            logger.info("Loading template from " + adtAckFile);

                            responseString = buf.toString();
                            writer.write(0x0b);
                            writer.write(buf.toString());
                            writer.write(0x1c);
                            writer.write(0x0d);
                            //writer.write(message.getAck());
                        }
                        writer.flush();
                        socket.shutdownOutput();
                        socket.close();
                        threadPoolItem.pifCallback.addhl7v2Msg(threadPoolItem.simId, responseString, msh9, dateDir, false);
                    } catch (IOException e) {
                        logger.error("Problem closing socket connection (already closed?)", e);
                    }
            }

        } catch (Exception e)   {
            logger.error("Cannot save anything (log or adt).  Cannot send ACK response.", e);
        }
    }

    /**
     * Gets entire contents of segment field as string
     * @param terser existing terser for message
     * @param segId segment ID for Segment (use leading "/" or "." as appropriate)
     * @param field field number in Segment (starting with 1
     * @return String contents of segment, including separators.
     * @throws Exception on error
     */
    private String getFieldString(Terser terser, String segId, int field) throws Exception {
        Segment seg = terser.getSegment(segId);
        String s = seg.getField(field)[0].toString();
        return s.substring(s.indexOf("[") + 1, s.length() - 1);
    }

    /**
     * Get entire contents of segment as string
     * @param terser existing terser for message
     * @param segId segment ID for Segment (use leading "/" or "." as appropriate)
     * @return String contents of segment, including separators.
     * @throws Exception on error.
     */
    private String getSegmentString(Terser terser, String segId) throws Exception  {
        Segment seg = terser.getSegment(segId);
        if (segId.startsWith("/") || segId.startsWith(".")) segId = segId.substring(1);
        String str = segId;
        for (int i = 1; i <= seg.numFields(); i++) {
            Type[] fld = seg.getField(i);
            str += "|";
            for (int j = 0; j < fld.length; j++) {
                Varies f = (Varies) fld[j];
                String s = f.toString();
                String p = s.substring(s.indexOf("[") + 1, s.length() - 1);
                str += p;
            }
        }
        return str;
    }

    /**
     * Generate a "random" id from an inputId. The same input will always generate the
     * same output unless the input is blank, which results in a random output.
     * The generated id will of the form XXX-999999.
     * @param seedId seed id
     * @return "random" generated id.
     */
    private String generateId(String seedId) {
        long seed = 0;
        for (int i=0; i<seedId.length(); i++) seed += seedId.charAt(i);
        Random rand = new Random(seed);
        String id = "";
        for (int i=0; i<10; i++) {
            if (i < 3) id += (char) ('A' + rand.nextInt(26));
            else if (i == 3) id += "-";
            else id += (char) ('0' + rand.nextInt(10));
        }
        return id;
    }
}