/**
 * 
 */
package gov.nist.toolkit.fhir.simulators.sim.iig;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;
import java.util.logging.Logger;

import java.io.IOException;

/**
 * Initiating Imaging Gateway Simulator
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class IigActorSimulator extends BaseDsActorSimulator {
   static Logger logger = Logger.getLogger(IigActorSimulator.class.getName());

   public IigActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
      super(common, dsSimCommon);
      this.db = db;
      setSimulatorConfig(simulatorConfig);
   }

   public IigActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
      super(dsSimCommon.simCommon, dsSimCommon);
      this.db = dsSimCommon.simCommon.db;
      setSimulatorConfig(simulatorConfig);
   }

   public IigActorSimulator() {}

   public void init() {}

   /*
    * (non-Javadoc)
    * 
    * @see
    * gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator#run(gov.nist.
    * toolkit.configDatatypes.client.TransactionType,
    * gov.nist.toolkit.valsupport.engine.MessageValidatorEngine,
    * java.lang.String)
    */
   @Override
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation)
      throws IOException {

      logger.info("IgActorSimulator: run - transactionType is " + transactionType);

      switch (transactionType) {

         case RET_IMG_DOC_SET_GW:

            logger.fine("Transaction type: RET_IMG_DOC_SET_GW");
            common.vc.isRad69 = true;
            common.vc.isXC = true;
            common.vc.isRequest = true;
            common.vc.isSimpleSoap = false;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            logger.fine("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary()) return false;

            logger.fine("mvc.hasErrors()");
            if (mvc.hasErrors()) {
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

            logger.fine("Extract retrieve from validator chain");
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "IG Internal Error - cannot find SoapMessageValidator instance",
                  "InitiatingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }
            logger.fine("Got AbstractMessageValidator");
            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement retrieveRequest = smv.getMessageBody();

            logger.fine("Process message");
            XcRetrieveImgSim retSim = new XcRetrieveImgSim(common, dsSimCommon, getSimulatorConfig());
            mvc.addMessageValidator("XcRetrieveImgSim", retSim, er);
            mvc.run();

            logger.fine("wrap response message");
            er.detail("Wrapping response in SOAP Message and sending");
            OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(retSim.getResult());
            dsSimCommon.sendHttpResponse(env, er);
            mvc.run();

            return false;

         // XCAI_TODO written, need to test

         default:
            er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType,
               "InitiatingGatewayActorSimulator", "");
            dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
            return true;
      }

   }
   public boolean supports(TransactionType transactionType) {
      return TransactionType.RET_IMG_DOC_SET_GW.equals(transactionType);
   }

}
