/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.server.PropertyManager;
import gov.nist.toolkit.installation.server.PropertyServiceManager;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.testengine.engine.SimulatorTransaction;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Used to pull information from NIST xdstools2 simulator logs. This is a 
 * modified version of ProcessNISTSimulatorLogs, designed to build a
 * {@link SimulatorTransaction} instance.
 */
public class PrsSimLogs {

   private static Logger log = Utility.getLog();
   
   private static Installation installation = Installation.instance();
   private static PropertyServiceManager propertyServiceManager = installation.propertyServiceManager();
   private static PropertyManager propertyManager = propertyServiceManager.getPropertyManager();

   private static SimpleDateFormat timeOfTransactionFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//   private static String externalCache = Paths.get(propertyManager.getExternalCache()).toString();
   private static String externalCache = Paths.get(installation.externalCache().getAbsolutePath()).toString();

   /**
    * Load SOAP Message components into passed {@link SimulatorTransaction} instance.
    * <p/>
    * This method will retrieve the SOAP Request and Response messages from the 
    * NIST log and load the following properties into the passed 
    * SimulatorTransaction instance (all in String format):<ul>
    * <li/> Request - The entire SOAP request (raw).
    * <li/> Response - The entire SOAP response (raw).
    * <li/> RequestHeader - The SOAP request envelope header.
    * <li/> ResponseHeader - The SOAP response envelope header.
    * <li/> RequestBody - The SOAP request envelope body. The root element
    * of this file will be the child element of {@code <env:Body>}, for example,
    * the {@code <xdsiB:RetrieveImagingDocumentSetRequest>} element.
    * <li/> ResponseBody - The SOAP response envelope header. The root 
    * element of this file will be the child element of {@code <env:Body>}, for 
    * example, the {@code <xdsiB:RetrieveDocumentSetResponse>} element.
    * <li/> Pfns - List of physical file names for files processed by the 
    * transaction. Empty if none.</ul>
    * If the SOAP Request and/or Response cannot be parsed, the corresponding
    * Header and Body files will not be present, but the raw Request and
    * Response xml files should always be present.<p/>
    * 
    * @param trn SimulatorTransaction xdstools log directory for transaction.
    * @throws Exception on error, such as IO error.
    */
   public static void loadTransaction(SimulatorTransaction trn) throws Exception {
      
      Path path = getTransactionLogDirectoryPath(trn.getSimId(), 
         trn.getTransactionType().getShortName(), trn.getPid(), trn.getTimeStamp());
      if (path == null)
         throw new Exception("No valid transactions for " + trn.getSimId()
         + " ec_dir via Paths.get is: [" + externalCache + "]"
                 + " ec_dir via installation is: [" + installation.externalCache().getAbsolutePath() + "]"
         );
      trn.setLogDirPath(path);
      
      // SOAP Request
      String soapRequest;
      try {
         String url = getRequestURL(path);
         trn.setUrl(url);
         soapRequest = getSOAPRequest(path);
         trn.setRequest(soapRequest);
         try {
            OMElement reqElement = XmlUtil.strToOM(soapRequest);
            String env = reqElement.getLocalName();
            if (env.equalsIgnoreCase("Envelope") == false)
               throw new Exception("root element not 'Envelope': [" + env + "]");
            try {
               List<OMElement> hdrElement = XmlUtil.childrenWithLocalName(reqElement, "Header"); 
               if (hdrElement.isEmpty())
                  throw new Exception("'Header' element not found");
               if (hdrElement.size() > 1)
                  throw new Exception(hdrElement.size() + " 'Header' elements found");
               String header = XmlUtil.OMToStr(hdrElement.get(0));
               trn.setRequestHeader(header);
            } catch (Exception e)  {
               String em = "SOAP Request Header error: " + e.getMessage();
               log.warn(em);
            }
            try {
               List<OMElement> bodyElement = XmlUtil.childrenWithLocalName(reqElement, "Body"); 
               if (bodyElement.isEmpty())
                  throw new Exception("'Body' element not found");
               if (bodyElement.size() > 1)
                  throw new Exception(bodyElement.size() + " 'Body' elements found");
               bodyElement = XmlUtil.children(bodyElement.get(0));
               if (bodyElement.isEmpty())
                  throw new Exception("No child elements found in 'Body' element");
               if (bodyElement.size() > 1)
                  throw new Exception(bodyElement.size() + " child elements found in 'Body' element");
                  
               String body = XmlUtil.OMToStr(bodyElement.get(0));
               trn.setRequestBody(body);
            } catch (Exception e)  {
               String em = "SOAP Request Body error: " + e.getMessage();
               log.warn(em);
            }
         } catch (Exception e)  {
            String em = "Error parsing SOAP Request: " + e.getMessage();
            log.warn(em);
         }
      } catch (Exception e) {
         String em = "Error reading SOAP Request: " + e.getMessage();
         log.warn(em);
      }
      
      // SOAP Response
      String soapResponse;
      try {
         soapResponse = getSOAPResponse(path);
         trn.setResponse(soapResponse);
         try {
            OMElement respElement = XmlUtil.strToOM(soapResponse);
            String env = respElement.getLocalName();
            if (env.equalsIgnoreCase("Envelope") == false)
               throw new Exception("root element not 'Envelope': [" + env + "]");
            try {
               List<OMElement> hdrElement = XmlUtil.childrenWithLocalName(respElement, "Header"); 
               if (hdrElement.isEmpty())
                  throw new Exception("'Header' element not found");
               if (hdrElement.size() > 1)
                  throw new Exception(hdrElement.size() + " 'Header' elements found");
               String header = XmlUtil.OMToStr(hdrElement.get(0));
               trn.setResponseHeader(header);
            } catch (Exception e)  {
               String em = "SOAP Response Header error: " + e.getMessage();
               log.warn(em);
            }
            try {
               List<OMElement> bodyElement = XmlUtil.childrenWithLocalName(respElement, "Body"); 
               if (bodyElement.isEmpty())
                  throw new Exception("'Body' element not found");
               if (bodyElement.size() > 1)
                  throw new Exception(bodyElement.size() + " 'Body' elements found");
               bodyElement = XmlUtil.children(bodyElement.get(0));
               if (bodyElement.isEmpty())
                  throw new Exception("No child elements found in 'Body' element");
               if (bodyElement.size() > 1)
                  throw new Exception(bodyElement.size() + " child elements found in 'Body' element");
               OMElement respEle = bodyElement.get(0);
               XmlUtil.truncateDocuments(respEle);
               String body = XmlUtil.OMToStr(respEle);
               trn.setResponseBody(body);
            } catch (Exception e)  {
               String em = "SOAP Response Body error: " + e.getMessage();
               log.warn(em);
            }
         } catch (Exception e)  {
            String em = "Error parsing SOAP Response: " + e.getMessage();
            log.warn(em);
         }
      } catch (Exception e) {
         String em = "Error reading SOAP Response: " + e.getMessage();
         log.warn(em);
      }
      
      Path repDir = path.resolve("Repository");
      try { 
         Utility.isValidPfn("", repDir, PfnType.DIRECTORY, "r");
      } catch (Exception e) {
         return;
      }
      List<String> pfns = new ArrayList<>();
      for (String name : repDir.toFile().list())
         pfns.add(repDir.resolve(name).toString());
      trn.setPfns(pfns);
      
   } // EO getSOAPComponents

