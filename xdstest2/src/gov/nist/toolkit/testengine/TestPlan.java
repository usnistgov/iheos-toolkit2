package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;

public class TestPlan {
	OMElement testplanEle;
	Map<String, TestStep> steps;
	
	public TestPlan(OMElement testplanEle) throws Exception {
		this.testplanEle = testplanEle;
		parse();
	}
	
	void parse() throws Exception {
		steps = new HashMap<String, TestStep>();
		
		for (OMElement ele : MetadataSupport.childrenWithLocalName(testplanEle, "TestStep")) {
			String name = ele.getAttributeValue(MetadataSupport.id_qname);
			if (name == null)
				throw new Exception("Testplan has TestStep without id attribute");
			steps.put(name, new TestStep(ele));
		}
	}
}
