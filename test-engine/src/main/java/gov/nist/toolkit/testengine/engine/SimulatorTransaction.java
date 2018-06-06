/**
 * 
 */
package gov.nist.toolkit.testengine.engine;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.PrsSimLogs;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used to retrieve results of a transaction previously sent to a simulator
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class SimulatorTransaction {
      
   private SimId simId;
   private TransactionType transactionType;
   private String pid;
   private Date timeStamp;
   private Path logDirPath;
   private String transactionDirectory;
   private String request;
   private String response;
   private String requestHeader;
   private String responseHeader;
   private String requestBody;
   private String responseBody;
   private List<String> pfns = new ArrayList<>();
   private String stdPfn;
   private String url;
   
   private UseReportManager useReportManager = null;
   
   private SimulatorTransaction(SimId simId, TransactionType transactionType, String pid, Date timeStamp) {
      this.simId = simId;
      this.transactionType = transactionType;
      this.pid = pid;
      this.timeStamp = timeStamp;
   }
   
   /**
    * @return the {@link #simId} value.
    */
   public SimId getSimId() {
      return simId;
   }

   /**
    * @param simId the {@link #simId} to set
    */
   public void setSimId(SimId simId) {
      this.simId = simId;
   }

   /**
    * @return the {@link #transactionType} value.
    */
   public TransactionType getTransactionType() {
      return transactionType;
   }


   /**
    * @param transactionType the {@link #transactionType} to set
    */
   public void setTransactionType(TransactionType transactionType) {
      this.transactionType = transactionType;
   }

   /**
    * @return the {@link #pid} value.
    */
   public String getPid() {
      return pid;
   }


   /**
    * @param pid the {@link #pid} to set
    */
   public void setPid(String pid) {
      this.pid = pid;
   }


   /**
    * @return the {@link #timeStamp} value.
    */
   public Date getTimeStamp() {
      return timeStamp;
   }


   /**
    * @param timeStamp the {@link #timeStamp} to set
    */
   public void setTimeStamp(Date timeStamp) {
      this.timeStamp = timeStamp;
   }


   /**
    * @return the {@link #logDirPath} value.
    */
   public Path getLogDirPath() {
      return logDirPath;
   }


   /**
    * @param logDirPath the {@link #logDirPath} to set
    */
   public void setLogDirPath(Path logDirPath) {
      this.logDirPath = logDirPath;
   }


   /**
    * @return the {@link #transactionDirectory} value.
    */
   public String getTransactionDirectory() {
      return transactionDirectory;
   }

   /**
    * @param transactionDirectory the {@link #transactionDirectory} to set
    */
   public void setTransactionDirectory(String transactionDirectory) {
      this.transactionDirectory = transactionDirectory;
   }

   /**
    * @return the {@link #request} value.
    */
   public String getRequest() {
      return request;
   }

   /**
    * @param request the {@link #request} to set
    */
   public void setRequest(String request) {
      this.request = request;
   }

   /**
    * @return the {@link #response} value.
    */
   public String getResponse() {
      return response;
   }

   /**
    * @param response the {@link #response} to set
    */
   public void setResponse(String response) {
      this.response = response;
   }

   /**
    * @return the {@link #requestHeader} value.
    */
   public String getRequestHeader() {
      return requestHeader;
   }

   /**
    * @param requestHeader the {@link #requestHeader} to set
    */
   public void setRequestHeader(String requestHeader) {
      this.requestHeader = requestHeader;
   }

   /**
    * @return the {@link #responseHeader} value.
    */
   public String getResponseHeader() {
      return responseHeader;
   }

   /**
    * @param responseHeader the {@link #responseHeader} to set
    */
   public void setResponseHeader(String responseHeader) {
      this.responseHeader = responseHeader;
   }

   /**
    * @return the {@link #requestBody} value.
    */
   public String getRequestBody() {
      return requestBody;
   }

   /**
    * @param requestBody the {@link #requestBody} to set
    */
   public void setRequestBody(String requestBody) {
      this.requestBody = requestBody;
   }

   /**
    * @return the {@link #responseBody} value.
    */
   public String getResponseBody() {
      return responseBody;
   }

   /**
    * @param responseBody the {@link #responseBody} to set
    */
   public void setResponseBody(String responseBody) {
      this.responseBody = responseBody;
   }
   
   /**
    * @return the {@link #pfns} value.
    */
   public List <String> getPfns() {
      return pfns;
   }

   /**
    * @param pfns the {@link #pfns} to set
    */
   public void setPfns(List <String> pfns) {
      this.pfns = pfns;
   }
   
   /**
    * @return the {@link #stdPfn} value.
    */
   public String getStdPfn() {
      return stdPfn;
   }

   /**
    * @param stdPfn the {@link #stdPfn} to set
    */
   public void setStdPfn(String stdPfn) {
      this.stdPfn = stdPfn;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
   
   /**
    * Returns value for name from useReportMananger
    * @param name use as name
    * @return value, or "unavailable" if not found.
    */
   public String resolve(String name) {
      for (UseReport ur : useReportManager.useReports) {
         if (name.equalsIgnoreCase(ur.useAs)) return ur.value;
      }
      return "unavailable";
   }
   

   /**
    * @return the {@link #useReportManager} value.
    */
   public UseReportManager getUseReportManager() {
      return useReportManager;
   }

   /**
    * @param useReportManager the {@link #useReportManager} to set
    */
   public void setUseReportManager(UseReportManager useReportManager) {
      this.useReportManager = useReportManager;
   }

   /**
    * Generates instance of this class for specified simulator transaction.
    * @param simId for the simulator which received the transaction. Must exist.
    * @param transactionType TransactionType value we are looking for. For 
    * example {@link TransactionType#PROVIDE_AND_REGISTER}.
    * @param pid <u>Complete</u> patient id if transaction is for a particular
    * patient. Null or blank string if any patient will do or transaction is
    * not patient based. For example, 
    * "P20160831112743.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO".
    * @param timeStamp of transaction. Earliest transaction not preceding this
    * time will be returned. If null, most recent transaction will be returned.
    * @return instance of this class for transaction.
    * @throws XdsInternalException on error, such as: no such simulator, no
    * transaction matching parameters, and so on.
    */
   public static SimulatorTransaction get(SimId simId, 
      TransactionType transactionType, String pid, Date timeStamp) 
      throws XdsInternalException {
      try {
         // Verify that simId represents an existing file
         Installation installation = Installation.instance();
         // PropertyServiceManager propertyServiceManager = installation.propertyServiceManager();
         // PropertyManager propertyManager = propertyServiceManager.getPropertyManager();
         // String cache = propertyManager.getExternalCache();
         String cache = installation.externalCache().getAbsolutePath();
         String name = simId.toString();
         Path simPath = Paths.get(cache, "simdb", simId.getTestSession().getValue(), name);
         Utility.isValidPfn("simulator " + name,  simPath, PfnType.DIRECTORY, "r");
         
         // Load simulator type
         String actorType = new String(Files.readAllBytes(simPath.resolve("sim_type.txt"))).trim();
         String requestedActorType = StringUtils.trimToEmpty(simId.getActorType());
         switch (requestedActorType) {
            // No requested actor type; use whatever type is there.
            case "":
               simId.setActorType(actorType);
               break;
            // repository/registry OK for requested repository or registry.
            case "rep":
            case "reg":
               if (actorType.equalsIgnoreCase("rr")) break;
            // All other types much match
            //$FALL-THROUGH$
            default:
               if (actorType.equalsIgnoreCase(requestedActorType)) break;
               String em = simId.toString() + " is actor type " + actorType +
                  ". actor type " + requestedActorType + " expected.";
               throw new Exception(em);
         }
         
         // Create instance and load transaction
         SimulatorTransaction trn = 
            new SimulatorTransaction(simId, transactionType, pid, timeStamp);
         PrsSimLogs.loadTransaction(trn);
         
         return trn;
      } catch (Exception e) {
         throw new XdsInternalException("SimulatorTransaction.get error: " + 
            e.getMessage());
      }
   }


   /**
    * Generates instance of this class for specified simulator transaction.
    * @param simId for the simulator which received the transaction. Must exist.
    * @param actorType is the type of actor that received the transaction. This is further specification for those simulators that encompass multiple actors.
    * @param transactionType TransactionType value we are looking for. For
    * example {@link TransactionType#PROVIDE_AND_REGISTER}.
    * @param pid <u>Complete</u> patient id if transaction is for a particular
    * patient. Null or blank string if any patient will do or transaction is
    * not patient based. For example,
    * "P20160831112743.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO".
    * @param timeStamp of transaction. Earliest transaction not preceding this
    * time will be returned. If null, most recent transaction will be returned.
    * @return instance of this class for transaction.
    * @throws XdsInternalException on error, such as: no such simulator, no
    * transaction matching parameters, and so on.
    */
   public static SimulatorTransaction get(SimId simId, ActorType actorType,
                                          TransactionType transactionType, String pid, Date timeStamp)
           throws XdsInternalException {
      try {
         // Verify that simId represents an existing file
         Installation installation = Installation.instance();
         String cache = installation.externalCache().getAbsolutePath();
         String name = simId.toString();
         Path simPath = Paths.get(cache, "simdb", simId.getTestSession().getValue(), name);
         Utility.isValidPfn("simulator " + name,  simPath, PfnType.DIRECTORY, "r");

         // This is a key statement. The SimId that is supplied to this method might indicate
         // a composite simulator such as Registry/Repository. When searching for a transaction,
         // we need to search under the specific actor type. The code that calls this method
         // supplies the actorType which gives us that exact name.
         simId.setActorType(actorType.getShortName());

         // Create instance and load transaction
         SimulatorTransaction trn =
                 new SimulatorTransaction(simId, transactionType, pid, timeStamp);
         PrsSimLogs.loadTransaction(trn);

         return trn;
      } catch (Exception e) {
         throw new XdsInternalException("SimulatorTransaction.get error: " +
                 e.getMessage());
      }
   }
   

}