   /**
    * Get SOAP Request message from xdstools log
    * 
    * @param dir xdstools log directory for transaction.
    * @return xml SOAP Request.
    */
   public static String getSOAPRequest(Path dir) {
      byte lt = "<".getBytes()[0];
      byte gt = ">".getBytes()[0];
      try {
         byte[] bytes = Files.readAllBytes(dir.resolve("request_body.bin"));
         int p = ByteUtils.indexOf(bytes, "Envelope".getBytes("UTF-8"), false);
         while (bytes[p] != lt)
            p-- ;
         bytes = ByteUtils.subbytes(bytes, p, false);
         p = ByteUtils.indexOf(bytes, "--MIMEBoundary".getBytes("UTF-8"), false);
         if (p == -1) {
            byte[] eoEnvelope = "</Envelope>".getBytes("UTF-8");
            p = ByteUtils.indexOf(bytes, "<Envelope".getBytes("UTF-8"), true);
            if (p != 0) {
               p = ByteUtils.indexOf(bytes, "<".getBytes("UTF-8"), true);
               int q = ByteUtils.indexOf(bytes, ":Envelope".getBytes("UTF-8"), true);
               byte[] ns = ByteUtils.subbytes(bytes, p + 1, q, true);
               eoEnvelope = ByteUtils.toBytes("</", new String(ns, "UTF-8"), ":Envelope>");
               p = ByteUtils.indexOf(bytes, eoEnvelope, true);
               if (p < 0) throw new Exception("getSOAPRequest: no MIMEBoundary or </Envelope>");
               while (bytes[p] != gt) p++; 
               p++;
            }
         }
         bytes = ByteUtils.subbytes(bytes, 0, p, false);
         String str = new String(bytes, "UTF-8").replaceAll("\\s+", " ");
         return str;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public static String getRequestURL(Path dir) {
      try {
         List<String> lines = Files.readAllLines(dir.resolve("request_hdr.txt"), Charsets.UTF_8);
         String line1 = lines.get(0);
         String[] parts = line1.split(" ");
         return parts[1];
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Pull the mime boundary string out of the message header
    * @param headerFile
    * @return
    */
   public static String getMimeBoundaryFromHeaderFile(File headerFile) {
      try {
         String str = FileUtils.readFileToString(headerFile);
         String lcStr = str.toLowerCase();
         String match = "boundary=";
         int p = lcStr.indexOf(match);
         if (p == -1)
            return null;
         p = p + match.length();
         if (p >= str.length())
            return null;
         int q;
         if (str.charAt(p) == '"') {
            q = str.indexOf('"', p+1);
            p++;
         } else {
            q = str.indexOf(' ', p+1);
            if (q == -1)
               q = str.indexOf('\t', p+1);
            if (q == -1)
               q = str.indexOf('\n', p+1);
         }
         if (q == -1)
            return null;
         return str.substring(p, q);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }
   /**
    * Get SOAP Response message from xdstools log
    * 
    * @param dir xdstools log directory for transaction.
    * @return xml SOAP Response.
    */
   public static String getSOAPResponse(Path dir) {
      try {
         String str = FileUtils.readFileToString(dir.resolve("response_body.txt").toFile());
         String mimeBoundary = getMimeBoundaryFromHeaderFile(dir.resolve("response_hdr.txt").toFile());
         int p = str.indexOf("Envelope");
         while (str.substring(p).startsWith("<") == false)
            p-- ;
         str = str.substring(p);
         p = str.indexOf(mimeBoundary);
         if (p != -1) str = str.substring(0, p);
         str = new String(str.replaceAll("\\s+", " "));
         return str;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Get SubmitObjectsRequest element from SOAP Request from xdstools log
    * 
    * @param dir xdstools log directory for transaction.
    * @return xml SubmitObjectsRequest element and contents.
    */
   public static String getSOAPMetaData(Path dir) {
      try {
         //String tag = "SubmitObjectsRequest";
         String tag = "Envelope";
         String str = getSOAPRequest(dir);
         int p = str.indexOf(tag);
         if (p < 0) {
            log.debug("getSOAPMetaData: No SubmitObjectsRequest element found");
            return null;
         }
         while (str.substring(p).startsWith("<") == false)
            p-- ;
         str = str.substring(p);
         p = str.lastIndexOf(tag);
         while (str.substring(p).startsWith(">") == false)
            p++ ;
         str = str.substring(0, p + 1);
         return str;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

  

   /**
    * Gets the Path of the log directory for a specific transaction.
    * 
    * @param simId for the simulator which received the transaction.
    * @param transactionCode the xtools transaction code, for example "prb" for
    * "provide and register". Default is "prb".
    * @param pid the Patient ID. If not null, only transactions for this patient
    * will be considered.
    * @param timeOfTransaction time the transaction was run. The Path returned
    * will be for the earliest transaction which is not before this time. If
    * null, method returns the most recently completed transaction.
    * @return absolute path to the log directory, or null. A null return means
    * there are no transactions or that all transactions were logged before the
    * passed time (probably an error).
    */
   public static Path getTransactionLogDirectoryPath(SimId simId,
                                                     String transactionCode, String pid, Date timeOfTransaction) {
     return getTransactionLogDirectoryPath(simId,transactionCode,pid,timeOfTransaction,true);
   }

   public static Path getTransactionLogDirectoryPath(SimId simId, 
      String transactionCode, String pid, Date timeOfTransaction, boolean mostRecentTransaction) {
      try {
         String simulatorName = simId.toString();
         String simulatorType = simId.getActorType();
         FilenameFilter filter = new PidDateFilenameFilter(pid, timeOfTransaction);
         if (StringUtils.isBlank(transactionCode)) transactionCode = "prb";
         Path base = Paths.get(externalCache, "simdb", simId.getTestSession().getValue(), simulatorName, simulatorType, transactionCode);
         Utility.isValidPfn("xtools log dir", base, PfnType.DIRECTORY, "r");
         String[] logDirs = base.toFile().list(filter);
         Arrays.sort(logDirs);
         if (logDirs.length == 0) {
            log.debug("No metadata folder found for actor " + simulatorType + " Patient ID " + pid);
            return null;
         }
         if (mostRecentTransaction) {
            log.debug("Found " + logDirs.length + " folder matching search criteria; will return most recent folder");
            return base.resolve(logDirs[logDirs.length - 1]);
         } else {
            log.debug("Found " + logDirs.length + " folder matching search criteria; will return earliest folder");
            return base.resolve(logDirs[0]);  // The earliest transaction would be the first one after sorting.
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }
   /**
    * Test harness for ProcessNISTSimulatorLogs static methods.
    * <p/>
    * To retrieve SOAP message components, arguments:
    * <ol start=0>
    * <li/>GETSOAP
    * <li/>The simulator name from the NIST tools.
    * <li/>The simulator type from the NIST tools, for example "rg" for 
    * receiving gateway. Default is "rep" for repository.
    * <li/>The transaction type from the NIST tools, for example, "xcr.ids" for
    * a Cross Community retrieve image set. Default is "prb" for provide and
    * register.
    * <li/>The Transaction time in yyyyMMddHHmmssSSS format. If present, the 
    * first transaction after this time will be used. If null the most recent 
    * transaction will be used.
    * <li/>The output directory.
    * </ol>
    * For other methods, arguments: 
    * <ol start = 0> 
    * <li/>Method to test
    * <ul> 
    * <li/>GETREQUEST = {@link #getSOAPRequest} 
    * <li/>GETMETADATA = {@link #getSOAPMetaData} 
    * <li/>GETRESPONSE = {@link #getSOAPResponse}
    * <li/>GETKOS
    * </ul> 
    * <li/>The simulator name from the NIST xtools. 
    * <li/>The simulator type from the NIST tools, for example "rg" for 
    * receiving gateway. Default is "rep" for repository.
    * <li/>The transaction type from the NIST tools, for example, "xcr.ids" for
    * a Cross Community retrieve image set. Default is "prb" for provide and
    * register.
    * <li/>The patient ID, or null for any patient id. 
    * <li/>The Transaction time in yyyyMMddHHmmssSSS format. If present, the 
    * first transaction after this time will be used. If null the most recent
    * transaction will be used. 
    * <li/>The output directory, or null for no write.
    * <li/>The output file name, or null for no write. 
    * </ol>
    * Use "-" or "null" for null arguments, trailing null arguments may be
    * omitted.
    * 
    * @param args arguments
    */
   public static void main(String[] args) {
      throw new UnsupportedOperationException("PrsSmLogs#main");
   }
//   public static void main(String[] args) {
//      try {
//         xdsiPath = Paths.get(Utility.getXDSIRoot());
//         log = Utility.getLog();
//         String methodToTest = getArg(args, 0).toUpperCase();
//         if (methodToTest.equals("GETSOAP")) args = new String[] { args[0], args[1], args[2], args[3], "-", args[4], args[5] };
//         String simulatorName = getArg(args, 1);
//         String simulatorType = getArg(args, 2);
//         String transactionType = getArg(args, 3);
//         String pid = getArg(args, 4);
//         Date timeOfTransaction = null;
//         String a3 = getArg(args, 5);
//         if (a3 != null) timeOfTransaction = timeOfTransactionFormat.parse(a3);
//         String outDir = getArg(args, 6);
//         String fName = getArg(args, 7);
//         if (pid != null) pid = pid + "^^^" + Identifiers.getAssigningAuthorityAffinityDomain();
//
//         Path logDir =
//            PrsSimLogs.getTransactionLogDirectoryPath(simulatorName, simulatorType, transactionType, pid, timeOfTransaction);
//         if (logDir == null) {
//            log.error(
//               "No metadata folder found for these criteria: " + simulatorName + " " + pid + " " + timeOfTransaction);
//         } else {
//            String req = "";
//            switch (methodToTest) {
//               case "GETSOAP":
//                  PrsSimLogs.getSOAPComponents(logDir, outDir, transactionType);
//                  break;
//               case "GETREQUEST":
//                  req = PrsSimLogs.getSOAPRequest(logDir, outDir, fName);
//                  req = XmlUtil.prettyPrintSOAP(req);
//                  log.info("SOAP Request:" + Utility.nl + req);
//                  break;
//               case "GETMETADATA":
//                  req = PrsSimLogs.getSOAPMetaData(logDir, outDir, fName);
//                  req = XmlUtil.prettyPrintSOAP(req);
//                  log.info("SOAP Request MetaData:" + Utility.nl + req);
//                  break;
//               case "GETRESPONSE":
//                  req = PrsSimLogs.getSOAPResponse(logDir, outDir, fName);
//                  req = XmlUtil.prettyPrintSOAP(req);
//                  log.info("SOAP Response:" + Utility.nl + req);
//                  break;
//               case "GETKOS":
//                  PrsSimLogs.getSOAPFile(logDir, outDir, fName);
//                  break;
//               default:
//                  throw new Exception("no such command: " + methodToTest);
//            }
//            log.info(methodToTest + " test completed");
//         }
//      } catch (Exception e) {
//         log.fatal("test failed" + e.getMessage());
//         e.printStackTrace();
//      }
//   }
//
//   private static String getArg(String[] args, int arg) {
//      if (args.length > arg) {
//         String a = args[arg];
//         if (StringUtils.isBlank(a) || a.equals("-") || a.equals("_") || a.equalsIgnoreCase("null")) return null;
//         return a.trim();
//      }
//      return null;
//   }

} // EO ProcessNISTSimulatorLogs
