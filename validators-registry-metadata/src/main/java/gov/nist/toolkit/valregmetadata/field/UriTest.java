package gov.nist.toolkit.valregmetadata.field;


import junit.framework.TestCase;


public class UriTest extends TestCase {
//	RegistryErrorList rel;
//	Metadata m;
//	OMElement ele;
//
//	public void setUp() {
//		try {
//			rel = new RegistryErrorList(RegistryErrorList.version_2, false);
//			rel.setVerbose(false);
//		} catch (XdsInternalException e) {
//			fail("Cannot build RegistryErrorList: " + ExceptionUtil.exception_details(e));
//		}
//
//		if (System.getenv("XDS_DEV") == null || System.getenv("XDS_DEV").equals(""))
//			fail("Env Var XDS_DEV not set");
//
//		try {
//			ele = Parse.parse_xml_file(System.getenv("XDS_DEV") + "/testkit/tests/11733/submit/single_doc.xml");
//		} catch (XMLParserException e) {
//			fail("Cannot parse metadata file" + ExceptionUtil.exception_details(e));
//		} catch (FactoryConfigurationError e) {
//			fail("Cannot parse metadata file - factory configuration error");
//		}
//
//		try {
//			m = new Metadata(ele);
//		} catch (MetadataException e) {
//			fail("Metadata constructor failed" + ExceptionUtil.exception_details(e));
//		}
//	}
//
//	boolean validate()  {
//		Validator val;
//		try {
//			m.reParse();
//			val = new Validator(m, rel, true, false, null, false);
//			val.run();
//		} catch (LoggerException e) {
//			fail("Exception: " + ExceptionUtil.exception_details(e));
//		} catch (XdsException e) {
//			fail("Exception: " + ExceptionUtil.exception_details(e));
//		}
//
//		// remove some errors that we don't worry about here
//
//		rel.delError("requires an OID format value");
//		rel.delError("is not formatted as an OID");
//		return !rel.has_errors();
//	}
//
//	public void testRel() {
//		rel.addError("Myerr", "a code", "location");
//		assertTrue(rel.getStatus().equals("Failure"));
//		assertFalse(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//		assertTrue(rel.has_errors());
//	}
//
//	public void testBasicMetadata() throws LoggerException, XdsException {
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testnoURI()  {
//		rmURI();
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().indexOf("required slot URI") != -1);
//	}
//
//	private void rmURI() {
//		try {
//			m.rmSlot(m.getExtrinsicObjectIds().getRetrievedDocumentsModel(0), "URI");
//		} catch (MetadataException e) {
//			fail("Exception: " + ExceptionUtil.exception_details(e));
//		}
//	}
//
//	public void testURI1() {
//		rmURI();
//		m.addSlot(m.getExtrinsicObject(0), "URI", "1|http://");
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testURI2() {
//		rmURI();
//		m.addSlot(m.getExtrinsicObject(0), "URI", "2|http://");
//		validate();
//	}
//
//	public void testURI3() {
//		rmURI();
//		m.addSlot(m.getExtrinsicObject(0), "URI", "1|ttp://");
//		validate();
//		assertFalse(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testURI4() {
//		rmURI();
//		m.addSlot(m.getExtrinsicObject(0), "URI", "ttp://");
//		validate();
//		assertFalse(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testURI5() {
//		rmURI();
//		OMElement uri = m.addSlot(m.getExtrinsicObject(0), "URI");
//		m.addSlotValue(uri, "1|http://");
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testURI6() {
//		rmURI();
//		OMElement uri = m.addSlot(m.getExtrinsicObject(0), "URI");
//		m.addSlotValue(uri, "2|://");
//		m.addSlotValue(uri, "1|http");
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}
//
//	public void testURI7() {
//		String uriStr = "http://blah.net/kajdfaf9elkvkjalkajdflkjaksdjfalsdkfjlkjalsdjf";
//		m.setUriChunkSize(10);
//		m.setURIAttribute(m.getExtrinsicObject(0), uriStr);
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//		String uriStr2 = null;
//		try {
//			uriStr2 = m.getURIAttribute(m.getExtrinsicObject(0));
//		} catch (MetadataException e) {
//			fail(ExceptionUtil.exception_details(e));
//		}
//		assertTrue(uriStr.equals(uriStr2));
//	}
//
//	public void testURI8() {
//		String uriStr = "";
//		m.setUriChunkSize(10);
//		m.setURIAttribute(m.getExtrinsicObject(0), uriStr);
//		String uriStr2 = null;
//		try {
//			uriStr2 = m.getURIAttribute(m.getExtrinsicObject(0));
//			fail("Empty URI should have been rejected");
//		} catch (MetadataException e) {
//		}
//	}
//	
//	public void testURI9() {
//		String uriStr = "http://blah.net/kajdfaf9elkvkjalkajdflkjaksdjfalsdkfjlkjalsdjf";
//		m.setUriChunkSize(10);
//		m.setURIAttribute(m.getExtrinsicObject(0), uriStr);
//		
//		OMElement slot = null;
//		try {
//			slot = m.getSlot(m.getExtrinsicObjectIds().getRetrievedDocumentsModel(0), "URI");
//		} catch (MetadataException e) {
//			fail(ExceptionUtil.exception_details(e));
//		}
//		
//		m.addSlotValue(slot, "xxx");
//		validate();
//		assertTrue(rel.getErrorsAndWarnings(), rel.getErrorsAndWarnings().equals(""));
//	}

}
