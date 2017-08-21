package gov.nist.toolkit.simulators.sim.rep.od;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.simulators.support.od.TransactionUtil;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.*;

public class RetrieveOnDemandDocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim {
	static Logger logger = Logger.getLogger(RetrieveOnDemandDocumentResponseSim.class);
	DsSimCommon dsSimCommon;
	List<String> documentUids;
	List<String> dynamicDocumentUids = new ArrayList<String>();
	RetrieveMultipleResponse response;
	RepIndex repIndex;
	String repositoryUniqueId;
	SimulatorConfig simulatorConfig;


	public RetrieveOnDemandDocumentResponseSim(ValidationContext vc, List<String> documentUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId, SimulatorConfig simulatorConfig) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.documentUids = documentUids;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
		this.simulatorConfig = simulatorConfig;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		logger.info("Entering ODDS Persistence Retrieve Response");
		try {
			response = new RetrieveMultipleResponse();

			repIndex.restore();
			// Make sure the documentUids are real ones and not bogus to throw the XDSDocumentUniqueIdError
			dsSimCommon.addDocumentAttachments(documentUids, er);

			Collection<StoredDocument> documents = dsSimCommon.getAttachments();

			/*
			 Possible cases (x2 Persistence Option (P-on) on/off):
			 1a. Single UID and it is not found
			 	Return an XDS error
			 1b. Single UID found
				 P-on:
				 	1. PnR
				 	2. Get the content to include in the response
				 P-off:
				 	1. (No PnR) Get the content to include in the response
			 2a. Multiple UID and all not found
			 2b. Multiple UID and partial results
			 2c. Multiple UID and all found
			 */
			// ------------------------------------------------

//			String siteName = getSimulatorConfig().get(SimulatorProperties.oddsRepositorySite).asList().get(0);


			// ---------------------------------------------------------------------------------------------------------
			String sessionName = getSimulatorConfig().getId().getUser();
			Session mySession = new Session(Installation.instance().warHome(), sessionName);
			getSimulatorConfig().getId().setEnvironmentName(Installation.instance().defaultEnvironmentName());
			mySession.setEnvironment(getSimulatorConfig().getId().getEnvironmentName());

			OMElement root = response.getRoot();

			for (StoredDocument document : documents) {
				if (document.getEntryDetail()==null) {
					logger.error("Null document entry in ODDS!  StoredDocument Uid: " + document.getUid());
					break;
				}

				DocumentEntryDetail ded = document.getEntryDetail();

//				TestInstance testId = ded.getTestInstance(); //getSimulatorConfig().get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT).asString();

				SiteSpec reposSite = ded.getReposSiteSpec();
				boolean persistenceOptn = reposSite!=null;

				// Is persistence option on then do a PnR
				if (persistenceOptn) {
					if (mySession.getMesaSessionName() == null) mySession.setMesaSessionName(sessionName);
					mySession.setSiteSpec(reposSite);
				}

				Map<String, String> params = new HashMap<>();
				String patientId =  getSimulatorConfig().get(SimulatorProperties.oddePatientId).asString(); //  "SKB1^^^&1.2.960&ISO";
				params.put("$patientid$", patientId);
				params.put("$od_doc_uuid$", ded.getId());

				if (persistenceOptn) {
					params.put("$repuid$", mySession.repUid);
					if (ded.getSnapshot()!=null) {
						params.put("$rplc_doc_uuid$", ded.getSnapshot().getId());
					}
				}

				Map<String,String> rsMap = TransactionUtil.getOdContentFile(persistenceOptn, mySession, sessionName
						, reposSite
						, ded, getSimulatorConfig().getId(), params);
				document.setPathToDocument(rsMap.get("file"));
				document.setMimetype(rsMap.get("mimeType"));

				OMElement docResponse = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

				OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
				repId.setText(repositoryUniqueId);
				docResponse.addChild(repId);

				OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
				docId.setText(document.getUid());
				docResponse.addChild(docId);

				// ----- Begin On-Demand

				// Q: Does the NewRepositoryUniqueId needs to be added in. (Vol. 2b, 3.43.5.1.3)?
				// A: Not required without the Persistence option. (Bill)

				if (persistenceOptn) {
					OMElement newReposId = MetadataSupport.om_factory.createOMElement(MetadataSupport.newRepositoryUniqueId);
					newReposId.setText(rsMap.get("newRepsoitoryUniqueId"));
					docResponse.addChild(newReposId);
				}

				OMElement newDocId = MetadataSupport.om_factory.createOMElement(MetadataSupport.newDocumentUniqueId);
				if (persistenceOptn) {
					newDocId.setText(rsMap.get("snapshotUniqueId"));
				} else {
					// Temporary made Id
					newDocId.setText(document.getUid() + "." + ded.getSupplyStateIndex());
				}
				docResponse.addChild(newDocId);
				// ------ End On-Demand

				OMElement mimeType = MetadataSupport.om_factory.createOMElement(MetadataSupport.mimetype_qnamens);
				mimeType.setText(document.getMimeType());
				docResponse.addChild(mimeType);

				OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
				docResponse.addChild(doc);

				OMElement include = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
				OMAttribute href = MetadataSupport.om_factory.createOMAttribute("href", null, "cid:" + document.cid);
				include.addAttribute(href);
				doc.addChild(include);

				root.addChild(docResponse);
			}


			// ------------------------------------------------------------------------------------------------------
		} catch (Exception e) {
				er.err(Code.XDSRepositoryError, e);
				return;
			}
	}



	/*
	private Result Transaction(String siteName, String sessionName, TestInstance testId, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, List<String> SECTIONS) {
		UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
		if (myTestSession.getMesaSessionName() == null) myTestSession.setMesaSessionName(sessionName);

		SiteSpec siteSpec = new SiteSpec();

		logger.info("index 0 has:" + siteName); // This should always be the selected value
		siteSpec.setName(siteName);
		myTestSession.setSiteSpec(siteSpec);

		Result result = utilityRunner.run(myTestSession, params, null, SECTIONS, testId, null, stopOnFirstError);

		// Save results to external_cache.
		// Supports getTestResults tookit api call
		ResultPersistence rPer = new ResultPersistence();
		try {
            rPer.write(result, sessionName);
        } catch (Exception e) {
            result.assertions.add(ExceptionUtil.exception_details(e), false);
        }
		return result;
	}
	*/


	public Response getResponse() {
		return response;
	}
	public SimulatorConfig getSimulatorConfig() {
		return simulatorConfig;
	}

}
