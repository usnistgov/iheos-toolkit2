package gov.nist.toolkit.fhir.simulators.sim.ig

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.fhir.simulators.sim.reg.AdhocQueryResponseGenerator
import gov.nist.toolkit.fhir.simulators.sim.reg.SoapWrapperRegistryResponseSim
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.fhir.simulators.support.GatewaySimulatorCommon
import gov.nist.toolkit.simcommon.server.SimCommon
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger

@TypeChecked
public class IgActorSimulator extends GatewaySimulatorCommon {
   static Logger logger = Logger.getLogger(IgActorSimulator.class);
   AdhocQueryResponseGenerator sqs;

   public IgActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
      super(common, dsSimCommon);
      this.db = db;
      setSimulatorConfig(simulatorConfig);
   }

   public IgActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
      super(dsSimCommon.simCommon, dsSimCommon);
      this.db = dsSimCommon.simCommon.db;
      setSimulatorConfig(simulatorConfig);
   }

   public IgActorSimulator() {}

   public void init() {}

   // boolean => hasErrors?
   public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validationPattern) throws IOException {

      logger.info("IgActorSimulator: run - transactionType is " + transactionType);

      switch (transactionType) {

         case TransactionType.IG_QUERY:

            common.vc.isSQ = true;
            common.vc.isXC = false;
            common.vc.isRequest = true;
            common.vc.isSimpleSoap = true;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;

            if (mvc.hasErrors()) {
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

         // extract query from validator chain
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "IG Internal Error - cannot find SoapMessageValidator instance", "InitiatingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }
            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement query = smv.getMessageBody();

            boolean validateOk = validateHomeCommunityId(er, query, false);
            if (!validateOk)
               return false;

         // run/forward the query
            XcQuerySim xcqSim = new XcQuerySim(common, dsSimCommon, getSimulatorConfig());
            mvc.addMessageValidator("XcQuerySim", xcqSim, er);

            mvc.run();

         // Add in errors
            AdhocQueryResponseGenerator ahqrg = new AdhocQueryResponseGenerator(common, dsSimCommon, xcqSim);
            mvc.addMessageValidator("Attach Errors", ahqrg, er);
            mvc.run();
            sqs = ahqrg;

         // wrap in soap wrapper and http wrapper
            mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, sqs), er);

            mvc.run();

            return false; // no updates anyway

         case TransactionType.IG_RETRIEVE:

            common.vc.isRet = true;
            common.vc.isXC = false;
            common.vc.isRequest = true;
            common.vc.isSimpleSoap = false;
            common.vc.hasSoap = true;
            common.vc.hasHttp = true;

            if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
               return false;

            if (mvc.hasErrors()) {
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }

         // extract retrieve request
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            if (mv == null || !(mv instanceof SoapMessageValidator)) {
               er.err(Code.XDSRegistryError, "IG Internal Error - cannot find SoapMessageValidator instance", "InitiatingGatewayActorSimulator", "");
               dsSimCommon.sendErrorsInRegistryResponse(er);
               return false;
            }
            SoapMessageValidator smv = (SoapMessageValidator) mv;
            OMElement retreiveReqeust = smv.getMessageBody();

            XcRetrieveSim retSim = new XcRetrieveSim(common, dsSimCommon, getSimulatorConfig())
            mvc.addMessageValidator("XcRetrieveSim", retSim, er)
            mvc.run()

         // wrap in soap wrapper and http wrapper
            er.detail("Wrapping response in SOAP Message and sending");
            OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(retSim.getResult());
            assert env
            dsSimCommon.sendHttpResponse(env, er);

            mvc.run()

            return false;

         default:
            er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType, "InitiatingGatewayActorSimulator", "");
            dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
            return true;
      }
   }


}
