/*
 * AdtSocketListener.java
 *
 * Created on December 3, 2004, 4:06 PM
 */

package gov.nist.toolkit.common.adt;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


// No longer logging -- 07/15/2005
/*
import gov.nist.registry.xds.log.JdbcConnection;
import gov.nist.registry.xds.log.BaseEntry;
import gov.nist.registry.xds.log.MessageEntry;
import gov.nist.registry.xds.log.TestEntry;
*/

/**
 *
 * @author  mccaffrey
 */
public class AdtSocketListener {
    
    ServerSocket server = null;
    AdtJdbcConnection adtConnection = null;
    
//    JdbcConnection logConnection = null;
// No longer logging -- 07/15/2005
    
    static final private int port = 8087;
    
    /** Creates a new instance of AdtSocketListener */
    public AdtSocketListener() {
        
    }
    public void openServer() {
        try {
            server = new ServerSocket(this.port);
        } catch (IOException e) {
            System.out.println("Error listening on port: " + this.port);
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
                System.out.println("Now listening on port: " + this.port);
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
//                    System.out.format("0x%x ", i);
                    char c = (char) i;
//                    System.out.print("(" + c + ") ");
                    input.append(c);
                    lastTwo[0] = lastTwo[1];
                    lastTwo[1] = (byte) i;
                    
                    if (lastTwo[0] == 0x1c && lastTwo[1] == 0x0d)
                    	break;
                    
//                    if(new String(lastTwo).equals(new String(AdtMessage.end)))
//                        break;
                    i = is.read();
                }
            } catch (IOException e) {
                sendError = true;
                e.printStackTrace();
                System.out.flush();
            }
   //         BaseEntry base = null;
            try {
   //             base = new BaseEntry();
                
                InetAddress address = socket.getInetAddress();
                
                String addressString = address.getHostAddress();  //new String(address.getAddress());
     //           base.setIp(new String(addressString));
     //           base.save();
                System.out.println("Connection from: " + addressString);
            } catch (Exception e) {
                sendError = true;
                
                e.printStackTrace();
                System.out.flush();
                
                //            throw new Exception("AdtSocketListener: listenSocket(): " + e.getMessage());
            }
            
            //        base.setIp("127.0.0.1");
            //      System.out.println(input.toString());
            
            AdtMessage message = null;
            try {
                message = new AdtMessage(input.toString());
    //            new MessageEntry(base,"ADT Full Message",input.toString());
                switch (message.isValid()) {
                    case ERROR_INCORRECT_BEGINNING:
                        sendError = true;
        //                new MessageEntry(base,"Parsing Error","Incorrect beginning (should be (" + AdtMessage.start.toString() + ")");
        //                new TestEntry(base,-20,false,null);
                        break;
                    case ERROR_INCORRECT_ENDING:
                        sendError = true;
         //               new MessageEntry(base,"Parsing Error","Incorrect ending -- should be (" + AdtMessage.end.toString() + ")");
          //              new TestEntry(base,-20,false,null);
                        break;
                    case ERROR_INCORRECT_MESSAGE_TYPE:
                        sendError = true;
        //                new MessageEntry(base,"Parsing Error","Incorrect Message Type");
        //                new TestEntry(base,-20,false,null);
                        break;
                    case ERROR_INCORRECT_NUMBER_OF_LINES:
                        sendError = true;
         //               new MessageEntry(base,"Parsing Error","Incorrect number of lines");
         //               new TestEntry(base,-20,false,null);
                        break;
                    case NO_ERROR:
         //               new MessageEntry(base,"Parsing", "No Errors");
                        break;
                }
                
//            } catch (java.net.SocketException e) {
//                sendError = true;
 //               System.out.println("Local socket error.  (Check connection to database(s).)");
 //               e.printStackTrace();
//            } catch (java.io.IOException e) {
  //              sendError = true;
    //            System.out.println("IO Error.  Check file!");
      //          e.printStackTrace();
        //    }
            try {
                
                String patientId = message.getPatientId();
                String patientName = message.getPatientName();
                System.out.println("Incoming PatientID = " + patientId + "  Patient Name = " + patientName);
                System.out.flush();
                try {
                    adtConnection = new AdtJdbcConnection();
                    //            logConnection = new JdbcConnection();
                    if(!adtConnection.doesIdExist(patientId)) {
                    	adtConnection.addAdtRecord(message.getAdtRecord());
//                        adtConnection.addIdName(patientId, patientName);
                        System.out.println("New Patient!");
                        System.out.flush();
                    } else {
                        System.out.println("Patient ID already exists.  Throwing away...");
                        System.out.flush();
                    }
                    
                    adtConnection.close();
       //             try {
       //                 new TestEntry(base,-20,true,null);
       //             } catch (java.io.IOException ioe) {
       //                 System.out.println("IO Error.  Check file.");
       //                 ioe.printStackTrace();
       //             }
                } catch (java.sql.SQLException sqle) {
                    sendError = true;
                    sqle.printStackTrace();
                    System.out.flush();
                }
                
                
                //           new MessageEntry(base,"ADT Full Message",input.toString());
       //         new MessageEntry(base,"ADT ID/Name",patientId + "/" + patientName);
                
//            } catch (java.sql.SQLException e) {
  //              sendError = true;
    //            e.printStackTrace();
            } catch (AdtMessageParseException e) {
                sendError = true;
     //           new MessageEntry(base,"Exception Message",e.getMessage());
      //          try{
      //              new TestEntry(base,-20,true,null);
//                } catch (java.io.IOException ioe) {
  //                  sendError = true;
    //                System.out.println("IO Error.  Check file.");
      //              ioe.printStackTrace();
        //        }
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
  //      } catch (java.sql.SQLException sqle) {
        } catch (Exception e )   {      
            System.out.println("Cannot save anything (log or adt).  Cannot send ACK response.");
           e.printStackTrace();
           System.out.flush();
            
            
        }
    }
    
    
    
    public static void main(String[] ignore) {
        AdtSocketListener listener = new AdtSocketListener();
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
