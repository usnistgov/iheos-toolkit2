package gov.nist.toolkit.valregmetadata.field;


public class AttributeTest extends TestSupport {
//	protected Attribute mv;
//
//	protected void compile_a() {
//		try {
//			System.out.println(root.toString());
//			m = MetadataParser.parseNonSubmission(root);
//			mv = new Attribute(m, true /* is submission */, false /* xds_b */, true);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getClass().getName() + ": " + e.getMessage());
//		}
//	}
//
//	protected void compile_b() {
//		try {
//			System.out.println(root.toString());
//			m = MetadataParser.parseNonSubmission(root);
//			mv = new Attribute(m, true /* is submission */, true /* xds_b */, true);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getClass().getName() + ": " + e.getMessage());
//		}
//	}
//
//
//	
//	public void test_doc_slot_ok() throws MetadataException {
//		OMElement eo = this.add_object("ExtrinsicObject");
//		this.add_slot("hash", "23", eo);
//		compile_a();
//		mv.validate_slots_are_legal();
//		assertFalse(mv.rel.has_errors());
//	}
//
//	public void test_doc_URI_slot_required_a() throws MetadataException {
//		OMElement doc = this.add_object("ExtrinsicObject", "eo");
//		this.add_slot("URI", "http://", doc);
//		this.add_slot("hash", "87i", doc);
//		this.add_slot("languageCode", "us-en", doc);
//		this.add_slot("size", "87", doc);
//		this.add_slot("sourcePatientInfo", "bill", doc);
//		this.add_slot("creationTime", "2000", doc);
//		compile_a();
//		mv.validate_required_slots_present();
//		assertFalse(mv.rel.getErrorsAndWarnings(), mv.rel.has_errors());
//	}
//
//	public void test_doc_URI_slot_required_b() throws MetadataException {
//		OMElement doc = this.add_object("ExtrinsicObject", "eo");
//		this.add_slot("URI", "http://", doc);
//		this.add_slot("hash", "87i", doc);
//		this.add_slot("languageCode", "us-en", doc);
//		this.add_slot("size", "87", doc);
//		this.add_slot("sourcePatientInfo", "bill", doc);
//		this.add_slot("creationTime", "2000", doc);
//		compile_b();
//		mv.validate_required_slots_present();
//		assertFalse(mv.rel.getErrorsAndWarnings(), mv.rel.has_errors());
//	}
//
//	public void test_doc_slot_bad() throws MetadataException {
//		OMElement eo = this.add_object("ExtrinsicObject", "eo");
//		this.add_slot("fubar", "23", eo);
//		compile_a();
//		mv.validate_slots_are_legal();
//		assertTrue(mv.rel.has_errors());
//	}
//
//	public void test_doc_slot_urn() throws MetadataException {
//		OMElement eo = this.add_object("ExtrinsicObject");
//		this.add_slot("urn:xxx", "23", eo);
//		compile_a();
//		mv.validate_slots_are_legal();
//		assertFalse(mv.rel.has_errors());
//	}
//	
//	public void test_ss_slot_ok() throws MetadataException {
//		OMElement ss = this.add_ss("ss");
//		this.add_slot("submissionTime", "23", ss);
//		compile_a();
//		mv.slots_are_legal();
//		assertFalse(mv.rel.has_errors());
//	}
//
//	public void test_ss_slot_bad() throws MetadataException {
//		OMElement ss = this.add_ss("ss");
//		this.add_slot("playTime", "23", ss);
//		compile_a();
//		mv.slots_are_legal();
//		assertTrue(mv.rel.has_errors());
//	}
//
//	public void test_fol_slot_ok() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		this.add_slot("lastUpdateTime", "23", fol);
//		compile_a();
//		mv.validate_fol_slots_are_legal();
//		assertFalse(mv.rel.has_errors());
//	}
//
//	public void test_fol_slot_bad() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		this.add_slot("lastUpdateTimexxxxx", "23", fol);
//		compile_a();
//		mv.validate_fol_slots_are_legal();
//		assertTrue(mv.rel.has_errors());
//	}
//	
//	public void test_required_doc_slots() throws MetadataException {
//		OMElement doc = this.add_object("ExtrinsicObject", "eo");
//		this.add_slot("creationTime", "7", doc);
//		this.add_slot("hash", "x", doc);
//		this.add_slot("languageCode", "x", doc);
//		this.add_slot("size", "7", doc);
//		this.add_slot("sourcePatientInfo", "x", "y", doc);
//		this.add_slot("URI", "x", doc);
//		compile_a();
//		mv.validate_required_slots_present();
//		assertFalse(mv.rel.has_errors());
//	}
//
//	public void test_required_doc_slots_bad() throws MetadataException {
//		OMElement doc = this.add_object("ExtrinsicObject", "eo");
//		//this.add_slot("creationTime", "7", doc);
//		this.add_slot("hash", "x", doc);
//		this.add_slot("languageCode", "x", doc);
//		this.add_slot("size", "7", doc);
//		this.add_slot("sourcePatientInfo", "x", "y", doc);
//		this.add_slot("URI", "x", doc);
//		compile_a();
//		mv.validate_required_slots_present();
//		assertTrue(mv.rel.has_errors());
//	}
//	
//	public void test_fol_ids() throws MetadataException {
//		OMElement fol = this.add_object("RegistryPackage", "fol");
//		OMElement pat_id_ele = this.add_ext_id("urn:uuid:f64ffdf0-4b97-4e06-b79f-a52b38ec2f8a", "pid", fol);
//		this.add_name("XDSFolder.patientId", pat_id_ele);
//		OMElement uni_id_ele = this.add_ext_id("urn:uuid:75df8f67-9973-4fbe-a900-df66cefecc5a", "45.3.2", fol);
//		this.add_name("XDSFolder.uniqueId", uni_id_ele);
//		compile_a();
//		mv.validate_folder_extids();
//		assertFalse(mv.rel.getErrorsAndWarnings(), mv.rel.has_errors());
//	}
//	
//	public void test_ss_class_ok() throws MetadataException {
//		OMElement ss = this.add_ss("ss");
//		OMElement class_ele = this.add_class("urn:uuid:aa543740-bdda-424e-8c96-df4873be8500", "ss", ss);
//		this.add_name("myclass", class_ele);
//		this.add_slot("codingScheme", "my scheme", class_ele);
//		OMElement class_ele2 = this.add_class("urn:uuid:aa543740-bdda-424e-8c96-df4873be8500", "ss", ss);
//		this.add_name("myclass2", class_ele);
//		this.add_slot("codingScheme", "my scheme2", class_ele2);
//		compile_a();
//		mv.validate_class();
//		assertTrue(mv.rel.has_errors());
//	}
//	
//	public void test_ss_classification() throws MetadataException {
//		OMElement ss = this.add_ss("ss");
//		this.add_main_class("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd", "ss", ss);
//		compile_a();
//		mv.validate_package_class();
//		assertFalse(mv.rel.getErrorsAndWarnings(), mv.rel.has_errors());
//	}
//
//	public void test_fol_classification() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		this.add_main_class("urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2", "fol", fol);
//		compile_a();
//		System.out.println("I Classifications:" + m.getClassifications("fol").toString());
//		System.out.println("O Classifications:" + m.getClassifications().toString());
//		mv.validate_package_class();
//		System.out.println("BIG ERROR" +  root.toString());
//		assertFalse( mv.rel.has_errors());
//	}
//
//	public void test_no_classification() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		compile_a();
//		mv.validate_package_class();
//		assertTrue(mv.rel.has_errors());
//	}
//
//	public void test_mult_classification() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		this.add_class("urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2", "fol", fol);
//		this.add_class("urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd", "fol", fol);
//		compile_a();
//		mv.validate_package_class();
//		assertTrue(mv.rel.has_errors());
//	}
//
//	public void test_external_classification() throws MetadataException {
//		OMElement fol = this.add_fol("fol");
//		OMElement class_ele = this.add_main_class("urn:uuid:d9d542f3-6cc4-48b6-8870-ea235fbc94c2", "fol", wrapper);
//		this.add_att("classifiedObject", "fol", class_ele);
//		compile_a();
//		mv.validate_package_class();
//		assertFalse(mv.rel.getErrorsAndWarnings(), mv.rel.has_errors());
//	}

}
