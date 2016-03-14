package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LogMessage;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class Structure {
	Metadata m;
	RegistryErrorListGenerator rel;
	boolean is_submit;
	LogMessage log_message;


	public Structure(Metadata m, boolean is_submit) throws XdsInternalException {
		this.m = m;
		rel = new RegistryErrorListGenerator(
				(m.isVersion2()) ? RegistryErrorListGenerator.version_2 : RegistryErrorListGenerator.version_3,
						false /* log */);
		this.is_submit = is_submit;
		log_message = null;
	}

	public Structure(Metadata m, boolean is_submit, RegistryErrorListGenerator rel, 	LogMessage log_message) throws XdsInternalException {
		this.m = m;
		this.is_submit = is_submit;
		this.rel = rel;
		this.log_message = log_message;
	}

	public void run()  throws MetadataException, MetadataValidationException, LoggerException, XdsException {
		submission_structure();

	}


	void submission_structure()  throws MetadataException, MetadataValidationException, LoggerException, XdsException {
		ss_doc_fol_must_have_ids();
		if (is_submit) {
			doc_implies_ss();
			fol_implies_ss();
			ss_implies_doc_or_fol_or_assoc();
			docs_in_ss();
			fols_in_ss();
			rplced_doc_not_in_submission();
			ss_status_relates_to_ss();
			by_value_assoc_in_submission();
			//folder_assocs();
		}
		ss_status_single_value();
		assocs_have_proper_namespace();
	}

	void ss_status_relates_to_ss() {
		String ss_id = m.getSubmissionSetId();
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = assocs.get(i);
			String a_target = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String a_type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			String a_source = assoc.getAttributeValue(MetadataSupport.source_object_qname);

			boolean target_is_included_is_doc = m.getExtrinsicObjectIds().contains(a_target);

			if (a_source.equals(ss_id)) {
				if ( !a_type.equals(assoc_type("HasMember"))) {
					err("Association referencing Submission Set has type " + a_type + " but only type " + assoc_type("HasMember") + " is allowed");
				}
				if (target_is_included_is_doc) {
					if ( ! m.hasSlot(assoc, "SubmissionSetStatus")) {
						err("Association " +
								assoc.getAttributeValue(MetadataSupport.id_qname) +
								" has sourceObject pointing to Submission Set but contains no SubmissionSetStatus Slot"
						);
					}
				} else if (m.getFolderIds().contains(a_target)) {

				} else {

				}
			}
			else {
				if ( m.hasSlot(assoc, "SubmissionSetStatus") && !"Reference".equals(m.getSlotValue(assoc, "SubmissionSetStatus", 0)))
					err("Association " +
							assoc.getAttributeValue(MetadataSupport.id_qname) +
							" does not have sourceObject pointing to Submission Set but contains SubmissionSetStatus Slot with value Original"
					);
			}
		}
	}

	void ss_doc_fol_must_have_ids() {
		List<OMElement> docs = m.getExtrinsicObjects();
		List<OMElement> rps = m.getRegistryPackages();

		for (int i=0; i<docs.size(); i++) {
			OMElement doc = docs.get(i);
			String id = doc.getAttributeValue(MetadataSupport.id_qname);
			if (	id == null ||
					id.equals("")
			) {
				err("All RegistryPackage and ExtrinsicObject objects must have id attributes");
				return;
			}
		}
		for (int i=0; i<rps.size(); i++) {
			OMElement rp = rps.get(i);
			String id = rp.getAttributeValue(MetadataSupport.id_qname);
			if (	id == null ||
					id.equals("")
			) {
				err("All RegistryPackage and ExtrinsicObject objects must have id attributes");
				return;
			}
		}
	}

	void by_value_assoc_in_submission() throws MetadataValidationException, MetadataException {
		List<OMElement> assocs = m.getAssociations();
		String ss_id = m.getSubmissionSetId();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = assocs.get(i);
			String source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
//			String type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			String target = assoc.getAttributeValue(MetadataSupport.target_object_qname);

			if ( !source.equals(ss_id))
				continue;

			boolean target_is_included_doc = m.getExtrinsicObjectIds().contains(target);

			String ss_status = m.getSlotValue(assoc, "SubmissionSetStatus", 0);

			if ( target_is_included_doc ) {

				if (ss_status == null || ss_status.equals("")) {
					err("SubmissionSetStatus Slot on Submission Set association has no value");
				} else if (	ss_status.equals("Original")) {
					if ( !m.containsObject(target))
						err("SubmissionSetStatus Slot on Submission Set association has value 'Original' but the targetObject " + target + " references an object not in the submission");
				} else if (	ss_status.equals("Reference")) {
					if (m.containsObject(target))
						err("SubmissionSetStatus Slot on Submission Set association has value 'Reference' but the targetObject " + target + " references an object in the submission");
				} else {
					err("SubmissionSetStatus Slot on Submission Set association has unrecognized value: " + ss_status);
				}
			} else {
				if (ss_status != null && !ss_status.equals("Reference"))
					err("A SubmissionSet Assocation has the SubmissionSetStatus Slot but the target ExtrinsicObject is not part of the Submission");

			}
		}
	}


	void ss_status_single_value() {
		List<OMElement> assocs = m.getAssociations();
		String ss_id = m.getSubmissionSetId();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = assocs.get(i);
			String source = assoc.getAttributeValue(MetadataSupport.source_object_qname);
//			String type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
//			String target = assoc.getAttributeValue(MetadataSupport.target_object_qname);

			if ( !source.equals(ss_id))
				continue;

			String ss_status = m.getSlotValue(assoc, "SubmissionSetStatus", 1);
			if (ss_status != null)
				err("SubmissionSetStatus Slot on Submission Set association has more than one value");
		}
	}

	void ss_implies_doc_or_fol_or_assoc() {
		if (	m.getSubmissionSet() != null &&
				! (
						m.getExtrinsicObjects().size() > 0 ||
						m.getFolders().size() > 0 ||
						m.getAssociations().size() > 0
				))
			err("Submission contains Submission Set but no Documents or Folders or Associations");
	}


	// does this id represent a folder in this metadata or in registry?
	// ammended to only check current submission since this code is in common
	public boolean isFolder(String id) throws LoggerException, XdsException {
		return m.getFolderIds().contains(id);
//			return true;
//
//		if ( !id.startsWith("urn:uuid:"))
//			return false;
//
//		RegistryObjectValidator rov = new RegistryObjectValidator(new StoredQuerySupport(rel, log_message));
//
//		List<String> ids = new ArrayList<String>();
//		ids.add(id);
//
//		List<String> missing = rov.validateAreFolders(ids);
//		if (missing != null && missing.contains(id))
//			return false;
//
//
//		return true;
	}

	// Folder Assocs must be linked to SS by a secondary Assoc
	void folder_assocs() throws XdsException, LoggerException {
		String ssId = m.getSubmissionSetId();
		List<OMElement> non_ss_assocs = null;
		for (OMElement a : m.getAssociations()) {
			String sourceId = m.getAssocSource(a);
			if (m.getAssocTarget(a) == ssId)
				err("SubmissionSet may not the be target of an Association");
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
						m.getSimpleAssocType(a2).equals("HasMember")) {
					if (good) {
						err("Multiple HasMember Associations link Submission Set " + ssId +
								" and Association\n" + a  );
					} else {
						good = true;
					}

				}
			}
			if (good == false)
				err("A HasMember Association is required to link Submission Set " + ssId +
						" and Folder/Document Association\n" + a);
		}

	}

	void doc_implies_ss() {
		if (	m.getExtrinsicObjects().size() > 0 &&
				m.getSubmissionSet() == null )
			err("Submission contains an Document but no Submission Set");
	}

	void fol_implies_ss() {
		if (	m.getFolders().size() > 0 &&
				m.getSubmissionSet() == null )
			err("Submission contains a Folder but no Submission Set");
	}

	void docs_in_ss() {
		List<OMElement> docs = m.getExtrinsicObjects();

		for (int i=0; i<docs.size(); i++) {
			OMElement doc = docs.get(i);

			if ( !has_assoc(m.getSubmissionSetId(), assoc_type("HasMember"), doc.getAttributeValue(MetadataSupport.id_qname)))
				err("Document " + doc.getAttributeValue(MetadataSupport.id_qname) + " is not linked to Submission Set with " + assoc_type("HasMember") + " Association");

		}
	}

	void fols_in_ss() {
		List<OMElement> fols = m.getFolders();

		for (int i=0; i<fols.size(); i++) {
			OMElement fol = fols.get(i);

			if ( !has_assoc(m.getSubmissionSetId(), assoc_type("HasMember"), fol.getAttributeValue(MetadataSupport.id_qname)))
				err("Folder " + fol.getAttributeValue(MetadataSupport.id_qname) + " is not linked to Submission Set with " + assoc_type("HasMember") + " Association");

		}
	}

	String associationSimpleType(String assocType) {
		String[] parts = assocType.split(":");
		if (parts.length < 2)
			return assocType;
		return parts[parts.length-1];
	}

	void assocs_have_proper_namespace() {
		List<OMElement> assocs = m.getAssociations();

		for (OMElement a_ele : assocs) {
			String a_type = a_ele.getAttributeValue(MetadataSupport.association_type_qname);
			if (m.isVersion2() && a_type.startsWith("urn"))
				err("XDS.a does not accept namespace prefix on association type:  found " + a_type);
			if ( ! m.isVersion2()) {
				String simpleType = associationSimpleType(a_type);
				if (Metadata.iheAssocTypes.contains(simpleType)) {
					if ( !a_type.startsWith(MetadataSupport.xdsB_ihe_assoc_namespace_uri))
						err("XDS.b requires namespace prefix urn:ihe:iti:2007:AssociationType on association type " + simpleType )	;
				} else {
					if ( !a_type.startsWith(MetadataSupport.xdsB_eb_assoc_namespace_uri))
						err("XDS.b requires namespace prefix urn:oasis:names:tc:ebxml-regrep:AssociationType on association type " + simpleType )	;

				}
			}
		}
	}

	void rplced_doc_not_in_submission() throws MetadataException, MetadataValidationException {
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = assocs.get(i);
			String id = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			if (type.equals(assoc_type("RPLC")) && ! m.isReferencedObject(id))
				err("Replaced document (RPLC assocation type) cannot be in submission\nThe following objects were found in the submission:"
						+ m.getIdsOfReferencedObjects().toString());
		}
	}

	boolean has_assoc(String source, String type, String target) {
		List<OMElement> assocs = m.getAssociations();

		for (int i=0; i<assocs.size(); i++) {
			OMElement assoc = assocs.get(i);
			String a_target = assoc.getAttributeValue(MetadataSupport.target_object_qname);
			String a_type = assoc.getAttributeValue(MetadataSupport.association_type_qname);
			String a_source = assoc.getAttributeValue(MetadataSupport.source_object_qname);

			if (	a_target != null && a_target.equals(target) &&
					a_type   != null && a_type.  equals(type) &&
					a_source != null && a_source.equals(source)
			)
				return true;
		}
		return false;
	}

	String assoc_type(String type) {
		if (m.isVersion2()) return type;
		return "urn:oasis:names:tc:ebxml-regrep:AssociationType:" + type;
	}

	void err(String msg) {
		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, "Structure.java", null, null);
	}
}
