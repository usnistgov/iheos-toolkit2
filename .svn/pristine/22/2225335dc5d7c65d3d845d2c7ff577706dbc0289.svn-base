package gov.nist.toolkit.utilities.xml;

import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

public class OMFormatter {
	OMElement ele;
	StringBuffer buf;

	String indentForElement;
	String indentForAttribute;
	String nl;
	String space;
	String lt;
	boolean multipleAttsOnALine = false;
	int indentation;
	int lineLengthLimit = 50;
	boolean recurse = true;
	boolean leadingNl = true;

	public OMFormatter(OMElement ele) {
		this.ele = ele;
	}

	public OMFormatter(String xml) throws XdsInternalException, FactoryConfigurationError {
		ele = Util.parse_xml(xml);
	}

	public OMFormatter(File file) throws XdsInternalException, FactoryConfigurationError {
		ele = Util.parse_xml(file);
	}

	public void noRecurse() {
		recurse = false;
	}

	public OMFormatter noLeadingNl() {
		leadingNl = false;
		return this;
	}

	public String toString() {
		buf = new StringBuffer();
		indentForElement="   ";
		indentForAttribute="     ";
		nl="\n";
		lt = "<";
		space = " ";
		indentation = -1;

		//buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if (ele != null)
			element(ele, true);
		return buf.toString();
	}

	public String toHtml() {
		buf = new StringBuffer();
		indentForElement="&nbsp;&nbsp;&nbsp;";
		indentForAttribute="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		space = " ";
		lt = "&lt;";
		nl= "<br />";
		indentation = -1;
		if (ele != null)
			element(ele, true);
		return buf.toString();
	}


	void element(OMElement ele, boolean isTop) {
		OMNamespace myNamespace = ele.getNamespace();
		String myNamespaceUri = (myNamespace != null) ? myNamespace.getNamespaceURI() : null;
		OMNamespace defaultNamespace = ele.getDefaultNamespace();
		String defaultNamespaceUri = (defaultNamespace != null) ? defaultNamespace.getNamespaceURI() : null;
		String prefix;

		OMNamespace parentsNamespace = null;
		String parentsNamespaceUri = null;
		String parentsDefaultNamespaceUri = null;

		OMElement parent = null;
		try {
			parent = (OMElement) ele.getParent();
		} catch (Exception e) {}
		
		try {
			parentsNamespace = parent.getNamespace();
			parentsNamespaceUri = (parentsNamespace != null) ? parentsNamespace.getNamespaceURI() : null;
		} catch (Exception e) {}
		
		try {
			OMNamespace parentsDefaultNamespace = parent.getDefaultNamespace();
			parentsDefaultNamespaceUri = parentsDefaultNamespace.getNamespaceURI();
		} catch (Exception e) {}
		
		boolean addDefaultNSDef = false;
		if (defaultNamespaceUri != null) {
			if (!defaultNamespaceUri.equals(parentsDefaultNamespaceUri)) {
				addDefaultNSDef = true;
			}
		}
		
		if (myNamespaceUri == null || myNamespaceUri.equals(defaultNamespaceUri))
			prefix = null;
		else
			prefix = myNamespace.getPrefix();

		boolean addMyNamespace = false;

		if (myNamespaceUri != null && !myNamespaceUri.equals(defaultNamespaceUri) && (isTop || !myNamespaceUri.equals(parentsNamespaceUri) || 
				(prefix != null && !prefix.equals(parentsNamespace.getPrefix()))))
			addMyNamespace = true;

		if (!leadingNl) {
			leadingNl = true;
		} else {
			buf.append(nl);
		}
		indentation++;
		indentElement();
		String name = ele.getLocalName();
		if (prefix != null)
			name = prefix + ":" + name;
		buf.append(lt).append(name);
		attributes(ele);

		if (addDefaultNSDef) {
			addIndentation();
			buf.append(space).append("xmlns")
			.append("=\"").append(defaultNamespace.getNamespaceURI()).append("\"");
		}

		if (addMyNamespace && !"xml".equals(myNamespace.getPrefix())) {
			addIndentation();
			buf.append(space).append("xmlns:").append(myNamespace.getPrefix())
			.append("=\"").append(myNamespace.getNamespaceURI()).append("\"");
		}

		if ( !recurse || (!hasChildElements(ele) && !hasText(ele))) {
			buf.append("/>");
			indentation--;
			return;
		}
		buf.append(">");
		if (hasChildElements(ele)) {
			Iterator<OMElement> children = ele.getChildElements();
			while(children.hasNext()) {
				OMElement child = children.next();
				element(child, false);
			}
		} else {
			String c = ele.getText();
			c = encodeAmp(c);
			buf.append(c);
		}

		if (hasChildElements(ele) && !hasText(ele)) {
			buf.append(nl);
			indentElement();
		}
		buf.append(lt + "/" + name + ">");

		indentation--;
	}

