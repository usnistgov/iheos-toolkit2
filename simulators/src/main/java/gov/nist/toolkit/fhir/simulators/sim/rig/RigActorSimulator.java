/**
 * 
 */
package gov.nist.toolkit.fhir.simulators.sim.rig;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Responding Imaging Gateway Simulator
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigActorSimulator extends BaseDsActorSimulator {
   SimDb db;
   static Logger logger = Logger.getLogger(RigActorSimulator.class);
   MessageValidatorEngine mvc;

   public RigActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
      super(common, dsSimCommon);
      this.db = db;
      setSimulatorConfig(simulatorConfig);
   }

   public RigActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
      super(dsSimCommon.simCommon, dsSimCommon);
      this.db = dsSimCommon.simCommon.db;
        setSimulatorConfig(simulatorConfig);
   }

    public RigActorSimulator() {}

   public void init() {}


   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {

      this.mvc = mvc;

      switch (transactionType) {        

         case XC_RET_IMG_DOC_SET:

         logger.debug("Transaction type: XC_RET_IMG_DOC_SET");
            common.vc.isRequest = true;
            common.vc.isRad69 = true;
            common.vc.isXC = true;
            common.vc.isSimpleSoap = false;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            logger.debug("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;    // SOAP Fault generated

            logger.debug("mvc.hasErrors()");
            if (mvc.hasErrors()) {
               returnRetrieveError();
               return false;
            }

            logger.debug("Extract retrieve");
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
               returnRetrieveError();
               return false;
            }
            logger.debug("Got AbstractMessageValidator");
            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            logger.debug("Process message");
            RigImgDocSetRet retSim = new RigImgDocSetRet(common, dsSimCommon, getSimulatorConfig());
            mvc.addMessageValidator("XcRetrieveImgSim", retSim, er);
            mvc.run();

            logger.debug("wrap response message");
            er.detail("Wrapping response in SOAP Message and sending");
            OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(retSim.getResult());
            dsSimCommon.sendHttpResponse(env, er);
            mvc.run();

            return false;

         default:

            dsSimCommon.sendFault("RGActorSimulator: Don't understand transaction " + transactionType, null);
            return true;
      }

   }

   private void returnRetrieveError() {
      mvc.run();
      Response response = null;
      try {
         response = dsSimCommon.getRegistryResponse();
         er.detail("Wrapping response in RetrieveDocumentSetResponse and then SOAP Message");
         OMElement rdsr = dsSimCommon.wrapResponseInRetrieveDocumentSetResponse(response.getResponse());
         OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(rdsr);

         dsSimCommon.sendHttpResponse(env, er);

      } catch (Exception e) {

      }
   }
   
   public boolean supports(TransactionType transactionType) {
      return TransactionType.XC_RET_IMG_DOC_SET.equals(transactionType);
   }
}
