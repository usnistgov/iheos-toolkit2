package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.ArrayList;
import java.util.List;

public class PatientId {
	List<String> patient_ids;
	ErrorRecorder er;
	Metadata m;


	public PatientId( Metadata m, ErrorRecorder er) {
		this.er = er;
		this.m = m;
		patient_ids = new ArrayList<String>();
	}

	public void run()  {
		try {
		gather_patient_ids(m, m.getSubmissionSetIds(),   MetadataSupport.XDSSubmissionSet_patientid_uuid);
		gather_patient_ids(m, m.getExtrinsicObjectIds(), MetadataSupport.XDSDocumentEntry_patientid_uuid);
		gather_patient_ids(m, m.getFolderIds(),          MetadataSupport.XDSFolder_patientid_uuid);

		if (patient_ids.size() > 1)
			er.err(XdsErrorCode.Code.XDSPatientIdDoesNotMatch, "Multiple Patient IDs found in submission: " + patient_ids, this, "ITI TF-3: 4.1.4.1");
		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e.getMessage(), this, "");
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

//	void err(String msg) {
//		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, "PatientId.java", null);
//	}
}