	OMNamespace attNamespace; // used for communication between getAttributeName() and attributes()
	OMNamespace xsiAttNameSpace;

	String getAttributeName(OMAttribute a) {
		attNamespace = a.getNamespace();
		String prefix = "";
		if (attNamespace != null) {
			prefix = attNamespace.getPrefix();
			if (prefix != null && !prefix.equals(""))
				prefix = prefix + ":";
			//			if (nsPrefix != null && nsPrefix.equals("xml")) {
			//				prefix = "xml:";
			//				attNamespace = null;  // so namespace does not get added to element
			//			}
		}
		String attName = a.getLocalName();

		return prefix + attName;
	}

	@SuppressWarnings("unchecked")
	void attributes(OMElement ele) {
		NamespaceManager nsman = null;

		Iterator<OMAttribute> it = ele.getAllAttributes();
		while (it.hasNext()) {
			addIndentation();
			OMAttribute a = it.next();
			//nsman = null;
			attNamespace = null;

			buf.append(space)
			.append(getAttributeName(a))
			.append("=")
			.append('"')
			.append(encodeQuote(encodeAmp(a.getAttributeValue())))
			.append('"');

			//vbeera: added below code to fix the 'Duplicate namespace' exception
			String attrName = getAttributeName(a) == null ? "" :getAttributeName(a);		
			String  attrParentLocalName = ele.getLocalName() == null ? "" : ele.getLocalName(); 

			if(!attrName.equals("") && attrName.trim().equalsIgnoreCase("wsu:Id") && 
					(!attrParentLocalName.equals("") && attrParentLocalName.trim().equalsIgnoreCase("Timestamp")))
			{
				System.out.println("************* Skipped adding Namespace for '"+attrName+"' under the element 'wsu:"+attrParentLocalName+"' *******");
				//vbeera: added to skip the duplication of wsu namespace.
			}else if(!attrName.equals("") && attrName.trim().equalsIgnoreCase("ITSVersion")){
				if (attNamespace != null) {
					if (nsman == null)
						nsman = new NamespaceManager();
					nsman.add(attNamespace);
				}else{
					if( xsiAttNameSpace!=null){
						if (nsman == null)
							nsman = new NamespaceManager();
						nsman.add(xsiAttNameSpace);
					}
				}
				/////////////discuss with BILL
			}else if(!attrName.equals("") && attrName.trim().equalsIgnoreCase("xsi:schemaLocation")){
				xsiAttNameSpace = attNamespace;
				//vbeera: added below code for undeclared 'xsi' prefix namespace exception
				if (attNamespace != null) {
					if (nsman == null)
						nsman = new NamespaceManager();
					nsman.add(attNamespace);
				}
				//vbeera code end
			}
			else
			{
				if (attNamespace != null) {
					if (nsman == null)
						nsman = new NamespaceManager();
					nsman.add(attNamespace);
				}
				//vbeera: commented the below namespace append code and moved it out of the loop
				//if (nsman != null)
					//buf.append(nsman.toString());
			}
		}
		if (nsman != null)
			buf.append(nsman.toString());
	}

	private void addIndentation() {
		if (currentLineLength() > lineLengthLimit) {
			buf.append(nl);
			indentAttribute();
		}
	}

	boolean hasChildElements(OMElement ele) { 
		return ele.getFirstElement() != null;
	}

	boolean hasText(OMElement ele) {
		String text = ele.getText();
		return text != null && !text.trim().equals("");
	}

	int currentLineLength() {
		return buf.length() - buf.lastIndexOf(nl);
	}

	void indentElement() {
		for (int i=0; i<indentation; i++) buf.append(indentForElement);
	}

	void indentAttribute() {
		indentElement();
		buf.append(indentForAttribute);
	}

	public static String encodeQuote(String in) {
		if (in == null) return in;
		return in.replaceAll("\"", "&quot;");
	}

	public static String encodeAmp(String in) {
		if (in == null) return in;
		if (in.indexOf('&') == -1 && in.indexOf('<') == -1) 
			return in;
		StringBuffer buf = new StringBuffer();
		buf.append(in.replaceAll("<", "&lt;"));
		try {
			for (int i=0; i<buf.length(); i++) {
				if (buf.charAt(i) != '&')
					continue;
				boolean isescape = false;
				try {
					if (buf.substring(i, i+"&amp;".length()).equals("&amp;"))
						isescape = true;
				} catch (Exception e) {}
				try {
					if (buf.substring(i, i+"&lt;".length()).equals("&lt;"))
						isescape = true;
				} catch (Exception e) {}
				if (!isescape) {
					buf.deleteCharAt(i);
					buf.insert(i, "&amp;");
				}
			}
		} catch (Exception e) {}
		return buf.toString();
	}



	static public void main(String[] args) {
		OMElement ele;
		try {
			ele = Util.parse_xml(new File(args[0]));
			System.out.println(new OMFormatter(ele).toString());
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
		} 
	}
}
