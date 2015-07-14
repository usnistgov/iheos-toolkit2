package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.MetadataException;

import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class FolderValidator extends ValidatorCommon {
	List<String> fol_slots = 
		Arrays.asList(
				"lastUpdateTime"
		);

	public FolderValidator(ValidatorCommon vc)  {
		cloneEnvironment(vc);
	}

	public void run() {
		validate_fol_class();
		validate_fol_slots_are_legal();
		validate_folder_slots();
		validate_folder_extids();
	}

	void validate_folder_extids()  {
		List<String> fol_ids = m.getFolderIds();
		for (int i=0; i<fol_ids.size(); i++) {
			String id = (String) fol_ids.get(i);
			try {
			List<OMElement> ext_ids = m.getExternalIdentifiers(id);

			//													name							identificationScheme            OID required
			validate_ext_id_present("Folder", id, ext_ids, "XDSFolder.patientId", "urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a", false);
			validate_ext_id_present("Folder", id, ext_ids, "XDSFolder.uniqueId", "urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a",  true);
			} catch (MetadataException e) {
				err(e);
			}
		}

	}

	void validate_folder_slots()  {
		List<String> fol_ids = m.getFolderIds();
		for (int i=0; i<fol_ids.size(); i++) {
//			String id = (String) fol_ids.get(i);
//			try {
//			List<OMElement> slots = m.getSlots(id);

			//                      					name						multi	required	number
			// query only
//			if (is_submit)
//				validate_slot("Folder", id, slots, 		"lastUpdateTime", 			false, 	false, 		true);
//			else
//				validate_slot("Folder", id, slots, 		"lastUpdateTime", 			false, 	true, 		true);
//			} catch (MetadataException e) {
//				err(e);
//			}
		}

	}

	void validate_fol_slots_are_legal()  {
		List<String> fol_ids = m.getFolderIds();

		for (int i=0; i<fol_ids.size(); i++) {
			String id = (String) fol_ids.get(i);

			try {
			List<OMElement> slots = m.getSlots(id);
			for (int j=0; j<slots.size(); j++) {
				OMElement slot = (OMElement) slots.get(j);

				String slot_name = slot.getAttributeValue(MetadataSupport.slot_name_qname);
				if (slot_name == null) slot_name = "";
				if ( ! legal_fol_slot_name(slot_name))
					err("Folder " + id + ": " + slot_name + " is not a legal slot name for a Folder");
			}
			} catch (MetadataException e) {
				err(e);
			}
		}
	}
	
	boolean legal_fol_slot_name(String name) {
		if (name == null) return false;
		if (name.startsWith("urn:")) return true;
		return fol_slots.contains(name);
	}

	void validate_fol_class()  {
		List<String> fol_ids = m.getFolderIds();

		for (int i=0; i<fol_ids.size(); i++) {
			String id = (String) fol_ids.get(i);
			try {
				List<OMElement> classs = m.getClassifications(id);

				//                                      classificationScheme						name							required	multiple
				validate_class("Folder", id, classs, MetadataSupport.XDSFolder_codeList_uuid , 	"codeList",						true, 		true);

			} catch (MetadataException e) {
				err(e);
			}
		}
	}
}
