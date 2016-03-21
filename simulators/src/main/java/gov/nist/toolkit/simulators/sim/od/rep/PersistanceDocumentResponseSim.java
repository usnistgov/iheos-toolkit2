package gov.nist.toolkit.simulators.sim.od.rep;

import gov.nist.toolkit.actorfactory.SimulatorProperties;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.TestRunType;
import gov.nist.toolkit.session.server.serviceManager.UtilityRunner;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.StoredDocument;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.testengine.engine.ResultPersistence;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistanceDocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim{
	static Logger logger = Logger.getLogger(PersistanceDocumentResponseSim.class);
	DsSimCommon dsSimCommon;
	List<String> documentUids;
	List<String> dynamicDocumentUids = new ArrayList<String>();
	RetrieveMultipleResponse response;
	RepIndex repIndex;
	String repositoryUniqueId;


	SimulatorConfig simulatorConfig;

	public PersistanceDocumentResponseSim(ValidationContext vc, List<String> documentUids, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId, SimulatorConfig simulatorConfig) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.documentUids = documentUids;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
		this.simulatorConfig = simulatorConfig;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = new RetrieveMultipleResponse();

			/***
			 * Document Response WBS:
			 * A) Check if persistence option is enabled for this sim, if it is on, then:
			 *
			 *  1) Get content state,
			 *  	a) if not in the last section, do a PnR (using api.runTest and replacing the prior document as needed) with the current section in the content bundle
			 *  	c)  return previous document if reached end of content state
			 *  3) Get the StoredDocument resulting from the PnR (store the document entry Uuid)
			 *  4) Set NewDocumentId in the response
			 *  5) Update content state -- how?
			 * B) If no persistence, then just serve up the content from disk
			 */

			String contentBundleId =  getSimulatorConfig().get(SimulatorProperties.TESTPLAN_TO_REGISTER_AND_SUPPLY_CONTENT).asString();
			SimulatorConfigElement sce = getSimulatorConfig().get(SimulatorProperties.currentContentBundleIdx);
			String contentBundleState = null;


			String sessionName = getSimulatorConfig().getId().getUser();
			TestInstance testId = new TestInstance(contentBundleId);


			Map<String, String> params = new HashMap<>();
			String patientId = "SKB1^^^&1.2.960&ISO";
			params.put("$patientid$", patientId);
			boolean stopOnFirstError = true;
 			Session myTestSession = new Session(Installation.installation().warHome(), sessionName);

			XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(myTestSession);

			int contentBundleIdx = (sce==null || (sce!=null && "".equals(sce.asString())))?0: (sce!=null?Integer.parseInt(contentBundleState):0);
			List<String> contentBundleSections = xdsTestServiceManager.getTestIndex(contentBundleId);

			String section = contentBundleSections.get(contentBundleIdx);
			logger.info("Selecting contentBundle section: " + section);

			List<String> sections = new ArrayList<String>(){};
			sections.add(section);


			UtilityRunner utilityRunner = new UtilityRunner(xdsTestServiceManager, TestRunType.TEST);
			if (myTestSession.getMesaSessionName() == null) myTestSession.setMesaSessionName(sessionName);

			SiteSpec siteSpec = new SiteSpec();
			String siteName = getSimulatorConfig().get(SimulatorProperties.oddsRepositorySite).asList().get(0);
			logger.info("index 0 has:" + siteName);
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



			// --------------

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

			if (documents!= null && documentUids!=null)
			if (documents.size() != documentUids.size()) {// This should always be equal in this case and only for OD where documents are bogus
				er.err(Code.XDSRepositoryError, new ToolkitRuntimeException("The On-Demand StoredDocument collection size does not match with the requested number of Uids."));
				return;
			}

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

				// Begin On-Demand
				OMElement newDocId = MetadataSupport.om_factory.createOMElement(MetadataSupport.newDocumentUniqueId);
				newDocId.setText(documentUids.get(cx++) + ".111" ); // TODO: Find out what needs to go here.
				docResponse.addChild(newDocId);

				// TODO: Find out if the NewRepositoryUniqueId needs to be added in. (Vol. 2b, 3.43.5.1.3).
				// A: Not required without the Persistence option. (Bill)
				// End On-Demand

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
		}
		catch (Exception e) {
			er.err(Code.XDSRepositoryError, e);
			return;
		}
	}

	public Response getResponse() {
		return response;
	}
	public SimulatorConfig getSimulatorConfig() {
		return simulatorConfig;
	}


}
