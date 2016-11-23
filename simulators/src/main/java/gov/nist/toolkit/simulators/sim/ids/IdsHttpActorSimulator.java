/**
 * 
 */
package gov.nist.toolkit.simulators.sim.ids;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.http.HttpMessageBa;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.simulators.support.BaseHttpActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * Simulator for Image Document Source (IDS) receiving WADO (RAD-55)
 * transactions.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class IdsHttpActorSimulator extends BaseHttpActorSimulator {

   static Logger logger = Logger.getLogger(IdsHttpActorSimulator.class);

   static List <TransactionType> transactions = new ArrayList <>();

   static {
      transactions.add(TransactionType.WADO_RETRIEVE);
   }

   public boolean supports(TransactionType transactionType) {
      return transactions.contains(transactionType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * gov.nist.toolkit.simulators.support.BaseHttpActorSimulator#run(gov.nist.
    * toolkit.configDatatypes.client.TransactionType,
    * gov.nist.toolkit.valsupport.engine.MessageValidatorEngine)
    */
   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc) throws IOException {

      logger.info("IdsHttpActorSimulator: run - transactionType = " + transactionType);
      simCommon.setLogger(logger);
      GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
      DsSimCommon dsSimCommon = new DsSimCommon(simCommon);

      logger.debug(transactionType);
      switch (transactionType) {
         case WADO_RETRIEVE:
            simCommon.vc.isRad55 = true;
            simCommon.vc.isRequest = true;
            
            logger.debug("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary()) {
               return false;
            }

            logger.debug("mvc.hasErrors()");
            if (mvc.hasErrors()) {
               return false;
            }
            
            HttpParserBa hparser = dsSimCommon.getHttpParserBa();
            HttpMessageBa httpMsg = hparser.getHttpMessage();
            logger.info("working here");
            
            
            
           // TODO working here@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            return false;

         default:
            er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType,
               "ImagingDocSourceActorSimulator", "");
            simCommon.sendHttpFault("Don't understand transaction " + transactionType);
            return true;

      }

   }

}
