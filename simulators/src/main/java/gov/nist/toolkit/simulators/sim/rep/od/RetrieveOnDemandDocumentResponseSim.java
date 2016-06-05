package gov.nist.toolkit.simulators.sim.rep.od;

import gov.nist.toolkit.actorfactory.OnDemandDocumentSourceActorFactory;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.results.client.DocumentEntryDetail;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.simulators.support.od.TransactionUtil;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrieveOnDemandDocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim {
	static Logger logger = Logger.getLogger(RetrieveOnDemandDocumentResponseSim.class);
	DsSimCommon dsSimCommon;
	List<String> documentUids;
	List<String> dynamicDocumentUids = new ArrayList<String>();
	RetrieveMultipleResponse response;
	RepIndex repIndex;
	String repositoryUniqueId;
	SimulatorConfig simulatorConfig;
	boolean persistenceOptn = false;

	public RetrieveOnDemandDocumentResponseSim(ValidationContext vc, List<String> documentUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId, SimulatorConfig simulatorConfig) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.documentUids = documentUids;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
		this.simulatorConfig = simulatorConfig;
		// Detect persistence simulator setting
		this.persistenceOptn = getSimulatorConfig().get(SimulatorProperties.PERSISTENCE_OF_RETRIEVED_DOCS).asBoolean();

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
			Session mySession = new Session(Installation.installation().warHome(), sessionName);
			getSimulatorConfig().getId().setEnvironmentName(Installation.installation().defaultEnvironmentName());
			mySession.setEnvironment(getSimulatorConfig().getId().getEnvironmentName());


			OMElement root = response.getRoot();


			for (StoredDocument document : documents) {

				// Is persistence option on then do a PnR
				String testPlanId =  getSimulatorConfig().get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT).asString();
				TestInstance testId = new TestInstance(testPlanId);

				if (document.getEntryDetail()==null) {
					logger.error("Null document entry in ODDS!  StoredDocument Uid: " + document.getUid());
					break;
				}

				DocumentEntryDetail ded = document.getEntryDetail();
				SiteSpec reposSite = null;

				if (persistenceOptn) {
					SimulatorConfigElement scReposEl = getSimulatorConfig().get(SimulatorProperties.oddsRepositorySite);
					if (scReposEl!=null) {
						if (scReposEl.asList()!=null) {
							reposSite = new SiteSpec(scReposEl.asList().get(0), ActorType.REPOSITORY, null);
						}
					}

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

				// TODO: Find out if the NewRepositoryUniqueId needs to be added in. (Vol. 2b, 3.43.5.1.3).
				// A: Not required without the Persistence option. (Bill)
				// FIXME: add new repos for persistence optn

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


	private void x() {
		/***
		 * TODO?: Extract class for the persistence?
		 * Document Response WBS:
		 * A) Check if persistence option is enabled for this sim, if it is on, then:
		 *
		 *  1) Get content supply state index, which could range from 0 to the number of available documents on disk minus 1
		 *  	a) if not in the last section, do a PnR (using api.runTest and replacing the prior document as needed) with the current section in the content bundle
		 *  	c) if in the last section, return previous document without a PnR
		 *  3) Get the StoredDocument resulting from the PnR (store the document entry Uuid)
		 *  4) Set NewDocumentId in the response
		 *  5) Update content supply state index -- it is the content of the document entry in the ODDS repository containing the ODDE UID, which is created when the original ODDE is initialized by the Initialize button.
		 * B) If no persistence, then just serve up the content from disk
		 */

		try {

		String testPlanId =  getSimulatorConfig().get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT).asString();
		String contentBundleState = null;

		String sessionName = getSimulatorConfig().getId().getUser();
		TestInstance testId = new TestInstance(testPlanId);

		boolean stopOnFirstError = true;
		Session myTestSession = new Session(Installation.installation().warHome(), sessionName);

		XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(myTestSession);

		List<String> testPlanSections = xdsTestServiceManager.getTestIndex(testPlanId);
		String registerSection = testPlanSections.get(0); // IMPORTANT NOTE: Make an assumption that the only (first) section is always the Register section which has the ContentBundle
		String contentBundle = testPlanId + "/" + registerSection + "/" + "ContentBundle";
		List<String> contentBundleSections = xdsTestServiceManager.getTestIndex(contentBundle);
		int contentBundleIdx = 0; //(supplyStateIdx==null || (supplyStateIdx!=null && "".equals(supplyStateIdx.asString())))?0: (supplyStateIdx!=null?Integer.parseInt(supplyStateIdx.asString()):0);
		String section = registerSection + "/" + "ContentBundle" + "/" + contentBundleSections.get(contentBundleIdx);
		logger.info("Selecting contentBundle section: " + section);

		List<String> sections = new ArrayList<String>(){};
		sections.add(section);

		Result result = null;
		// FIXME:
		// TODO: Result result = RunTestPlan.Transaction(siteName, sessionName, testId, params, stopOnFirstError, myTestSession, xdsTestServiceManager, sections);

		if (result.passed()) {
			// 1. Update the supplyStateIdx
			// NOTE: UI needs to be reloaded for this change to be reflected
			int nextStateIdx = ((contentBundleSections.size() < contentBundleIdx+1)?contentBundleIdx+1:contentBundleSections.size()-1); // Zero based index adjustment
//			supplyStateIdx.setValue(""+nextStateIdx);
//			getSimulatorConfig().get(SimulatorProperties.oddsContentSupplyState).setValue(""+nextStateIdx);
			new OnDemandDocumentSourceActorFactory().saveConfiguration(getSimulatorConfig());



			// 2. Send retrieve response
			// At this point, there are no documents in this repository, so insert a fake one here.
			String dynamicDocumentUuid = "od-doc-uid";
			StoredDocument storedDocument = repIndex.getDocumentCollection().getStoredDocument(dynamicDocumentUuid);
			if (storedDocument==null) { 			// Begin insert a fake document here
				storedDocument =  new StoredDocument("nonexistent-od-file-path",dynamicDocumentUuid);
				storedDocument.setContent("This content is served on-demand.".getBytes());
				storedDocument.setMimetype("text/plain");

				repIndex.getDocumentCollection().add(storedDocument);
			}
			// End

			// Replace all OD promise Uuids with fake dynamic contents
			for (String uid : documentUids) {
				dynamicDocumentUids.add(dynamicDocumentUuid);
			}

			dsSimCommon.addDocumentAttachments(dynamicDocumentUids, er); // Old param was (documentUids)
			// End On-Demand update block

			Collection<StoredDocument> documents = dsSimCommon.getAttachments();

			OMElement root = response.getRoot();


			int cx = 0;
			for (StoredDocument document : documents) {
				String uid = document.getUid();

				StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);

				OMElement docResponse = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_response_qnamens);

				OMElement repId = MetadataSupport.om_factory.createOMElement(MetadataSupport.repository_unique_id_qnamens);
				repId.setText(repositoryUniqueId);
				docResponse.addChild(repId);

				OMElement docId = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_unique_id_qnamens);
				docId.setText(documentUids.get(cx));
				/* "sd.uid" is the value of the bogus StoredDocument inserted above.
				We are restoring the original promise Id. This is because OD changes are temporary in this class and changes elsewhere should not be needed.
				The order of the collection iterator doesn't matter here because the bogus content is exactly the same for any OD promise (for now).
				* */
				docResponse.addChild(docId);

				// ----- Begin On-Demand
				OMElement newDocId = MetadataSupport.om_factory.createOMElement(MetadataSupport.newDocumentUniqueId);
//				newDocId.setText(documentUids.get(cx++) + "." + supplyStateIdx );
				docResponse.addChild(newDocId);

				// TODO: Find out if the NewRepositoryUniqueId needs to be added in. (Vol. 2b, 3.43.5.1.3).
				// A: Not required without the Persistence option. (Bill)
				// ------ End On-Demand

				OMElement mimeType = MetadataSupport.om_factory.createOMElement(MetadataSupport.mimetype_qnamens);
				mimeType.setText(sd.getMimeType());
				docResponse.addChild(mimeType);

				OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
				docResponse.addChild(doc);

				OMElement include = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
				OMAttribute href = MetadataSupport.om_factory.createOMAttribute("href", null, "cid:" + document.cid);
				include.addAttribute(href);
				doc.addChild(include);

				root.addChild(docResponse);
			}
		} else {
			// TODO: what to do if PnR failed?
		}

		} catch (Exception ex) {

		}

	}



	/*
	private Result Transaction(String siteName, String sessionName, TestInstance testId, Map<String, String> params, boolean stopOnFirstError, Session myTestSession, XdsTestServiceManager xdsTestServiceManager, List<String> sections) {
		UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
		if (myTestSession.getMesaSessionName() == null) myTestSession.setMesaSessionName(sessionName);

		SiteSpec siteSpec = new SiteSpec();

		logger.info("index 0 has:" + siteName); // This should always be the selected value
		siteSpec.setName(siteName);
		myTestSession.setSiteSpec(siteSpec);

		Result result = utilityRunner.run(myTestSession, params, null, sections, testId, null, stopOnFirstError);

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

	public boolean isPersistenceOptn() {
		return persistenceOptn;
	}
}
