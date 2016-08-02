package gov.nist.toolkit.valregmetadata.coding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.junit.Before;
import org.junit.Test;

public class UpdateCodesTest {
	private static final File codesFile = new File("/Users/bmajur/tmp/NA2014/environment/NA2014/codes.xml");
	AllCodes allCodes = null;
	String classificationString = 
	        "<Classification classificationScheme=\"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a\" " +
	                "classifiedObject=\"Document01\" nodeRepresentation=\"DEMO-Procedure\"> " +
	                "<Name> " +
	                    "<LocalizedString value=\"Procedure\"/> " +
	                "</Name> " +
	                "<Slot name=\"codingScheme\"> " +
	                    "<ValueList> " +
	                        "<Value>1.3.6.1.4.1.21367.100.1</Value> " +
	                    "</ValueList> " +
	                "</Slot> " +
	            "</Classification> ";
	
	String classificationsString = 
			"<wrapper> " +
	        "<Classification classificationScheme=\"urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a\" " +
	                "classifiedObject=\"Document01\" nodeRepresentation=\"DEMO-Procedure\"> " +
	                "<Name> " +
	                    "<LocalizedString value=\"Procedure class code\"/> " +
	                "</Name> " +
	                "<Slot name=\"codingScheme\"> " +
	                    "<ValueList> " +
	                        "<Value>MyScheme</Value> " +
	                    "</ValueList> " +
	                "</Slot> " +
	            "</Classification> " +
	                "</wrapper> "
	                ;
	
	@Before
	public void init() {
		allCodes = new CodesFactory().load(new File("/Users/bmajur/tmp/NA2014/environment/NA2014/codes.xml"));
	}

//	@Test
	public void codeFromClassificationTest() throws XdsInternalException, FactoryConfigurationError {
		OMElement rawCode = Util.parse_xml(classificationString);
		CodeUpdater engine = new CodeUpdater();
		Code code = engine.code(rawCode);
		assertTrue("DEMO-Procedure".equals(code.getCode()));
		assertTrue("Procedure".equals(code.getDisplay()));
		assertTrue("1.3.6.1.4.1.21367.100.1".equals(code.getScheme()));
	}
	
//	@Test
	public void updateCodeTest() throws XdsInternalException, FactoryConfigurationError {
		OMElement rawCode = Util.parse_xml(classificationString);
		CodeUpdater engine = new CodeUpdater();
		Code code = new Code("MyCode", "MyScheme", "MyDisplay");
		engine.updateClassification(rawCode, code);
		Code code2 = engine.code(rawCode);
		assertTrue(code.equals(code2));
	}
	
//	@Test 
	public void classificationUuidFindableTest() {
		String uuid = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
		assertTrue(allCodes.isKnownClassification(new Uuid(uuid)));
	}
	 
//	@Test
	public void findBadCodesTest() throws XdsInternalException, FactoryConfigurationError {
		OMElement rawCode = Util.parse_xml(classificationsString);
		CodeUpdater engine = new CodeUpdater();
		engine.init(codesFile);
		List<OMElement> badCodes = engine.nonConformingCodes(rawCode);
		assertEquals(1, badCodes.size());
	}
	
//	@Test
	public void metadataFileMapTest() {
		File testDataDir = new File("/Users/bmajur/workspace/toolkit/testkit/tests/11990/submit");
		CodeUpdater engine = new CodeUpdater();
		Map<String, File> metadataFileMap = engine.metadataFileMap(testDataDir);
		assertEquals(1, metadataFileMap.size());
	}
	
	@Test
	public void runTest() throws XdsInternalException, IOException, FactoryConfigurationError {
		File testDataDir = new File("/Users/bmajur/workspace/toolkit/xdstools2/war/toolkitx/testkit");
		CodeUpdater engine = new CodeUpdater();
		try {
		engine.walkTestData(testDataDir, codesFile);
		} catch (RuntimeException e) {
			System.out.println(e);
		}
	}
}
