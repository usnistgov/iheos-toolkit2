package gov.nist.toolkit.simulators.sim.ids
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator
import gov.nist.toolkit.registrymsg.registry.Response
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGenerator
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.GatewaySimulatorCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger
import javax.xml.namespace.QName

@TypeChecked
public class IdsActorSimulator extends GatewaySimulatorCommon {
	static Logger logger = Logger.getLogger(IdsActorSimulator.class);
	AdhocQueryResponseGenerator sqs;

	public IdsActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig) {
		super(common, dsSimCommon);
		this.db = db;
		setSimulatorConfig(simulatorConfig);
	}

	public IdsActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
		super(dsSimCommon.simCommon, dsSimCommon);
		this.db = dsSimCommon.simCommon.db;
        setSimulatorConfig(simulatorConfig);
	}

    public IdsActorSimulator() {}

	public void init() {}

	// boolean => hasErrors?
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validationPattern) throws IOException {

        logger.info("IdsActorSimulator: run - transactionType is " + transactionType);
	GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

		if (transactionType.equals(TransactionType.RET_IMG_DOC_SET)) {
			logger.debug("Transaction type: RET_IMG_DOC_SET");
			common.vc.isRet = false;
			common.vc.isRad69 = true;
			common.vc.isXC = false;
			common.vc.isRequest = true;
			common.vc.isSimpleSoap = false;
			common.vc.hasSoap = true;
			common.vc.hasHttp = true;

			logger.debug("dsSimCommon.runInitialValidationsAndFaultIfNecessary()");
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
			    return false;

			logger.debug("mvc.hasErrors()");
			if (mvc.hasErrors()) {
			    dsSimCommon.sendErrorsInRegistryResponse(er);
			    return false;
			}
			
			// extract query from validator chain
			logger.debug("Extract query from validator chain");
			SoapMessageValidator smv = (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (smv == null || !(smv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "IDS Internal Error - cannot find SoapMessageValidator instance",
					"IdsActorSimulator", "");
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
			logger.debug("Got AbstractMessageValidator");
			OMElement retrieveRequest = smv.getMessageBody();

			List<String> docUids = new ArrayList<String>();
			for (OMElement uidEle : XmlUtil.decendentsWithLocalName(retrieveRequest, "DocumentUniqueId")) {
				String uid = uidEle.getText();
				docUids.add(uid);
				logger.debug("Document UID: " + uid);
			}

			List<String> imagingUids = new ArrayList<String>();
			for (OMElement studyEle : XmlUtil.decendentsWithLocalName(retrieveRequest, "StudyRequest")) {
				String studyUid = studyEle.getAttributeValue(new QName("studyInstanceUID"));
				logger.debug("Study UID: " + studyUid);
				Iterator<OMElement> seriesIterator = studyEle.getChildElements();
				while (seriesIterator.hasNext()) {
					OMElement seriesEle = (OMElement)seriesIterator.next();
					String seriesUid = seriesEle.getAttributeValue(new QName("seriesInstanceUID"));
					logger.debug(" Series UID: " + seriesUid);
					for (OMElement instanceEle : XmlUtil.decendentsWithLocalName(seriesEle, "DocumentUniqueId")) {
						String uid = instanceEle.getText();
						String fullUid=studyUid + ":" + seriesUid + ":" + uid;
						imagingUids.add(fullUid);
						logger.debug(fullUid);
					}
				}
			}
			List<String> transferSyntaxUids = new ArrayList<String>();
			for (OMElement transferSyntaxEle : XmlUtil.decendentsWithLocalName(retrieveRequest, "TransferSyntaxUID")) {
				String xferSyntaxUid = transferSyntaxEle.getText();
				logger.debug("Transfer Syntax UID: " + xferSyntaxUid);
				//logger.debug(" to string: " + transferSyntaxEle.toString());
				transferSyntaxUids.add(xferSyntaxUid);
			}

			RetrieveImagingDocSetResponseSim dms = null;
			String repositoryUniqueId="";
			dms = new RetrieveImagingDocSetResponseSim(
				common.vc,
				imagingUids,
				transferSyntaxUids,
				common,
				dsSimCommon,
				repositoryUniqueId);

			mvc.addMessageValidator("Generate DocumentResponse", dms, gerb.buildNewErrorRecorder());

			mvc.run();

			// generate special retrieve response message
			Response resp = dms.getResponse();
			// add in any errors collected
			try {
                        	RegistryErrorListGenerator relg = dsSimCommon.getRegistryErrorList();
                        	resp.add(relg, null);
			} catch (Exception e) {}

			// wrap in soap wrapper and http wrapper
			// auto-detects need for multipart/MTOM
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, dms), gerb.buildNewErrorRecorder());

			mvc.run();


		} else if (transactionType.equals(TransactionType.IG_QUERY)) {
/*
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
			AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
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
			
*/
			return false; // no updates anyway

		} 
		else if (transactionType.equals(TransactionType.IG_RETRIEVE)) {
/*
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
            AbstractMessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
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
*/

            return false;
		}
			
		else {
			er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType, "ImagingDocSourceActorSimulator", "");
            dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
			return true;
		}
	}


}
