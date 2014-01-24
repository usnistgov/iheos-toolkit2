package gov.nist.toolkit.valregmetadata.coding;

import gov.nist.toolkit.utilities.xml.Util;

import java.io.File;

import org.apache.axiom.om.OMElement;

public class CodesFactory {

	public AllCodes load(File codesFile)  {
		try {
			OMElement rawCodes = Util.parse_xml(codesFile);
			return new CodesParser().parse(rawCodes);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load codes file <" + codesFile + ">.");
		}
	}

}
