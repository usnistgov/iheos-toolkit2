package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.actorfactory.ActorFactory;
import gov.nist.toolkit.actorfactory.RGActorFactory;
import gov.nist.toolkit.actorfactory.RepositoryActorFactory;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGenerator;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.*;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.RetInfo;
import gov.nist.toolkit.testengine.RetrieveB;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class RGActorSimulator extends GatewaySimulatorCommon implements MetadataGeneratingSim {
	SimDb db;
	SimulatorConfig asc;
	static Logger logger = Logger.getLogger(RegistryActorSimulator.class);
	Metadata m;
	MessageValidatorEngine mvc;

	public RGActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig asc) {
		super(common, dsSimCommon);
		this.db = db;
		this.asc = asc;
	}

	public boolean run(ATFactory.TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		
		this.mvc = mvc;
		
		if (transactionType.equals(TransactionType.XC_RETRIEVE)) {

			common.vc.isRequest = true;
			common.vc.isRet = true;
			common.vc.isXC = true;
			common.vc.isSimpleSoap = false;
			common.vc.hasSoap = true;
			common.vc.hasHttp = true;
		
			// this validates through soap wrapper
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;    // SOAP Fault generated

			if (mvc.hasErrors()) {
				returnRetrieveError();
				return false;
			}

			// extract retrieve request 
			MessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (mv == null || !(mv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
				returnRetrieveError();
				return false;
			}

			SoapMessageValidator smv = (SoapMessageValidator) mv;
			OMElement query = smv.getMessageBody();
			
			SimulatorConfigElement asce = asc.getUserByName(ActorFactory.homeCommunityId);
			if (asce == null) {
				er.err(Code.XDSRepositoryError, "RG Internal Error - homeCommunityId not configured", this, "");
				returnRetrieveError();
				return false;
			}
			String configuredHomeCommunityId = asce.asString();
			List<OMElement> targetHomeCommunityIdEles = MetadataSupport.decendentsWithLocalName(query, "HomeCommunityId");
			for (OMElement e : targetHomeCommunityIdEles) {
				String id = e.getText();
				if (id == null)
					id = "";
				if (!configuredHomeCommunityId.equals(id)) {
					er.err(Code.XDSRepositoryError, "HomeCommunityId in request (" +  id + ") does not match configured value (" + configuredHomeCommunityId + ")", this, "");
				}
			}

			if (mvc.hasErrors()) {
				returnRetrieveError();
				return false;
			}

			// get repository endpoint for retrieve
			String endpoint = asc.get(RepositoryActorFactory.retrieveEndpoint).asString();

			// issue soap call to repository
			Soap soap = new Soap();
			OMElement result = null;

			try {
				result = soap.soapCall(query, endpoint, true, true, true, SoapActionFactory.ret_b_action, SoapActionFactory.getResponseAction(SoapActionFactory.ret_b_action));

				// add these back in after testing sq
//				boolean hasErrors = passOnErrors(result);
//				
//				if (hasErrors)
//					return false;
				
			} catch (Exception e) {
				er.err(Code.XDSRegistryError, e);
				returnRetrieveError();
				return false;
			}
			
			
			RetrieveB retb = new RetrieveB(null);
			Map<String, RetInfo> docMap = null;
			
			try {
				docMap = retb.parse_rep_response(result);
			} catch (Exception e) {
				er.err(Code.XDSRegistryError, e);
				returnRetrieveError();
				return false;
			}
			
			StoredDocumentMap stdocmap = new StoredDocumentMap(docMap);
			dsSimCommon.intallDocumentsToAttach(stdocmap);

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

			mvc.run();

			return true; // no updates anyway
		}
		else if (transactionType.equals(TransactionType.XC_QUERY)) {
			ValidationContext vc = common.vc;
			vc.isRequest = true;
			vc.isSimpleSoap = true;
			vc.isSQ = true;
			vc.isXC = true;
			vc.xds_b = true;
			vc.hasSoap = true;
			vc.hasHttp = true;

			// run validations on message
			er.challenge("Scheduling initial validations");
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;   // if SOAP Fault generated
			
			if (mvc.hasErrors()) {
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}

			// extract query 
			MessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (mv == null || !(mv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}

			SoapMessageValidator smv = (SoapMessageValidator) mv;
			OMElement query = smv.getMessageBody();
			
			RemoteSqSim rss = new RemoteSqSim(common, dsSimCommon, this, asc, query);
			
			mvc.addMessageValidator("Forward query to local Registry", rss, newER());

			mvc.run();
			
			m = rss.getMetadata();
			
			String home = asc.get(RGActorFactory.homeCommunityId).asString();
			
			// add homeCommunityId
			XCQHomeLabelSim xc = new XCQHomeLabelSim(common, this, home);
			mvc.addMessageValidator("Attach homeCommunityId", xc, newER());
			
			// Add in errors
			AdhocQueryResponseGenerator queryResponseGenerator = new AdhocQueryResponseGenerator(common, dsSimCommon, rss);
			mvc.addMessageValidator("Attach Errors", queryResponseGenerator, newER());
			
			mvc.run();

			// wrap response in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, rss), newER());

			mvc.run();

			return true; // no updates anyway
		} else if (transactionType.equals(TransactionType.XC_PATIENT_DISCOVERY)) {
			common.vc.isRequest = true;
			common.vc.updateable = false;
			common.vc.isXcpd = true;
			common.vc.hasSoap = true;
			common.vc.hasHttp = true;
			
			// this validates through soap wrapper
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;    // SOAP Fault generated

			if (mvc.hasErrors()) {
				returnRetrieveError();
				return false;
			}

			// extract retrieve request 
			// We do not do anything with retrieve request for now ...  later we will need the parameters to create response.
			/*
			MessageValidator mv = common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (mv == null || !(mv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "RG Internal Error - cannot find SoapMessageValidator instance", "RespondingGatewayActorSimulator", "");
				return false;
			}
			SoapMessageValidator smv = (SoapMessageValidator) mv;
			OMElement query = smv.getMessageBody();
			*/
			// End obtaining retrieve
			mvc.run();
			// Create a response and Add in errors
			XCPDResponseGenerator xcpdResponse = new XCPDResponseGenerator(common);
			OMElement result = null;
			result = xcpdResponse.getXCPDResponse();
			mvc.addMessageValidator("Attach Errors", xcpdResponse, er);

			
			mvc.run();
			// wrap response in soap wrapper and http wrapper
			//mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperXCPDResponseSim(common, result), newER());
			//mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperXCPDResponseSim(common, result), er);
			mvc.addMessageValidator("SendResponseInSoapWrapper", new SoapWrapperResponseSim(common, dsSimCommon, result), er);

			//
			mvc.run();
			
			return true;
		} else {
			dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
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
	
	public Metadata getMetadata() {
		return m;
	}


}
