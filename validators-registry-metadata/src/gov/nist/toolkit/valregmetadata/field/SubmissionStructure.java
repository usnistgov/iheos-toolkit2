package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class SubmissionStructure {
	Metadata m;
	RegistryValidationInterface rvi;
	boolean hasmember_error = false;


	public SubmissionStructure(Metadata m, RegistryValidationInterface rvi)  {
		this.m = m;
		this.rvi = rvi;
	}

	public void run(ErrorRecorder er, ValidationContext vc)   {
		submission_structure(er, vc);

	}


	void submission_structure(ErrorRecorder er, ValidationContext vc)   {
		if (vc.isSubmit() && vc.isRequest) 
			er.sectionHeading("Submission Structure");
		ss_doc_fol_must_have_ids(er, vc);
		if (vc.isSubmit() && vc.isRequest) {
			has_single_ss(er, vc);
			all_docs_linked_to_ss(er, vc);
			all_fols_linked_to_ss(er, vc);
			symbolic_refs_not_in_submission(er);
			eval_assocs(er);

			ss_status_single_value(er, vc);
			
			new PatientId(m, er).run();
		}
		if (hasmember_error) 
			log_hasmember_usage(er);
	}

	String assocDescription(OMElement obj) {
		return assocDescription(m.getId(obj));
	}

	String assocDescription(String id) {
		return "Association(" + id + ")";
	}

	String docEntryDescription(OMElement obj) {
		return docEntryDescription(m.getId(obj));
	}

	String docEntryDescription(String id) {
		return "DocumentEntry(" + id + ")";
	}

	String folderDescription(OMElement obj) {
		return folderDescription(m.getId(obj));
	}

	String folderDescription(String id) {
		return "Folder(" + id + ")";
	}

	String ssDescription(OMElement obj) {
		return "SubmissionSet(" + m.getId(obj) + ")";
	}

	boolean isSubmissionSet(String id) {
		if (id == null)
			return false;
		try {
			if (m.getId(m.getSubmissionSet()).equals(id))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	boolean isDocumentEntry(String id) {
		if (id == null)
			return false;
		return m.getExtrinsicObjectIds().contains(id);
	}

	boolean isAssoc(String id) {
		if (id == null)
			return false;
		return m.getAssociationIds().contains(id);
	}

	OMElement getObjectById(String id) {
		try {
			return m.getObjectById(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	boolean submissionContains(String id) {
		return getObjectById(id) != null;
	}

	void validateFolderHasMemberAssoc(ErrorRecorder er, String assocId) {
		OMElement assoc = getObjectById(assocId);
		if (simpleAssocType(m.getAssocType(assoc)).equals("HasMember")) {
			// must relate folder to docentry
			String source = m.getAssocSource(assoc);
			String target = m.getAssocTarget(assoc);
			if (source == null || target == null)
				return;
			// try to verify that source is a Folder
			if (m.isFolder(source)) {
				// is folder
			} else if (source.startsWith("urn:uuid:")) {
				// may be folder
				er.externalChallenge(source + " must be shown to be a Folder already in the registry");
			} else {
				// is not folder
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, source + " is not a Folder in submission and cannot already be in registry", this, assocsRef);
				hasmember_error = true;
			}
			// try to verify that target is a DocumentEntry
			if (m.isDocument(target)) {
				// is DocumentEntry
			} else if (target.startsWith("urn:uuid:")) {
				// may be DocumentEntry
				er.externalChallenge(source + " must be shown to be a DocumentEntry already in the registry");
			} else {
				// is not DocumentEntry
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, source + " is not a DocumentEntry in submission and cannot already be in registry", this, assocsRef);
				hasmember_error = true;
			}
		} else {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assocDescription(assocId) + ": only Folder to DocumentEntry associations can be members of SubmissionSet (linked to SubmissionSet object via HasMember association", this, assocsRef);
			hasmember_error = true;
		}
	}
	
	boolean isMemberOfSS(String id) {
		String ssid = m.getSubmissionSetId();
		return haveAssoc("HasMember", ssid, id);
	}
	
	void log_hasmember_usage(ErrorRecorder er) {
		
		er.detail("A HasMember association can be used to do the following:");
		er.detail("  Link the SubmissionSet to a DocumentEntry in the submission (if it has SubmissionSetStatus value of Original)");
		er.detail("  Link the SubmissionSet to a DocumentEntry already in the registry (if it has SubmissionSetStatus value of Reference)");
		er.detail("  Link the SubmissionSet to a Folder in the submission");
		er.detail("  Link the SubmissionSet to a HasMember association that links a Folder to a DocumentEntry.");
		er.detail("    The Folder and the DocumentEntry can be in the submisison or already in the registry");
		
	}
	
	boolean haveAssoc(String type, String source, String target) {
		String simpleType = simpleAssocType(type);
		for (OMElement assoc : m.getAssociations()) {
			if (!simpleType.equals(simpleAssocType(m.getAssocType(assoc))))
				continue;
			if (!source.equals(m.getAssocSource(assoc)))
				continue;
			if (!target.equals(m.getAssocTarget(assoc)))
				continue;
			return true;
		}
		return false;
	}

	boolean is_ss_to_de_hasmember(OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = getSimpleAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return false;
		
		if (!type.equals("HasMember"))
			return false;
		
		if (!source.equals(m.getSubmissionSetId()))
			return false;
		
		if (!m.getExtrinsicObjectIds().contains(target))
			return false;
		
		if (!is_sss_original(assoc))
			return false;
		return true;
	}
	
	public boolean is_fol_to_de_hasmember(OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = getSimpleAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return false;
		
		if (!type.equals("HasMember"))
			return false;
		
		if (!m.getFolderIds().contains(source)) {
			if (isUUID(source)) {
				if (rvi != null && !rvi.isFolder(source))
					return false;
			} else {
				return false;
			}
		}
		
		if (!m.getExtrinsicObjectIds().contains(target)) {
			if (isUUID(target)) {
				if (rvi != null && !rvi.isDocumentEntry(target))
					return false;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	boolean is_ss_to_existing_de_hasmember(OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = getSimpleAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return false;
		
		if (!type.equals("HasMember"))
			return false;
		
		if (!source.equals(m.getSubmissionSetId()))
			return false;
		
		if (submissionContains(target) || !isUUID(target))
			return false;
		
		if (!is_sss_reference(assoc))
			return false;
		return true;
	}
	
	boolean is_ss_to_folder_hasmember(OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = getSimpleAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return false;
		
		if (!type.equals("HasMember"))
			return false;
		
		if (!source.equals(m.getSubmissionSetId()))
			return false;
		
		if (!m.getFolderIds().contains(target))
			return false;
		
		return true;
		
	}
	
	boolean is_ss_to_folder_hasmember_hasmember(OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = getSimpleAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return false;
		
		if (!type.equals("HasMember"))
			return false;
		
		if (!source.equals(m.getSubmissionSetId()))
			return false;
		
		if (!m.getAssociationIds().contains(target))
			return false;
		
		// target association - should link a folder and a documententry
		// folder can be in submission or registry
		// same for documententry
		OMElement tassoc = getObjectById(target);
		
		// both source and target of tassoc have to be uuids and not in submission
		// hopefully in registry
		
		String ttarget = m.getAssocTarget(tassoc);
		String tsource = m.getAssocSource(tassoc);
		
		
		// for both the target and source
		// if points to an object in submission, can be symbolic or uuid
		//     but object must be HasMember Association
		// if points to an object in registry, must be uuid
		
		if (submissionContains(tsource)) {
			// tsource must be folder
			if (!m.getFolderIds().contains(tsource))
				return false;
		} else {
			// in registry?
			if (isUUID(tsource)) {
				if (rvi != null && !rvi.isFolder(tsource))
					return false;
			} else {
				return false;
			}
		}
		
		if (submissionContains(ttarget)) {
			// ttarget must be a DocumentEntry
			if (!m.getExtrinsicObjectIds().contains(ttarget))
				return false;
		} else {
			// in registry?
			if (isUUID(ttarget)) {
				if (rvi != null && !rvi.isDocumentEntry(ttarget))
					return false;
			} else {
				return false;
			}
		}
		

		// registry contents validation needed here
		// to show that the tsource references a folder 
		// and ttarget references a non-deprecated docentry
		
		return true;
	}
	
	boolean isUUID(String id) {
		return id != null && id.startsWith("urn:uuid:");
	}
	
	String objectType(String id) {
		if (id == null)
			return "null";
		if (m.getSubmissionSetIds().contains(id))
			return "SubmissionSet";
		if (m.getExtrinsicObjectIds().contains(id))
			return "DocumentEntry";
		if (m.getFolderIds().contains(id))
			return "Folder";
		if (m.getAssociationIds().contains(id))
			return "Association";
		return "Unknown";
	}
	
	String objectDescription(String id) {
		return objectType(id) + "(" + id + ")";
	}
	
	String objectDescription(OMElement ele) {
		return objectDescription(m.getId(ele));
	}
	
	String assocsRef = "ITI Tf-3: 4.1";

	void evalHasMember(ErrorRecorder er, OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = m.getAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return;
		
		if (is_ss_to_de_hasmember(assoc)) {
			er.detail(assocDescription(assoc) + ": is a SubmissionSet to DocmentEntry HasMember association");
		} else if (is_ss_to_existing_de_hasmember(assoc)) {
			er.detail(assocDescription(assoc) + ": is a SubmissionSet to existing DocmentEntry HasMember (ByReference) association");
		} else if (is_ss_to_folder_hasmember(assoc)) {
			er.detail(assocDescription(assoc) + ": is a SubmissionSet to Folder HasMember association");
		} else if (is_ss_to_folder_hasmember_hasmember(assoc)) {
			er.detail(assocDescription(assoc) + ": is a SubmissionSet to Folder-HasMember HasMember association (adds existing DocumentEntry to existing Folder)");
		} else if (is_fol_to_de_hasmember(assoc)) {
			er.detail(assocDescription(assoc) + ": is a Folder to DocumentEntry HasMember association");
		} else {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assocDescription(assoc) + ": do not understand this HasMember association. " +
					"sourceObject is " + objectDescription(source) +
					" and targetObject is " + objectDescription(target), this, assocsRef);
			hasmember_error = true;
		}
	}

	void evalRelationship(ErrorRecorder er, OMElement assoc) {
		String source = m.getAssocSource(assoc);
		String target = m.getAssocTarget(assoc);
		String type = m.getAssocType(assoc);
		
		if (source == null || target == null || type == null)
			return;

		if (!isDocumentEntry(source))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc) + ": with type " + simpleAssocType(type) + " must reference a DocumentEntry in submission with its sourceObject attribute, it references " + objectDescription(source), this, "ITI TF-3: 4.1.6.1");
		
		if (containsObject(target)) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc) + ": with type " + simpleAssocType(type) + " must reference a DocumentEntry in the registry with its targetObject attribute, it references " + objectDescription(target) + " which is in the submission", this, "ITI TF-3: 4.1.6.1");
		}
		
		if (!isUUID(target)) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc) + ": with type " + simpleAssocType(type) + " must reference a DocumentEntry in the registry with its targetObject attribute, it references " + objectDescription(target) + " which is a symbolic ID that cannot reference an object in the registry", this, "ITI TF-3: 4.1.6.1");
		}
	}

	void evalSigns(ErrorRecorder er, OMElement assoc) {

	}

	static List<String> relationships = 
		Arrays.asList(
				"HasMember",
				"RPLC",
				"XFRM",
				"XFRM_RPLC",
				"APND"
		);

	void eval_assocs(ErrorRecorder er) {
		for (OMElement assoc : m.getAssociations()) {
			String type = m.getAssocType(assoc);
			if (type == null)
				continue;
			type = simpleAssocType(type);
			if (type.equals("HasMember")) {
				evalHasMember(er, assoc);
			} else if(relationships.contains(type)) {
				evalRelationship(er, assoc);
			} else if (type.equals("signs")) {
				evalSigns(er, assoc);
			}

		}
	}

	String simpleAssocType(String qualifiedType) {
		if (qualifiedType == null)
			return "";
		int i = qualifiedType.lastIndexOf(':');
		if (i == -1)
			return qualifiedType;
		try {
			return qualifiedType.substring(i+1);
		} catch (Exception e) {
			return qualifiedType;
		}
	}

	void cannotValidate(ErrorRecorder er, String context) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, context + ": cannot validate - error parsing", this, "ebRIM");
	}

	void has_single_ss(ErrorRecorder er, ValidationContext vc) {
		List<OMElement> ssEles = m.getSubmissionSets();
		if (ssEles.size() == 0) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Submission does not contain a SubmissionSet", this, "ITI TF-3: 4.1.4");
		} else if (ssEles.size() > 1) {
			List<String> doc = new ArrayList<String>();
			for (String ssid : m.getSubmissionSetIds())
				doc.add(objectDescription(ssid));
			
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Submission contains multiple SubmissionSets: " + doc , this, "ITI TF-3: 4.1.4");
		} else
			er.detail(ssDescription(ssEles.get(0)) + ": SubmissionSet found");
	}
	
	void symbolic_refs_not_in_submission(ErrorRecorder er) {
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String target = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			String source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
			
			if (target == null || source == null || type == null)
				continue;
			
			if (!isUUID(source) && !submissionContains(source))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc) + ": sourceObject has value " + source +
						" which is not in the submission but cannot be in registry since it is not in UUID format", this, "ITI TF-3: 4.1.12.3");

			if (!isUUID(target) && !submissionContains(target))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, objectDescription(assoc) + ": targetObject has value " + target +
						" which is not in the submission but cannot be in registry since it is not in UUID format", this, "ITI TF-3: 4.1.12.3");
}
	}



	void sss_relates_to_ss(ErrorRecorder er, ValidationContext vc) {
		String ss_id = m.getSubmissionSetId();
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String a_target = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String a_type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			String a_source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
			if (a_target == null) {
				cannotValidate(er, "Association(" + assoc.getAttributeValue(MetadataSupport.id_qname) + ") - targetObject");
				return;
			}
			if (a_source == null) {
				cannotValidate(er, "Association(" + assoc.getAttributeValue(MetadataSupport.id_qname) + ") - sourceObject");
				return;
			}
			if (a_type == null) {
				cannotValidate(er, "Association(" + assoc.getAttributeValue(MetadataSupport.id_qname) + ") - associationType");
				return;
			}

			boolean target_is_included_is_doc = m.getExtrinsicObjectIds().contains(a_target); 

			if (a_source.equals(ss_id)) {
				String hm = assoc_type("HasMember");
				if ( !a_type.equals(hm)) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Association referencing SubmissionSet has type " + a_type + " but only type " + assoc_type("HasMember") + " is allowed", this, "ITI TF-3: 4.1.4");
					hasmember_error = true;
				}
				if (target_is_included_is_doc) {
					if ( ! m.hasSlot(assoc, "SubmissionSetStatus")) {
						er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Association(" +
								assoc.getAttributeValue(MetadataSupport.id_qname) +
								") has sourceObject pointing to SubmissionSet and targetObject pointing to a DocumentEntry but contains no SubmissionSetStatus Slot", this, "ITI TF-3: 4.1.4.1"
						);
					} 
				} else if (m.getFolderIds().contains(a_target)) {

				} else {

				}
			}
			else {
				if ( m.hasSlot(assoc, "SubmissionSetStatus") && !"Reference".equals(m.getSlotValue(assoc, "SubmissionSetStatus", 0)))
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Association " +
							assoc.getAttributeValue(MetadataSupport.id_qname) +
							" does not have sourceObject pointing to SubmissionSet but contains SubmissionSetStatus Slot with value Original", this, "ITI TF-3: 4.1.4.1"
					);
			}
		}
	}

	void ss_doc_fol_must_have_ids(ErrorRecorder er, ValidationContext vc) {
		List<OMElement> docs = m.getExtrinsicObjects();
		List<OMElement> rps = m.getRegistryPackages();

		for (int i=0; i<docs.size(); i++) {
			OMElement doc = (OMElement) docs.get(i);
			String id = doc.getAttributeValue(MetadataSupport.id_qname);
			if (	id == null ||
					id.equals("")
			) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "All RegistryPackage and ExtrinsicObject objects must have id attributes", this, "ebRIM 2.4.1");
				return;
			}
		}
		for (int i=0; i<rps.size(); i++) {
			OMElement rp = (OMElement) rps.get(i);
			String id = rp.getAttributeValue(MetadataSupport.id_qname);
			if (	id == null ||
					id.equals("")
			) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "All RegistryPackage and ExtrinsicObject objects must have id attributes", this, "ebRIM 2.4.1");
				return;
			}
		}
	}

	void by_value_assoc_in_submission(ErrorRecorder er, ValidationContext vc)  {
		List<OMElement> assocs = m.getAssociations();
		String ss_id = m.getSubmissionSetId();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
			String target = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			if (source == null)
				continue;
			if (target == null)
				continue;

			if ( !source.equals(ss_id))
				continue;

			boolean target_is_included_doc = m.getExtrinsicObjectIds().contains(target);
			
			if (m.getSlot(assoc, "SubmissionSetStatus") == null)
				return;

			String ss_status = m.getSlotValue(assoc, "SubmissionSetStatus", 0);

			if ( target_is_included_doc ) {

				if (ss_status == null || ss_status.equals("")) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSetStatus Slot on Submission Set association has no value", this, "ITI TF-3: 4.1.4.1");
				} else if (	ss_status.equals("Original")) {
					if ( !containsObject(target)) 
						er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSetStatus Slot on SubmissionSet association has value 'Original' but the targetObject " + target + " references an object not in the submission", 
								this, "ITI TF-3: 4.1.4.1");
				} else if (	ss_status.equals("Reference")) {
					if (containsObject(target))
						er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSetStatus Slot on SubmissionSet association has value 'Reference' but the targetObject " + target + " references an object in the submission",
								this, "ITI TF-3: 4.1.4.1");
				} else {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSetStatus Slot on Submission Set association has unrecognized value: " + ss_status, this, "ITI TF-3: 4.1.4.1");
				}
			} else {
				if (ss_status != null && !ss_status.equals("Reference")) 
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "A SubmissionSet Assocation has the SubmissionSetStatus Slot but the target ExtrinsicObject is not part of the Submission", this, "ITI TF-3: 4.1.4.1");

			}
		}
	}

	boolean containsObject(String id) {
		try {
			if (m.containsObject(id))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
//	boolean mustBeInRegistry(String id) {
//		return !containsObject(id) && isUUID(id);
//	}

	void ss_status_single_value(ErrorRecorder er, ValidationContext vc) {
		List<OMElement> assocs = m.getAssociations();
		String ss_id = m.getSubmissionSetId();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
			if (source == null)
				continue;

			if ( !source.equals(ss_id))
				continue;

			String ss_status = m.getSlotValue(assoc, "SubmissionSetStatus", 1);
			if (ss_status != null)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSetStatus Slot on SubmissionSet association has more than one value", this, "ITI TF-3: 4.1.4.1");
		}
	}

//	void ss_implies_doc_or_fol_or_assoc(ErrorRecorder er, ValidationContext vc) {
//		if (	m.getSubmissionSet() != null &&
//				! (
//						m.getExtrinsicObjects().size() > 0 ||
//						m.getFolders().size() > 0 ||
//						m.getAssociations().size() > 0
//				))
//			er.err("Submission contains a SubmissionSet but no DocumentEntries or Folders or Associations", "ITI TF-3: 4.1.3.1");
//	}


	// does this id represent a folder in this metadata or in registry?
	// ammended to only check current submission since this code is in common
	public boolean isFolder(String id)  {
		if (id == null)
			return false;
		return m.getFolderIds().contains(id);
	}

	// Folder Assocs must be linked to SS by a secondary Assoc
	void folder_assocs(ErrorRecorder er, ValidationContext vc)  {
		String ssId = m.getSubmissionSetId();
		List<OMElement> non_ss_assocs = null;
		for (OMElement a : m.getAssociations()) {
			String sourceId = m.getAssocSource(a);
			if (m.getAssocTarget(a) == ssId)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "SubmissionSet may not the be target of an Association", this, "ITI TF-3: 4.1.4");
			// if sourceId points to a SubmissionSet in this metadata then no further work is needed
			// if sourceId points to a Folder (in or out of this metadata) then secondary Assoc required
			if (sourceId.equals(ssId))
				continue;
			if (isFolder(sourceId)) {
				if (non_ss_assocs == null)
					non_ss_assocs = new ArrayList<OMElement>();
				non_ss_assocs.add(a);
			}
		}
		if (non_ss_assocs == null) return;

		// Show that the non-ss associations are linked to ss via a HasMember association
		// This only applies when the association's sourceObject is a Folder
		for (OMElement a : non_ss_assocs) {
			String aId = a.getAttributeValue(MetadataSupport.id_qname);
			boolean good = false;
			for (OMElement a2 : m.getAssociations()) {
				if (m.getAssocSource(a2).equals(ssId) &&
						m.getAssocTarget(a2).equals(aId) &&
						getSimpleAssocType(a2).equals("HasMember")) {
					if (good) {
						er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Multiple HasMember Associations link SubmissionSet " + ssId + 
								" and Association\n" + a, this, "ITI TF-3: 4.1.4");
					} else {
						good = true;
					}

				}
			}
			if (good == false)
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "A HasMember Association is required to link SubmissionSet " + ssId + 
						" and Folder/DocumentEntry Association\n" + a, this, "ITI TF-3: 4.1.4.2");
		}

	}

	String getSimpleAssocType(OMElement a) {
		try {
			return m.getSimpleAssocType(a);
		} catch (Exception e) {
			return "";
		}
	}

	void all_docs_linked_to_ss(ErrorRecorder er, ValidationContext vc) {
		List<OMElement> docs = m.getExtrinsicObjects();

		for (int i=0; i<docs.size(); i++) {
			OMElement doc = (OMElement) docs.get(i);
			
			OMElement assoc = find_assoc(m.getSubmissionSetId(), assoc_type("HasMember"), doc.getAttributeValue(MetadataSupport.id_qname));

			if ( assoc == null) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "DocumentEntry(" + 
						doc.getAttributeValue(MetadataSupport.id_qname) + 
						") is not linked to the SubmissionSet with a " + assoc_type("HasMember") + " Association",
						this, "ITI TF-3: 4.1.4.1");
			} else {
				if (!has_sss_slot(assoc)) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assocDescription(assoc) + 
							": links a DocumentEntry to the SubmissionSet but does not have a " + 
							"SubmissionSetStatus Slot with value Original", 
							this, "ITI TF-3: 4.1.4.1");
					hasmember_error = true;
				} else if (!is_sss_original(assoc)) {
					er.err(XdsErrorCode.Code.XDSRegistryMetadataError, assocDescription(assoc) + 
							": links a DocumentEntry to the SubmissionSet but does not have a " + 
							"SubmissionSetStatus Slot with value Original", 
							this, "ITI TF-3: 4.1.4.1");
					hasmember_error = true;
				}
			}

		}
	}

	void all_fols_linked_to_ss(ErrorRecorder er, ValidationContext vc) {
		List<OMElement> fols = m.getFolders();

		for (int i=0; i<fols.size(); i++) {
			OMElement fol = (OMElement) fols.get(i);

			if ( !has_assoc(m.getSubmissionSetId(), assoc_type("HasMember"), m.getId(fol))) {
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Folder " + m.getId(fol) + " is not linked to the SubmissionSet with a " + assoc_type("HasMember") + " Association",
						this, "ITI TF-3: 4.1.4.2");
				hasmember_error = true;
			}

		}
	}

	String associationSimpleType(String assocType) {
		String[] parts = assocType.split(":");
		if (parts.length < 2)
			return assocType;
		return parts[parts.length-1];
	}

	//	void assocs_have_proper_namespace() {
	//		List<OMElement> assocs = m.getAssociations();
	//
	//		for (OMElement a_ele : assocs) {
	//			String a_type = a_ele.getAttributeValue(MetadataSupport.association_type_qname);
	//			if (m.isVersion2() && a_type.startsWith("urn"))
	//				err("XDS.a does not accept namespace prefix on association type:  found " + a_type);
	//			if ( ! m.isVersion2()) {
	//				String simpleType = associationSimpleType(a_type);
	//				if (Metadata.iheAssocTypes.contains(simpleType)) {
	//					if ( !a_type.startsWith(MetadataSupport.xdsB_ihe_assoc_namespace_uri))
	//						err("XDS.b requires namespace prefix urn:ihe:iti:2007:AssociationType on association type " + simpleType )	;
	//				} else {
	//					if ( !a_type.startsWith(MetadataSupport.xdsB_eb_assoc_namespace_uri))
	//						err("XDS.b requires namespace prefix urn:oasis:names:tc:ebxml-regrep:AssociationType on association type " + simpleType )	;
	//
	//				}
	//			}
	//		}
	//	}

	void rplced_doc_not_in_submission(ErrorRecorder er, ValidationContext vc)  {
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String id = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			if (MetadataSupport.relationship_associations.contains(type) && ! isReferencedObject(id))
				er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "DocumentEntry referenced by a relationship style assocation " + MetadataSupport.relationship_associations + 
						" cannot be contained in submission\nThe following objects were found in the submission:" 
						+ getIdsOfReferencedObjects().toString(), this, "ITI TF-3: 4.1.6.1");
		}
	}
	
	List<String> getIdsOfReferencedObjects() {
		try {
			return m.getIdsOfReferencedObjects();
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	boolean isReferencedObject(String id) {
		try {
			return m.isReferencedObject(id);
		} catch (Exception e) {
			return false;
		}
	}
	
	boolean is_sss_original(OMElement assoc) {
		OMElement sss = get_sss_slot(assoc);
		if (sss == null)
			return false;
		String value = m.getSlotValue(assoc, "SubmissionSetStatus", 0);
		if (value == null)
			return false;
		if (value.equals("Original"))
			return true;
		return false;
	}
	
	boolean is_sss_reference(OMElement assoc) {
		OMElement sss = get_sss_slot(assoc);
		if (sss == null)
			return false;
		String value = m.getSlotValue(assoc, "SubmissionSetStatus", 0);
		if (value == null)
			return false;
		if (value.equals("Reference"))
			return true;
		return false;
	}
	
	boolean has_sss_slot(OMElement assoc) {
		return get_sss_slot(assoc) != null;
	}
	
	OMElement get_sss_slot(OMElement assoc) {
		return m.getSlot(assoc, "SubmissionSetStatus");
	}
	
	OMElement find_ss_hasmember_assoc(String target) {
		return find_assoc(m.getSubmissionSetId(), "HasMember", target);
	}
	
	boolean has_ss_hasmember_assoc(String target) {
		if (find_ss_hasmember_assoc(target) == null)
			return false;
		return true;
	}
	
	boolean has_assoc(String source, String type, String target) {
		if (find_assoc(source, type, target) == null)
			return false;
		return true;
	}

	OMElement find_assoc(String source, String type, String target) {
		
		if (source == null || type == null || target == null)
			return null;
		
		List<OMElement> assocs = m.getAssociations();
		
		type = simpleAssocType(type);

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = (OMElement) assocs.get(i);
			String a_target = m.getAssocTarget(assoc);
			String a_type = simpleAssocType(m.getAssocType(assoc)); 
			String a_source = m.getAssocSource(assoc);
			
			if (source.equals(a_source) &&
					target.equals(a_target) &&
					type.equals(a_type))
				return assoc;

		}		
		return null;
	}

	String assoc_type(String type) {
		if (m.isVersion2()) 
			return type;
		if (type.equals("HasMember"))
			return "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type;
		if (type.equals("RPLC") ||
				type.equals("XFRM") ||
				type.equals("APND") ||
				type.equals("XFRM_RPLC") ||
				type.equals("signs"))
			return "urn:ihe:iti:2007:AssociationType:" + type;
		return "";
	}

	//	void err(String msg) {
	//		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, "Structure.java", null);
	//	}
}
