package gov.nist.toolkit.fhir.simulators.sim.ig;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequest;
import gov.nist.toolkit.registrymsg.registry.AdhocQueryRequestParser;
import gov.nist.toolkit.registrymsg.registry.AdhocQueryResponseParser;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.fhir.simulators.sim.reg.AdhocQueryResponseGeneratingSim;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.fhir.simulators.support.MetadataGeneratingSim;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.engine.RegistryUtility;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XcQuerySim extends AbstractMessageValidator implements MetadataGeneratingSim, AdhocQueryResponseGeneratingSim {
	SimCommon common;
	DsSimCommon dsSimCommon;
	AdhocQueryResponse response;
	Metadata m = new Metadata();
	Exception startUpException = null;
	Logger logger = Logger.getLogger(XcQuerySim.class);
	static List<String> findQueryIds = Arrays.asList(MetadataSupport.SQ_FindDocuments, MetadataSupport.SQ_FindFolders, MetadataSupport.SQ_FindSubmissionSets, MetadataSupport.SQ_GetAll);
	boolean isSecure;
	boolean isAsync;
	AdhocQueryRequest request;
	SimulatorConfig asc;
	XcQueryMockSoap mockSoap = null;  // for unit testing only

	public XcQuerySim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
		super(common.vc);
		this.common = common;
		this.dsSimCommon = dsSimCommon;
		this.asc = asc;
		isSecure = common.isTls();
		isAsync = false;


		// build response
		try {
			response = new AdhocQueryResponse(Response.version_3);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			startUpException = e;
		}
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);

		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		try {
			// if request didn't validate, return so errors can be reported
			if (dsSimCommon.hasErrors()) {
				response.add(dsSimCommon.getRegistryErrorList(), null);
				return;
			}

			// Get body of request
			SoapMessageValidator smv = (SoapMessageValidator) dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			OMElement ahqr = smv.getMessageBody();

			request = new AdhocQueryRequestParser(ahqr).getAdhocQueryRequest();

            SimManager simMgr = new SimManager("ignored");
//            List<Site> sites = simMgr.getSites(asc.rgSiteNames);
            List<Site> sites = simMgr.getSites(asc.getConfigEle(SimulatorProperties.respondingGateways).asList(), asc.getId().getTestSession());

            if (sites == null || sites.size() == 0) {
                er.err(Code.XDSRegistryError, "No RespondingGateways configured", this, null);
                return;
            }
			Sites remoteSites = new Sites(sites);


			if (findQueryIds.contains(request.getQueryId())) {
				// a find type query
				// should not have home attribute
				if (request.getHomeAtt() != null) {
					er.err(Code.XDSRegistryError, "Request is Query by Patient ID, homeCommunityId attribute is not allowed", this, "ITI TF-2b: 3.18.4.1.3 (XCA Supplement)");
					return;
				}

				// forward to all registered RGs
				int forwards=0;
				for (Site site : sites) {
					if (site.hasActor(ActorType.RESPONDING_GATEWAY)) {
						// forward the query
						forwards++;
						forwardQuery(site);
					}
				}
				if (forwards == 0) {
					er.err(Code.XDSRegistryError, "No RGs configured", this, null);
					return;
				}
			} else {
				// look at home to see where to route this message
				if (request.getHomeAtt() == null) {
					er.err(Code.XDSRegistryError, "Request missing homeCommunityId (home attribute)", this, null);
					return;
				}
				Site site = remoteSites.getSiteForHome(request.getHome());

				if (site == null) {
                    response.add_error(Code.XDSRegistryError.name(), "Don't have configuration for RG with homeCommunityId " + request.getHome(), this.getClass().getName(), null, null);
					er.err(Code.XDSRegistryError, "Don't have configuration for RG with homeCommunityId " + request.getHome(), this, null);
					return;
				}

				if (!site.hasActor(ActorType.RESPONDING_GATEWAY)) {
					er.err(Code.XDSRegistryError, "Requested RG " + request.getHome() + " is not configured as an RG", this, null);
					return;
				}

				// forward the query
				forwardQuery(site);
			}

			List<OMElement> results = m.getAllObjects(); // everything but ObjectRefs
			results.addAll(m.getObjectRefs());

			// forceably remove home attribute from RG response if requested by configuration.
			SimulatorConfigElement forceNoHomeEle = asc.getConfigEle(SimulatorProperties.returnNoHome);
			if (forceNoHomeEle.asBoolean()) {
				for (OMElement ele : results)
					m.unsetHome(ele);
			}

			response.addQueryResults(results, false);

			// set status
			boolean hasErrors = response.has_errors();
			boolean hasResults = response.getQueryResult().getFirstElement() != null;
			String status;

			if (hasErrors && !hasResults)
				status = MetadataSupport.status_failure;
			else if (hasErrors && hasResults)
				status = MetadataSupport.status_partial_success;
			else
				status = MetadataSupport.status_success;

			response.setForcedStatus(status);

		} catch (Exception e) {
			logException(er, e);
		}
        finally {
            er.unRegisterValidator(this);
        }

	}

	void forwardQuery(Site site) {
		String home = request.getHome();
		if (home == null)
			home = site.getHome();
		try {
			String endpoint = site.getEndpoint(TransactionType.XC_QUERY, isSecure, isAsync);
			er.detail("Forwarding query to " + endpoint);

			OMElement req = request.getAdhocQueryRequestElement();

			er.challenge("Request to RG is");
			er.detail(new OMFormatter(req).toString());

			gov.nist.toolkit.registrymsg.registry.AdhocQueryResponse rgResponse = sqCall(req, endpoint);

			er.challenge("Response from RG is");
			er.detail(new OMFormatter(rgResponse.getMessage()).toString());

			if (rgResponse.isSuccess()) {
				Metadata mr = MetadataParser.parseNonSubmission(rgResponse.getRegistryObjectListEle());

				String home2 = findHome(mr);
				if (home2 != null)
					home = home2;

				boolean ok = areObjectsLabeledWithHome(mr);

				if (ok)
					m.copy(mr);
			} else {

				// validate out XDSUnknownPatientId errors before adding to general response
				List<String> codesToFilter = new ArrayList<String>();
				codesToFilter.add(XdsErrorCode.Code.XDSUnknownPatientId.toString());
//				response.getRegistryErrorList().addRegistryErrorList(rgResponse.getRegistryErrorListEle(), codesToFilter, null);
				RegistryErrorListGenerator rel = response.getRegistryErrorList();
				rel.addRegistryErrorList(rgResponse.getRegistryErrorListEle(), codesToFilter, null);
				if (home != null)
					rel.setLocationPrefix(home + ": ");
			}
		} catch (Exception e) {
			logException(er, e);
		}
	}

	String findHome(Metadata mr) {
		// All ExtrinsicObjects, RegistryPackages, and ObjectRefs must be labeled with non empty home
		List<OMElement> all = new ArrayList<OMElement>();
		all.addAll(mr.getExtrinsicObjects());
		all.addAll(mr.getRegistryPackages());
		all.addAll(mr.getObjectRefs());

		boolean ok = true;
		for (OMElement ele : all) {
			String home = m.getHome(ele);
			if (home == null || home.equals("") || !home.startsWith("urn:oid:")) {
				continue;
			}
			return home;
		}
		return null;
	}

	boolean areObjectsLabeledWithHome(Metadata mr) {
		// All ExtrinsicObjects, RegistryPackages, and ObjectRefs must be labeled with non empty home
		List<OMElement> all = new ArrayList<OMElement>();
		all.addAll(mr.getExtrinsicObjects());
		all.addAll(mr.getRegistryPackages());
		all.addAll(mr.getObjectRefs());

		boolean ok = true;
		for (OMElement ele : all) {
			String home = m.getHome(ele);
			if (home == null || home.equals("") || !home.startsWith("urn:oid:")) {
				er.err(XdsErrorCode.Code.XDSMissingHomeCommunityId,
						ele.getLocalName() + " " + m.getId(ele) + " from RG " + home +
						" is missing the homeCommunityId (or is incorrectly formatted)",
						this,  "ITI TF-2b: 3.38.4.1.3 (XCA Supplement)");
				ok = false;
			}
		}
		return ok;
	}

	private void logException(ErrorRecorder er, Exception e) {
		String msg = e.getMessage();
		if (msg == null || msg.equals(""))
			msg = ExceptionUtil.exception_details(e);
		logger.error(msg);
		er.err(XdsErrorCode.Code.XDSRegistryError, msg, this, null);
	}

	gov.nist.toolkit.registrymsg.registry.AdhocQueryResponse sqCall(OMElement request, String endpoint) throws Exception {
		Soap soap = new Soap();
		soap.setAsync(false);
		soap.setUseSaml(false);

		OMElement result;
		if (mockSoap == null) {
			try {
				soap.soapCall(request,
						endpoint,
						false, //mtom
						true,  // WS-Addressing
						true,  // SOAP 1.2
						"urn:ihe:iti:2007:CrossGatewayQuery",
						"urn:ihe:iti:2007:CrossGatewayQueryResponse"
				);
			} catch (Exception e) {
				throw new Exception("Soap Call to endpoint " + endpoint + " failed - " + e.getMessage(), e);
			}
			result = soap.getResult();
		} else {
			result = mockSoap.call(endpoint, request);
		}

		RegistryUtility.schema_validate_local(result, MetadataTypes.METADATA_TYPE_SQ);

		gov.nist.toolkit.registrymsg.registry.AdhocQueryResponse response = new AdhocQueryResponseParser(result).getResponse();

		return response;
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
