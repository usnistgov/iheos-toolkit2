package gov.nist.toolkit.testengine.engine;

import edu.wustl.mir.erl.ihe.xdsi.util.PrsSimLogs;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimDbEvent;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SoapSimulatorTransaction implements TransactionRecord<SoapSimulatorTransaction> {
   private SimReference simReference;
   private String request;
   private String requestHeader;
   private String requestBody;
   private String response;
   private String responseHeader;
   private String responseBody;
   private SimDbEvent simDbEvent;

   public SoapSimulatorTransaction(SimReference simReference) {
      this.simReference = simReference;
   }

   @Override
   public List<SoapSimulatorTransaction> get() throws XdsInternalException {
      SimDb simDb = null;
      List<SoapSimulatorTransaction> transactions = new ArrayList<>();
      try {
         simDb = new SimDb(simReference.getSimId());
         List<SimDbEvent> events = simDb.getEventsSinceMarker(simReference.getActorType().getShortName(), simReference.getTransactionType().getShortName());

         for (SimDbEvent event : events) {
            try {
               Path path = event.getRequestBodyFile().getParentFile().toPath();
               SimulatorTransaction st = new SimulatorTransaction(event.getSimId(), event.getTransactionType(), null, null);
               PrsSimLogs.loadTransaction(st, path);

               SoapSimulatorTransaction sst = new SoapSimulatorTransaction(simReference);
               sst.simDbEvent = event;

               sst.requestHeader = st.getRequestHeader();
               sst.requestBody = st.getRequestBody();
               sst.request = st.getRequest();
               sst.responseHeader = st.getResponseHeader();
               sst.responseBody = st.getResponseBody();
               sst.response = st.getResponse();

              transactions.add(sst);
            } catch (Exception ex) {
               // Unwanted transaction in event?
            }
         }

      } catch (Exception ex) {
         throw new XdsInternalException(ex.toString());
      }

      return transactions;
   }

   public SimReference getSimReference() {
      return simReference;
   }

   public String getRequest() {
      return request;
   }

   public String getRequestHeader() {
      return requestHeader;
   }

   public String getRequestBody() {
      return requestBody;
   }

   public String getResponse() {
      return response;
   }

   public String getResponseHeader() {
      return responseHeader;
   }

   public String getResponseBody() {
      return responseBody;
   }

   @Override
   public SimDbEvent getSimDbEvent() {
      return simDbEvent;
   }

   @Override
   public TransactionType getTransactionType() {
      return simDbEvent.getTransactionType();
   }

   @Override
   public String getUrl() {
      return simDbEvent.getSimLogUrl(); // TODO: Is this correct?
   }

   @Override
   public String getPlaceToken() {
       return ""; // skb TODO
   }
}
