package gov.nist.toolkit.session.server;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.CodeConfiguration;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

public class CodesConfigurationBuilder {
	static transient QName codeQname = new QName("code");
	static transient QName codingSchemeQname = new QName("codingScheme");
	static transient QName displayQname = new QName("display");
	static transient QName nameQname = new QName("name");
	
	CodesConfiguration csc;
	
	public CodesConfigurationBuilder(File codeFile) throws XdsInternalException, FactoryConfigurationError {
		csc = new CodesConfiguration();
		
		OMElement ele = Util.parse_xml(codeFile);
		
		Map<String, CodeConfiguration> codes = new HashMap<String, CodeConfiguration>();
		csc.setCodes(codes);
		
		for (OMElement codeType : MetadataSupport.decendentsWithLocalName(ele, "CodeType")) {
			CodeConfiguration cc = new CodeConfiguration();
			cc.name = codeType.getAttributeValue(nameQname);
			cc.codes = new ArrayList<String>();
			
			List<Code> codex = new ArrayList<Code>();
			for (OMElement codeEle : MetadataSupport.childrenWithLocalName(codeType, "Code")) {
				Code c = new Code(codeEle);
				insertAlphabetically(codex, c);
				//cc.codes.add(new Code(codeEle).toString());
			}
			
			for (Code c : codex) {
				cc.codes.add(c.toString());
			}
			

			codes.put(cc.name, cc);
		}
		
		for (OMElement aaEle : MetadataSupport.decendentsWithLocalName(ele, "AssigningAuthority")) {
			String aa;
		}
		
	}
	
	/**
	 * Sort by display name
	 * @param codes
	 * @param newCode
	 */
	void insertAlphabetically(List<Code> codes, Code newCode) {
		int i = 0;
		for (Code c : codes) {
			int x = newCode.display.compareToIgnoreCase(c.display);
			if (x < 0 ) {
				codes.add(i, newCode);
				return;
			}
			i++;
		}
		codes.add(newCode);
	}
	
	public CodesConfiguration get() {
		return csc;
	}

	class Code {
		String code;
		String scheme;
		String display;
		
		public Code(OMElement ele) {
			code = ele.getAttributeValue(codeQname);
			scheme = ele.getAttributeValue(codingSchemeQname);
			display = ele.getAttributeValue(displayQname);
			if (display == null)
				display = "...No DisplayName";
		}
		
		public String toString() {
			return code + "^" +
			display + "^" +
			scheme
			;
		}
	}

}
