package gov.nist.toolkit.adt;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class AdtSender {
    private static Logger logger = Logger.getLogger(AdtSender.class.getName());
    private static String HL7_ACK_ACCEPTED = "AA";
    private static int MLLP_END_BLOCK = (char) 0x1c; //28
    
    private String server;
    private int port;
    private String[] message;

    public AdtSender(String[] message, String server, int port) {
        this.message = message;
        this.server = server;
        this.port = port;
    }
    
    public void send() throws IOException, AdtMessageParseException, AdtMessageRejectedException {
    	send("");
    }

    public void send(String pid) throws IOException, AdtMessageParseException, AdtMessageRejectedException {
        logger.fine("Sending Patient ID " + pid + " to " + server + ":" + port + "...");
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(server, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
            logger.fine("...no errors");
        } catch (UnknownHostException e) {
            logger.severe("Don't know about host: " + server);
            throw e;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't get I/O for "
                    + "the connection to: " + server + " at port: " + port, e);
            throw e;
        }

        char c;

        c = 0x0b;
        out.print(c);

        StringBuilder buf = new StringBuilder();

        for (int i=0; i<message.length; i++) {
            buf.append(message[i].trim()).append("\r");
        }

        out.print(buf.toString().replace("$pid$", pid));
        
        logger.fine("Sending ADT message...");
        logger.fine(buf.toString());

        c = 0x1c;
        out.print(c);

        c = 0x0d;
        out.print(c);
        out.flush();

        String output = getStringFromBufferedReader(in);

        out.close();
        in.close();
        echoSocket.close();
        
        logger.fine("ADT message response...");
        logger.fine(()->output);
 
        isSuccessful(output);
    }
 
    
	/**
	 * @param br
	 * @return
	 * @throws IOException
	 */
	private String getStringFromBufferedReader(BufferedReader br) throws IOException {

		StringBuilder sb = new StringBuilder();

		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				if(line.indexOf(MLLP_END_BLOCK) > -1) {
	                  break;
	            }
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		logger.fine(sb::toString);

		return sb.toString();
	}
    
	/**
	 * Reads MSA Segment to determine if message was accepted.
	 * 
	 * @param message
	 * @throws AdtMessageParseException
	 */
	private void isSuccessful(String message) throws AdtMessageRejectedException, AdtMessageParseException {

		AdtMessage adtMessage = new AdtMessage(message);
		String ackCode = adtMessage.getACKCode();
		if (!ackCode.equals(HL7_ACK_ACCEPTED)) {
			throw new AdtMessageRejectedException("Application returned code: '" + ackCode + "'");
		}
	}

}


