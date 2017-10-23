/**
 * 
 */
package gov.nist.toolkit.testengine.engine;

import edu.wustl.mir.erl.ihe.xdsi.util.PfnType;
import edu.wustl.mir.erl.ihe.xdsi.util.PrsSimLogs;
import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.testengine.simLogs.FhirTransactionLoader;
import gov.nist.toolkit.utilities.html.HeaderBlock;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Used to retrieve results of a transaction previously sent to a simulator
 * 
 *
 */
public class FhirSimulatorTransaction {

   private SimId simId;
   private TransactionType transactionType;
//   private String pid;
//   private Date timeStamp;
//   private Path logDirPath;
//   private String transactionDirectory;
   private IBaseResource request;
   private IBaseResource response;
   private HeaderBlock requestHeaders;
   private HeaderBlock responseHeaders;
//   private String requestBody;
//   private String responseBody;
//   private List<String> pfns = new ArrayList<>();
//   private String stdPfn;
//   private String url;

   private UseReportManager useReportManager = null;

   public FhirSimulatorTransaction(SimId simId, TransactionType transactionType) {
      this.simId = simId;
      this.transactionType = transactionType;
//      this.pid = pid;
//      this.timeStamp = timeStamp;
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
    * @return the {@link #request} value.
    */
   public IBaseResource getRequest() {
      return request;
   }

   /**
    * @param request the {@link #request} to set
    */
   public void setRequest(IBaseResource request) {
      this.request = request;
   }

   /**
    * @return the {@link #response} value.
    */
   public IBaseResource getResponse() {
      return response;
   }

   /**
    * @param response the {@link #response} to set
    */
   public void setResponse(IBaseResource response) {
      this.response = response;
   }

   /**
    * @return the {@link #requestHeaders} value.
    */
   public HeaderBlock getRequestHeaders() {
      return requestHeaders;
   }

   /**
    * @param requestHeaders the {@link #requestHeaders} to set
    */
   public void setRequestHeaders(HeaderBlock requestHeaders) {
      this.requestHeaders = requestHeaders;
   }

   /**
    * @return the {@link #responseHeaders} value.
    */
   public HeaderBlock getResponseHeaders() {
      return responseHeaders;
   }

   /**
    * @param responseHeaders the {@link #responseHeaders} to set
    */
   public void setResponseHeaders(HeaderBlock responseHeaders) {
      this.responseHeaders = responseHeaders;
   }


   /**
    * Returns value for name from useReportMananger
    *
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
    *
    * @param simId           for the simulator which received the transaction. Must exist.
    * @param transactionType TransactionType value we are looking for. For
    *                        example {@link TransactionType#PROVIDE_AND_REGISTER}.
    * @return instance of this class for transaction.
    * @throws XdsInternalException on error, such as: no such simulator, no
    *                              transaction matching parameters, and so on.
    */
   public static List<FhirSimulatorTransaction> getAll(SimId simId, TransactionType transactionType)
           throws XdsInternalException {
      try {
         // Verify that simId represents an existing file
         Installation installation = Installation.instance();
         // PropertyServiceManager propertyServiceManager = installation.propertyServiceManager();
         // PropertyManager propertyManager = propertyServiceManager.getPropertyManager();
         // String cache = propertyManager.getExternalCache();
         String cache = installation.externalCache().getAbsolutePath();
         String name = simId.toString();
         Path simPath = Paths.get(cache, "simdb", name);
         Utility.isValidPfn("simulator " + name, simPath, PfnType.DIRECTORY, "r");

         SimDb simDb = new SimDb(simId);

         // Load simulator type
         ActorType actorType = simDb.getSimulatorActorType();
         String actorTypeName = actorType.getShortName();
         String requestedActorType = StringUtils.trimToEmpty(simId.getActorType());
         switch (requestedActorType) {
            // No requested actor type; use whatever type is there.
            case "":
               simId.setActorType(actorTypeName);
               break;
            // repository/registry OK for requested repository or registry.
            case "rep":
            case "reg":
               if (actorTypeName.equalsIgnoreCase("rr")) break;
               // All other types much match
               //$FALL-THROUGH$
            default:
               if (actorTypeName.equalsIgnoreCase(requestedActorType)) break;
               String em = simId.toString() + " is actor type " + actorType +
                       ". actor type " + requestedActorType + " expected.";
               throw new Exception(em);
         }

         return FhirTransactionLoader.loadTransactions(simId, transactionType);

      } catch (Exception e) {
         throw new XdsInternalException("SimulatorTransaction.get error: " +
                 ExceptionUtil.exception_details(e));
      }
   }

}
