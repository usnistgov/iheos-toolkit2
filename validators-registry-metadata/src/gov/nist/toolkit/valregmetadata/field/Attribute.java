package gov.nist.toolkit.valregmetadata.field;


public class Attribute extends ValidatorCommon {

//
//	public Attribute(Metadata m, boolean is_submit, boolean xds_b, boolean isPnR) throws XdsInternalException {
//		this.m = m;
//		this.xds_b = xds_b;
//		this.rel = new RegistryErrorList( 
//				(m.isVersion2()) ? RegistryErrorList.version_2 : RegistryErrorList.version_3, 
//						false /* log */);
//		this.is_submit = is_submit;
//		this.isPnR = isPnR;
//
//	}
//
//	public Attribute(Metadata m, boolean is_submit, boolean xds_b, RegistryErrorList rel, boolean isPnR) throws XdsInternalException {
//		this.m = m;
//		this.rel = rel;
//		this.is_submit = is_submit;
//		this.xds_b = xds_b;
//		this.isPnR = isPnR;
//
//	}
//
//	public void setIsXDM(boolean isxdm) {
//		isXDM = isxdm;
//	}
//
//
//	public void run()   {
//
//		new SubmissionSetValidator(this).run();
//		new DocumentEntryValidator(this).run();
//		new FolderValidator(this).run();
//
//		if (is_submit && m.getSubmissionSets().size() == 0) 
//			err("Submission must contain a SubmissionSet object, none were found");
//
//		if (is_submit && m.getSubmissionSets().size() > 1) {
//			StringBuffer buf = new StringBuffer();
//
//			buf.append("Submission must contain a single SubmissionSet object, ");
//			buf.append(m.getSubmissionSets().size()).append(" were found:\n");
//
//			for (OMElement ss : m.getSubmissionSets()) {
//				String id = m.getId(ss);
//				buf.append("\t").append(id).append("\n");
//			}
//
//			err(buf.toString());
//		}
//
//		validate_package_class();
//
//		validate_author_structure();
//
//
//		// Associations?????
//		// Submission structure?????
//		// Symbolic only on submission
//		// uuid only on query response
//	}
//
//
//	List<String> validate_XON(String value) {
//		List<String> errs = new ArrayList<String>();
//		String[] parts = value.split("\\^");
//		if (parts.length < 1) {
//			errs.add("No value");
//			return errs;
//		}
//		String xon_1 = parts[0];
//		if (xon_1.length() == 0)
//			errs.add("XON.1 missing");
//
//		String xon_6 = (parts.length < 6) ? "" : parts[5];
//		xon_6 = xon_6.replaceAll("\\&amp;", "&");
//		String xon_10 = (parts.length < 10) ? "" : parts[9];
//		String[] xon_6_parts = xon_6.split("\\&");
//		String xon_6_2 = (xon_6_parts.length < 2) ? "" : xon_6_parts[1];
//		String xon_6_3 = (xon_6_parts.length < 3) ? "" : xon_6_parts[2];
//
//		if (xon_10.length() > 0 && !is_oid(xon_10, true)) {
//			if (xon_6_2.length() == 0)
//				errs.add("XON.10 is valued and not an OID so XON.6.2 is required");
//			else if (!is_oid(xon_6_2,true))
//				errs.add("XON.6.2 must be an OID");
//
//			if (!xon_6_3.equals("ISO"))
//				errs.add("XON.10 is valued and not an OID so XON.6.3 is required to have the value ISO");
//		}
//
//		for (int i=1; i<=10; i++) {
//			if (i == 1 || i == 6 || i == 10)
//				continue;
//			if (parts.length < i)
//				continue;
//			if (parts[i-1].length() > 0)
//				errs.add("Only XON.1, XON.6, XON.10 are allowed to have values: found value in XON." + i);
//		}
//
//		return errs;
//	}
//
//	void validate_author_institution(OMElement ai_slot) {
//		OMElement value_list_ele = MetadataSupport.firstChildWithLocalName(ai_slot, "ValueList");
//		if (value_list_ele == null) {
//			err("authorInstitution Slot has no ValueList");
//			return;
//		}
//		List<OMElement> values = MetadataSupport.childrenWithLocalName(value_list_ele, "Value");
//		for (OMElement value_ele : values) {
//			String value = value_ele.getText();
//			List<String> errs = validate_XON(value);
//			for (String err : errs) {
//				err("authorInstituion: " + err);
//			}
//		}
//	}
//
//
//	void validate_author_structure()  {
//		try {
//			List<OMElement> classs = m.getClassifications();
//
//			for (int i=0; i<classs.size(); i++) {
//				OMElement class_ele = (OMElement) classs.get(i);
//				String class_scheme = class_ele.getAttributeValue(MetadataSupport.classificationscheme_qname);
//				String classified_object_id = class_ele.getAttributeValue(MetadataSupport.classified_object_qname);
//				OMElement classified_object_ele = m.getObjectById(classified_object_id);
//				String classified_object_type = (classified_object_ele == null) ? "Unknown type" : classified_object_ele.getLocalName();
//				String nodeRepresentation = class_ele.getAttributeValue(MetadataSupport.noderepresentation_qname);
//
//				if (	class_scheme != null &&
//						(class_scheme.equals(MetadataSupport.XDSDocumentEntry_author_uuid)  ||
//								class_scheme.equals(MetadataSupport.XDSSubmissionSet_author_uuid))
//				) {
//					// doc.author or ss.author
//
//					if (nodeRepresentation == null || (nodeRepresentation != null && !nodeRepresentation.equals("")))
//						err(classified_object_type + " " + classified_object_id + " has a author type classification (classificationScheme=" +
//								class_scheme + ") with no nodeRepresentation attribute.  It is required and must be the empty string."	);
//
//
//					String author_person = m.getSlotValue(class_ele, "authorPerson", 0);
//					if (author_person == null)
//						err(classified_object_type + " " + classified_object_id + " has a author type classification (classificationScheme=" +
//								class_scheme + ") with no authorPerson slot.  One is required."	);
//					//				if ( ! is_xcn_format(author_person))
//					//					err(classified_object_type + " " + classified_object_id + " has a author type classification (classificationScheme=" +
//					//							class_scheme + ") with authorPerson slot that is not in XCN format. The value found was " + author_person	);
//
//					if (m.getSlotValue(class_ele, "authorPerson", 1) != null) 
//						err(classified_object_type + " " + classified_object_id + " has a author type classification (classificationScheme=" +
//								class_scheme + ") with multiple values in the authorPerson slot.  Only one is allowed. To document a second author, create a second Classification object"	);
//
//					for (OMElement slot : MetadataSupport.childrenWithLocalName(class_ele, "Slot")) {
//						String slot_name = slot.getAttributeValue(MetadataSupport.slot_name_qname);
//						if ( 	slot_name != null &&
//								(slot_name.equals("authorPerson") ||
//										slot_name.equals("authorRole") ||
//										slot_name.equals("authorSpecialty") )){
//						} else if (slot_name.equals("authorInstitution")) {
//							validate_author_institution(slot);
//						} else {
//							err(classified_object_type + " " + classified_object_id + " has a author type classification (classificationScheme=" +
//									class_scheme + ") with an unknown type of slot with name " + slot_name + ".  Only XDS prescribed slots are allowed inside this classification"	);
//
//						}
//					}
//				}
//			}
//		} catch (MetadataException e) {
//			err(e);
//		}
//	}
//
//	boolean is_xcn_format(String value) {
//		int count = 0;
//		for (int i=0; i<value.length(); i++) {
//			if (value.charAt(i) == '^') count++;
//		}
//		return (count == 5);
//	}
//
//
//	// should base validation on segmentation done by Metadata.class
//	// if segmented as SS then should be classified as same ... 
//	void validate_package_class()  {
//		List<String> rp_ids = m.getRegistryPackageIds();
//
//		for (int i=0; i<rp_ids.size(); i++) {
//			String id = (String) rp_ids.get(i);
//
//			int ss_class_count = 0;
//			int fol_class_count = 0;
//
//			List<OMElement> ext_classs = m.getClassifications();
//			for (int c=0; c<ext_classs.size(); c++) {
//				OMElement class_ele = ext_classs.get(c);
//				String classified_id = class_ele.getAttributeValue(MetadataSupport.classified_object_qname);
//				if ( classified_id == null || !id.equals(classified_id))
//					continue;
//				String classification_node = class_ele.getAttributeValue(MetadataSupport.classificationnode_qname);
//				if (classification_node != null && classification_node.equals(MetadataSupport.XDSSubmissionSet_classification_uuid)) {
//					//ss
//					ss_class_count++;
//				} else if (classification_node != null && classification_node.equals(MetadataSupport.XDSFolder_classification_uuid)) {
//					// fol
//					fol_class_count++;
//				}
//			}
//
//			if (ss_class_count + fol_class_count == 0)
//				err("RegistryPackage" + " " + id + " : is not Classified as either a Submission Set or Folder: " +
//						"Submission Set must have classification urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd " +
//				"and Folder must have classification urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2");
//			if (ss_class_count + fol_class_count > 1)
//				err("RegistryPackage" + " " + id + " : is Classified multiple times: " +
//						"Submission Set must have single classification urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd " +
//				"and Folder must have single classification urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2");
//		}
//	}







}
