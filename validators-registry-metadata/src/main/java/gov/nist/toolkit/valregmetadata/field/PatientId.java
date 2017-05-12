package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.List;

public class PatientId {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();
	List<String> patient_ids;
	IErrorRecorder er;
	Metadata m;


	public PatientId( Metadata m, IErrorRecorder er) {
		this.er = er;
		this.m = m;
		patient_ids = new ArrayList<String>();
	}

	public void run()  {
		try {
			gather_patient_ids(m, m.getSubmissionSetIds(),   MetadataSupport.XDSSubmissionSet_patientid_uuid);
			gather_patient_ids(m, m.getExtrinsicObjectIds(), MetadataSupport.XDSDocumentEntry_patientid_uuid);
			gather_patient_ids(m, m.getFolderIds(),          MetadataSupport.XDSFolder_patientid_uuid);

			String assertionID = "TA002";
			Assertion assertion = ASSERTIONLIBRARY.getAssertion(assertionID);

			if (patient_ids.size() > 1) {
				String detail = String.join(", ", patient_ids);
				er.err(XdsErrorCode.Code.XDSPatientIdDoesNotMatch, assertion, this, "", detail);
			}
		} catch (Exception e) {
			String assertionID = "TA003";
			Assertion a = ASSERTIONLIBRARY.getAssertion(assertionID);
			String detail = e.getMessage();
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, a, this, "", detail);
		}
	}

	void gather_patient_ids(Metadata m, List<String> parts, String uuid) throws MetadataException {
		String patient_id;
		for (String id : parts) {
			patient_id = m.getExternalIdentifierValue(id, uuid);
			if (patient_id == null) continue;
			if ( ! patient_ids.contains(patient_id))
				patient_ids.add(patient_id);
		}
	}

}
