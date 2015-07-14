package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class SubmissionSetValidator extends ValidatorCommon {

	static final List<String> ss_slots =
		Arrays.asList(
				"submissionTime",
				"intendedRecipient"
		);

	static final List<String> identificationSchemes =
		Arrays.asList(
				MetadataSupport.XDSSubmissionSet_patientid_uuid,
				MetadataSupport.XDSSubmissionSet_sourceid_uuid,
				MetadataSupport.XDSSubmissionSet_uniqueid_uuid
		);
	
	static final List<String> classificationSchemes = 
		Arrays.asList(
				MetadataSupport.XDSSubmissionSet_author_uuid,
				MetadataSupport.XDSSubmissionSet_contentTypeCode_uuid
		);

	public SubmissionSetValidator(ValidatorCommon vc)  {
		cloneEnvironment(vc);
	}

	public void run()  {
		validate_extids();
		slots_are_legal();
		validate_slots();
		validate_classifications();
	}

	void validate_extids()  {
		List<String> ss_ids = m.getSubmissionSetIds();

		try {
			for (int i=0; i<ss_ids.size(); i++) {
				String id = (String) ss_ids.get(i);
				OMElement ssEle = m.getObjectById(id);
				List<OMElement> ext_ids = m.getExternalIdentifiers(id);

				// has necessary ExternalIdentifiers
				//													name							identificationScheme                          OID value required
				validate_ext_id_present("Submission Set", id, ext_ids, "XDSSubmissionSet.patientId", MetadataSupport.XDSSubmissionSet_patientid_uuid, false);
				validate_ext_id_present("Submission Set", id, ext_ids, "XDSSubmissionSet.sourceId",  MetadataSupport.XDSSubmissionSet_sourceid_uuid,  true);
				validate_ext_id_present("Submission Set", id, ext_ids, "XDSSubmissionSet.uniqueId",  MetadataSupport.XDSSubmissionSet_uniqueid_uuid,  true);

				// does not have ExternalIdentifiers not defined by XDS
				List<OMElement> extIds = m.getExternalIdentifiers(ssEle);
				for (OMElement extId : extIds) {
					String idScheme = extId.getAttributeValue(MetadataSupport.identificationscheme_qname);
					if (!identificationSchemes.contains(idScheme))
						err("ExternalIdentifier found in SubmissionSet(" + id + "): identificationScheme of " + idScheme + " is not defined in XDS");
				}
			}
		} catch (MetadataException e) {
			err(e);
		}
	}

	void slots_are_legal()  {
		try {
			String ss_id = m.getSubmissionSetId();

			List<OMElement> slots = m.getSlots(ss_id);
			for (int j=0; j<slots.size(); j++) {
				OMElement slot = (OMElement) slots.get(j);

				String slot_name = slot.getAttributeValue(MetadataSupport.slot_name_qname);
				if (slot_name == null) slot_name = "";
				if ( ! legal_slot_name(slot_name))
					err("Submission Set " + ss_id + ": " + slot_name + " is not a legal slot name for a Submission Set");
			}
		} catch (MetadataException e) {
			err(e);
		}
	}

	boolean legal_slot_name(String name) {
		if (name == null) return false;
		if (name.startsWith("urn:")) return true;
		return ss_slots.contains(name);
	}

	void validate_slots()  {
		try {
			List<String> ss_ids = m.getSubmissionSetIds();

			for (int i=0; i<ss_ids.size(); i++) {
				String id = (String) ss_ids.get(i);
				List<OMElement> slots = m.getSlots(id);

				//                      					name						multi	required	number
				validate_slot("Submission Set", id, slots, 	"submissionTime", 			false, 	true, 		true);

			}
		} catch (MetadataException e) {
			err(e);
		}
	}

	void validate_classifications()  {
		try {
			List<String> ss_ids = m.getSubmissionSetIds();

			for (int i=0; i<ss_ids.size(); i++) {
				String id = (String) ss_ids.get(i);
				List<OMElement> classs = m.getClassifications(id);

				//                                               classificatinScheme								name							required	multiple
				validate_class("SubmissionSet", id, classs, "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500" , 	"contentTypeCode",		true, 		 false);
			}		
		} catch (MetadataException e) {
			err(e);
		}
	}


}
