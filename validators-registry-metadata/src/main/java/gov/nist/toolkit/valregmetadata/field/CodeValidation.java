package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.xdsexception.XdsInternalException;


//this gets invoked from both Validator.java and directly from Repository.  Should optimize the implementation so that codes.xml
//gets cached in memory.
public class CodeValidation extends CodeValidationBase {
//	RegistryErrorListGenerator rel;       all errors to go through the ErrorRecorder interface
	ErrorRecorder er;
	boolean is_submit;
	boolean xds_b;

	public CodeValidation(Metadata m, boolean is_submit, boolean xds_b, RegistryErrorListGenerator rel) throws XdsInternalException {
		super(1);

		this.m = m;
//		this.rel = rel;
		this.is_submit = is_submit;
		this.xds_b = xds_b;
		
		er = rel;    // all errors to go through the ErrorRecorder interface
	}
	
	public CodeValidation(Metadata m) {
		super();
		this.m = m;
		is_submit = true;
		xds_b = true;
	}

	// this is used for easy access to mime lookup
	public CodeValidation() throws XdsInternalException {
		super();
	}
	
	public void run() {
		run(er);
	}


//	public void run() throws MetadataException, XdsInternalException {
//		List<String> all_object_ids = m.getObjectIds(m.getAllObjects());
//
//		for (String obj_id : all_object_ids) {
//			List<OMElement> classifications = m.getClassifications(obj_id);
//
//			for (OMElement cl_ele : classifications) {
//
//				Classification cl = new Classification(m, cl_ele);
//				validate(cl);
//
//				validateAssocClassifications(cl);
//			}
//		}
//
//		for (OMElement doc_ele : m.getExtrinsicObjects()) {
//			String mime_type = doc_ele.getAttributeValue(MetadataSupport.mime_type_qname);
//			if ( !isValidMimeType(mime_type)) {
//				err("Mime type, " + mime_type + ", is not available in this Affinity Domain");
//			} else {
//				val("Mime type " + mime_type, null);
//			}
//
//			String objectType = doc_ele.getAttributeValue(MetadataSupport.object_type_qname);
//			if (objectType == null) {
//				err("XDSDocumentEntry has no objectType attribute");
//			}
//			else if ( !objectType.equals(MetadataSupport.XDSDocumentEntry_objectType_uuid)) {
//				err("XDSDocumentEntry has incorrect objectType, found " + objectType + ", must be " + MetadataSupport.XDSDocumentEntry_objectType_uuid);
//			} else {
//				val("XDSDocumentEntry.objectType", null);
//			}
//		}
//	}
//	
//	// if classified object is an Association, only some types of Associations can
//	// accept an associationDocumenation classification
//	private void validateAssocClassifications(Classification cl)
//	throws MetadataException {
//
//		String classification_type = cl.getClassificationScheme();
//		
//		if (classification_type == null || !classification_type.equals(MetadataSupport.XDSAssociationDocumentation_uuid))
//			return;  // not associationDocumenation classification
//		
//		String classified_object_id = cl.parent_id();
//		String classified_object_type = m.getObjectTypeById(classified_object_id);
//		if (classified_object_type == null)
//			return;
//		
//		if ( !classified_object_type.equals("Association")) {
//			err("associationDocumentation Classification (" + MetadataSupport.XDSAssociationDocumentation_uuid + ") can only be used on Associations");
//			return;
//		}
//		
//		String assoc_id = classified_object_id;
//		OMElement assoc_ele = m.getObjectById(assoc_id);
//		if (assoc_ele == null) 
//			return;
//		String assoc_type = m.getSimpleAssocType(assoc_ele);
//		for (int i=0; i<assocClassifications.length; i++) {
//			String a = assocClassifications[i];
//			if (a.equals(assoc_type))
//				return;
//		}
//		err("Association Type " + assoc_type + " cannot have an associationDocumentation classification");
//	}
//
//	void validate(Classification cl) {
//		String classification_scheme = cl.getClassificationScheme();
//
//		if (classification_scheme == null) {
//			String classification_node = cl.getClassificationNode();
//			if (classification_node == null || classification_node.equals("")) {
//				err("classificationScheme missing", cl);
//				return ;
//			} else
//				return;
//		}
//		if (classification_scheme.equals(MetadataSupport.XDSSubmissionSet_author_uuid))
//			return;
//		if (classification_scheme.equals(MetadataSupport.XDSDocumentEntry_author_uuid))
//			return;
//		String code = cl.getCodeValue();
//		String coding_scheme = cl.getCodeScheme();
//
//		if (code == null) {
//			err("code (nodeRepresentation attribute) missing", cl);
//			return ;
//		}
//		if (coding_scheme == null) {
//			err("codingScheme (Slot codingScheme) missing", cl);
//			return;
//		}
//		for (OMElement code_type : MetadataSupport.childrenWithLocalName(codes, "CodeType")) {
//			String class_scheme = code_type.getAttributeValue(MetadataSupport.classscheme_qname);
//
//			// some codes don't have classScheme in their definition
//			if (class_scheme != null && !class_scheme.equals(classification_scheme))
//				continue;
//
//			for (OMElement code_ele : MetadataSupport.childrenWithLocalName(code_type, "Code")) {
//				String code_name = code_ele.getAttributeValue(MetadataSupport.code_qname);
//				String code_scheme = code_ele.getAttributeValue(MetadataSupport.codingscheme_qname);
//				if ( 	code_name.equals(code) && 
//						(code_scheme == null || code_scheme.equals(coding_scheme) )
//				) {
//					val("Coding of " + code_scheme, null);
//					return;
//				}
//			}
//		}
//		val("Coding of " + coding_scheme, " (" + code + ") Not Found");
//		err("The code, " + code + ", is not found in the configuration for the Affinity Domain", cl);
//	}

//	void val(String topic, String msg ) {
//		if (msg == null) msg = "Ok";
////		rel.add_validation(topic, msg, "CodeValidation.java");
//		er.detail(topic + ": " + msg + " - CodeValidation.java");
//	}
//
//	void err(String msg, Classification cl) {
//		er.err(MetadataSupport.XDSRegistryMetadataError, cl.identifying_string() + ": " + msg, "CodeValidation.java", "Error", ADConfigError);
//	}
//
//	void err(String msg) {
//		er.err(MetadataSupport.XDSRegistryMetadataError, msg, "CodeValidation.java", "Error", ADConfigError);
//	}


}


