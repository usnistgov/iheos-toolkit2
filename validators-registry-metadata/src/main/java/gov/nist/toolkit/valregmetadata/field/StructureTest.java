package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;

import org.apache.axiom.om.OMElement;

public class StructureTest extends TestSupport {
	protected Structure mv;

	protected void compile() {
		try {
			m = MetadataParser.parseNonSubmission(root);
			mv = new Structure(m, true /* is submission */);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public void test_iheAssocList() {
		assertTrue(Metadata.iheAssocTypes.contains("RPLC"));
	}
	

	public void test_is_v3() {
		compile();
		assertFalse(m.isVersion2());
	}

	public void test_empty() {
		compile();
	}

	public void test_single_object() {
		add_object("ExtrinsicObject");
		add_object("RegistryPackage");
		compile();
		assertTrue(m.getExtrinsicObjects().size() == 1);
		assertTrue(m.getRegistryPackages().size() == 1);
	}
	
	public void test_doc_not_in_ss() {
		add_object("ExtrinsicObject");
		compile();
		mv.docs_in_ss();
		assertTrue(mv.rel.has_errors());
	}

	public void test_doc_in_ss() {
		add_ss("ss");
		add_object("ExtrinsicObject", "eo");
		add_assoc("ss", "HasMember", "eo");
		compile();
		System.out.println(m.getRoot().toString());
		mv.docs_in_ss();
		mv.doc_implies_ss();
		assertFalse(mv.rel.has_errors());
	}

	public void test_doc_no_ss() {
		add_object("ExtrinsicObject");
		compile();
		mv.doc_implies_ss();
		assertTrue(mv.rel.has_errors());
	}

	public void test_fol_no_ss() {
		add_fol("fol");
		compile();
		mv.fol_implies_ss();
		assertTrue(mv.rel.has_errors());
	}
	
	public void test_ss_no_doc() {
		add_ss("ss");
		compile();
		mv.ss_implies_doc_or_fol_or_assoc();
		assertTrue(mv.rel.has_errors());
	}
	
	public void test_ss_assoc_has_no_ss_status() {
		add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		add_assoc("ss", "HasMember", "doc");
		compile();
		mv.ss_status_relates_to_ss();
		assertTrue(mv.rel.has_errors());
	}

	public void test_fol_assoc_has_no_ss_status() {
		add_object("RegistryPackage");
		add_object("ExtrinsicObject", "doc");
		add_assoc("ss", "HasMember", "doc");
		compile();
		mv.ss_status_relates_to_ss();
		assertFalse(mv.rel.has_errors());
	}

	public void test_ss_assoc_has_ss_status() {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "doc");
		add_slot("SubmissionSetStatus", "Original", assoc);
		compile();
		mv.ss_status_relates_to_ss();
		assertFalse(mv.rel.has_errors());
	}

	public void test_fol_assoc_has_ss_status() {
		add_object("RegistryPackage");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "doc");
		add_slot("SubmissionSetStatus", "Original", assoc);
		compile();
		mv.ss_status_relates_to_ss();
		assertTrue(mv.rel.has_errors());
	}
	
	public void test_doc_has_id() {
		add_object("ExtrinsicObject", "doc");
		compile();
		mv.ss_doc_fol_must_have_ids();
		assertFalse(mv.rel.has_errors());
	}

	// No longer valid since metadata compiler inserts missing ids and technically on the interface there is no problem
//	public void test_doc_has_no_id() {
//		add_object("ExtrinsicObject", "");
//		System.out.println("before compile:\n" + metadataToString());
//		compile();
//		System.out.println("after compile:\n" + metadataToString());
//		mv.ss_doc_fol_must_have_ids();
//		assertTrue(mv.rel.has_errors());
//	}
	
	public void test_by_value_in_submission() throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "doc");
		add_slot("SubmissionSetStatus", "Original", assoc);
		compile();
		mv.by_value_assoc_in_submission();
		assertFalse(mv.rel.has_errors());
	}
	
	public void test_by_reference_in_submission()  throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "doc");
		add_slot("SubmissionSetStatus", "Reference", assoc);
		compile();
		mv.by_value_assoc_in_submission();
		assertTrue(mv.rel.has_errors());
		
	}

	public void test_by_value_out_of_submission() throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "urn:uuid:xxx");  
		add_slot("SubmissionSetStatus", "Original", assoc);
		compile();
		mv.by_value_assoc_in_submission();
		assertTrue(mv.rel.has_errors());
	}
	
	public void test_by_value_in_submission_with_uuid() throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "urn:uuid:xxx");
		OMElement assoc = add_assoc("ss", "HasMember", "urn:uuid:xxx");  // not reliable - could be early uuid assignment
		add_slot("SubmissionSetStatus", "Original", assoc);
		compile();
		mv.by_value_assoc_in_submission();
		assertFalse(mv.rel.has_errors());
	}
	
	public void test_by_reference_out_of_submission()  throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "urn:uuid:xxx");
		add_slot("SubmissionSetStatus", "Reference", assoc);
		compile();
		System.out.println(metadataToString());
		mv.by_value_assoc_in_submission();
		assertFalse(mv.rel.has_errors());
	}

	public void test_ss_status_multiple_values()  throws MetadataException, MetadataValidationException {
		OMElement ss = add_ss("ss");
		add_object("ExtrinsicObject", "doc");
		OMElement assoc = add_assoc("ss", "HasMember", "urn:uuid:xxx");
		add_slot("SubmissionSetStatus", "Reference", "Extra", assoc);
		compile();
		mv.ss_status_single_value();
		assertTrue(mv.rel.has_errors());
		
	}

}
