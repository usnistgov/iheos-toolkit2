package gov.nist.toolkit.adt;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AdtSocketListener {

    ServerSocket server = null;

    int port = 8087;

    /** Creates a new instance of AdtSocketListener */
    public AdtSocketListener(int port) {
        this.port = port;
    }
    public void openServer() {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error listening on port: " + port);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void closeServer() {
        try {
            server.close();
        } catch (IOException e) {
            System.out.println("Problem closing server connection (already closed?)");
            e.printStackTrace();
        }
    }

    public void listenSocket() {
        try {
            boolean sendError = false;
            Socket socket = null;
            try {
                System.out.println("Now listening on port: " + port);
                System.out.flush();
                socket = server.accept();
            } catch (IOException e) {
                sendError = true;
                System.out.println("Error while listening for connection.");
                e.printStackTrace();
                System.out.flush();
                return;
            }
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
                System.out.println("Error creating InputStream/OutputStream/PrintWriter");
                e.printStackTrace();
                System.out.flush();
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
                e.printStackTrace();
                System.out.flush();
            }
            try {
                InetAddress address = socket.getInetAddress();

                String addressString = address.getHostAddress();
                System.out.println("Connection from: " + addressString);
            } catch (Exception e) {
                sendError = true;

                e.printStackTrace();
                System.out.flush();
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
                    System.out.println("Incoming PatientID = " + patientId + "  Patient Name = " + patientName);
                    System.out.flush();
                        if(true /*  new patient id */) {
                            // save it here
                            System.out.println("New Patient!");
                            System.out.flush();
                        } else {
                            System.out.println("Patient ID already exists.  Throwing away...");
                            System.out.flush();
                        }

                } catch (AdtMessageParseException e) {
                    sendError = true;
                    e.printStackTrace();
                    System.out.flush();
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
                System.out.println("Problem closing socket connection (already closed?)");
                e.printStackTrace();
                System.out.flush();
            }
        } catch (Exception e )   {
            System.out.println("Cannot save anything (log or adt).  Cannot send ACK response.");
            e.printStackTrace();
            System.out.flush();
        }
    }

    public static void main(String[] ignore) {
        AdtSocketListener listener = new AdtSocketListener(8087);
        listener.openServer();
        while(true) {
            try{
                listener.listenSocket();
            } catch (Exception e) {
                System.out.println("Unknown error.  Contining to listen on port 8087.");
                e.printStackTrace();
                System.out.flush();
            }
        }
    }
}
