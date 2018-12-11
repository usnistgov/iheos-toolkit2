package gov.nist.toolkit.fhir.simulators.sim.rep;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.fhir.simulators.support.StoredDocument;
import gov.nist.toolkit.fhir.simulators.support.TransactionSimulator;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valregmsg.registry.RemoveMultipleResponse;
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

import java.util.Collection;
import java.util.List;

public class RemoveDocumentResponseSim extends TransactionSimulator implements RegistryResponseGeneratingSim{
	private DsSimCommon dsSimCommon;
	private List<RepIdUidPair> repIdUidPairs;
	private RemoveMultipleResponse response;
	private RepIndex repIndex;
	private String repositoryUniqueId;

	RemoveDocumentResponseSim(ValidationContext vc, List<RepIdUidPair> repIdUidPairs, SimCommon common, DsSimCommon dsSimCommon, String repositoryUniqueId) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.repIdUidPairs = repIdUidPairs;
		this.repIndex = dsSimCommon.repIndex;
		this.repositoryUniqueId = repositoryUniqueId;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = new RemoveMultipleResponse();


			OMElement root = response.getRoot();

			for (RepIdUidPair pair : repIdUidPairs) {
				String uid = pair.getDocUid();
				String repUid = pair.getRepUid();

				if (repUid == null || !repUid.equals(repositoryUniqueId)) {
					er.err(Code.XDSUnknownRepositoryId, "Unknown Repository Id " + repUid, null, null);
					continue;
				}

				if (uid == null) {
					er.err(Code.XDSDocumentUniqueIdError, "Unknown Document Id " + repUid, null, null);
					continue;
				}

				repIndex.delete(uid);
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

}
