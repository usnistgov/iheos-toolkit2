package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymsg.registry.AdhocQueryResponseParser;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGeneratingSim;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseSendingSim;
import gov.nist.toolkit.simulators.sim.reg.sq.SqSim;
import gov.nist.toolkit.simulators.support.*;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.List;

public class RemoteSqSim  extends TransactionSimulator implements MetadataGeneratingSim, AdhocQueryResponseGeneratingSim {
	DsSimCommon dsSimCommon;
	AdhocQueryResponse response;
	GatewaySimulatorCommon gatewayCommon;
	Metadata m = new Metadata();
	Exception startUpException = null;
	OMElement query;
	Logger logger = Logger.getLogger(SqSim.class);

	public RemoteSqSim(SimCommon common, DsSimCommon dsSimCommon, GatewaySimulatorCommon gatewayCommon, SimulatorConfig simulatorConfig, OMElement query) {
		super(common, simulatorConfig);
        this.dsSimCommon = dsSimCommon;
		this.gatewayCommon = gatewayCommon;
		this.query = query;

		// build response
		try {
			response = new AdhocQueryResponse(Response.version_3);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			startUpException = e;
			return;
		}
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {

		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		// if request didn't validate, return so errors can be reported
		if (common.hasErrors()) {
			try {
				response.add(dsSimCommon.getRegistryErrorList(), null);
			} catch (XdsInternalException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e);
			}
			return;
		}

		boolean validateOk = gatewayCommon.validateHomeCommunityId(er, query, true);
		if (!validateOk)
			return;

		// get configured endpoint for backend registry for SQ
        String endpointLabel = (common.isTls()) ? SimulatorProperties.storedQueryTlsEndpoint : SimulatorProperties.storedQueryEndpoint;
		String endpoint = simulatorConfig.get(endpointLabel).asString();

		// issue soap call to registry
		Soap soap = new Soap();
		OMElement result = null;
		try {

			er.challenge("Forwarding on as SQ from RG to local Registry " + endpoint);
			er.detail(new OMFormatter(query).toString());


			result = soap.soapCall(query, endpoint, false, true, true, MetadataSupport.SQ_action, SoapActionFactory.getResponseAction(MetadataSupport.SQ_action));

			er.challenge("Response from registry is");
			er.detail(new OMFormatter(result).toString());

			boolean hasErrors = passOnErrors(mvc, result);

			if (hasErrors)
				return;

			Metadata mr = MetadataParser.parseNonSubmission(result);
//			m.copy(mr);
			m = mr;

			List<OMElement> results = m.getAllObjects(); // everything
//			results.addAll(m.getObjectRefs());
			response.addQueryResults(results, false);


		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg == null || msg.equals(""))
				msg = ExceptionUtil.exception_details(e);
			logger.error(msg);
			er.err(XdsErrorCode.Code.XDSRegistryError, msg, this, null);

			return;
		}

	}

	boolean passOnErrors(MessageValidatorEngine mvc, OMElement result) throws XdsInternalException {

		AdhocQueryResponseParser aqrp = new AdhocQueryResponseParser(result);
		AdhocQueryResponseParser.AdhocQueryResponse aqr = aqrp.getResponse();

		if (!aqr.isSuccess()) {
			RegistryErrorListGenerator relg = new RegistryErrorListGenerator();
			relg.addRegistryErrorList(aqr.getRegistryErrorListEle(), null);
            dsSimCommon.setRegistryErrorListGenerator(relg);
			mvc.addMessageValidator("Send RegistryResponse with errors", new RegistryResponseSendingSim(common, dsSimCommon), er);

			mvc.run();

			return true;
		}

		return false;
	}

	public Metadata getMetadata() {
		return m;
	}

	public AdhocQueryResponse getAdhocQueryResponse() {
		return response;
	}

	public Response getResponse() {
		return response;
	}



}
