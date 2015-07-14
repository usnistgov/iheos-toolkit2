package gov.nist.toolkit.valregmetadata.coding;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class CodesParser {
	AllCodes allCodes = new AllCodes();
	static final QName classSchemeQname = new QName("classScheme");
	static final QName codeQname = new QName("code");
	static final QName displayQname = new QName("display");
	static final QName codingSchemeQname = new QName("codingScheme");
	
	public AllCodes parse(OMElement rawCodes) {
		
		Iterator codeTypesIterator = rawCodes.getChildrenWithLocalName("CodeType");
		while (codeTypesIterator.hasNext()) {
			Object obj = codeTypesIterator.next();
			if (!(obj instanceof OMElement)) continue;
			OMElement codeType = (OMElement) obj;
			String classScheme = codeType.getAttributeValue(classSchemeQname);
			if (classScheme == null || classScheme.equals("")) continue;
			
			Codes codes = new Codes(new Uuid(classScheme));
			allCodes.add(codes);
			parseCodes(codeType, codes);
		}
		return allCodes;
	}
	
	private void parseCodes(OMElement rawCodeType, Codes codes) {
		Iterator codeIterator = rawCodeType.getChildrenWithLocalName("Code");
		while (codeIterator.hasNext()) {
			Object obj = codeIterator.next();
			if (!(obj instanceof OMElement)) continue;
			OMElement rawCode = (OMElement) obj;
			String codeString = rawCode.getAttributeValue(codeQname);
			String displayString = rawCode.getAttributeValue(displayQname);
			String codingSchemeString = rawCode.getAttributeValue(codingSchemeQname);
			
			Code code = new Code(codeString, codingSchemeString, displayString);
			codes.add(code);		
		}
	}
	
	public AllCodes getAllCodes() { return allCodes; }
}
