package gov.nist.toolkit.saml.util;

import org.w3c.dom.Document;

import java.util.Hashtable;

/**
 * A Utility Class to parse codes.xml using DOM parser.
 * 
 * @author WenYu Lin
 */
public class CodesUtil {
	
	private static Document dom;
	private static Hashtable<String, CodeTypeBean> codeTypeList = null; // <codeType->name, CodeType>
	// private static Hashtable<String, CodeBean> codeList;		 // <code->name, Code>
//	private ValidationContext vc;
	
//	public CodesUtil(ValidationContext vc) {
//		this.vc = vc;
//	}
//
//	public void parseXmlFile(){
//		//get the factory
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//
//		try {
//
//			//Using factory get an instance of document builder
//			DocumentBuilder db = dbf.newDocumentBuilder();
//			//parse using builder to get DOM representation of the XML file
//			System.out.println(vc.getCodesFilename());
//			dom = db.parse(vc.getCodesFilename());
//			parseDocument();
//
//		} catch(ParserConfigurationException pce) {
//			pce.printStackTrace();
//		} catch(SAXException se) {
//			se.printStackTrace();
//		} catch(IOException ioe) {
//			ioe.printStackTrace();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}

	
//	public void parseDocument(){
//		//get the root elememt
//		Element docEle = dom.getDocumentElement();
//
//		//get a nodelist of <codes> elements
//		// get codeType
//		NodeList nodelist = docEle.getElementsByTagName("CodeType");
//		// nl.getLength();
//		// NodeList nl = docEle.getChildNodes();
//		if (nodelist != null && nodelist.getLength() > 0) {
//			codeTypeList = new Hashtable<String, CodeTypeBean>();
//			for(int i = 0 ; i < nodelist.getLength(); i++) {
//				Element e = (Element) nodelist.item(i);
//				CodeTypeBean bean = new CodeTypeBean();
//				System.out.println("CodeType Name: " + e.getAttribute("name"));
//				bean.setName(e.getAttribute("name"));
//				System.out.println("CodeType classScheme: " + e.getAttribute("classScheme"));
//				bean.setClassScheme(e.getAttribute("classScheme"));
//				bean.setCode(getCode(e));
//				codeTypeList.put(bean.getName(), bean);
//			}
//		}
//
//	}
/*	
	private static CodeTypeBean getCodeType(Element element) {
		CodeTypeBean bean = new CodeTypeBean();
		
		NodeList nodelist = element.getElementsByTagName("CodeType");
		
		if (nodelist != null && nodelist.getLength() > 0) {
			for(int i = 0 ; i < nodelist.getLength(); i++) {
				Element e = (Element) nodelist.item(i);
				System.out.println("CodeType Name: " + e.getAttribute("name"));
				bean.setName(e.getAttribute("name"));
				System.out.println("CodeType classScheme: " + e.getAttribute("classScheme"));
				bean.setClassScheme(e.getAttribute("classScheme"));
				bean.setCode(getCode(e));
			}
		}
		
		return bean;
	}
*/
//	public Hashtable<String, CodeBean> getCode(Element element) {
//		Hashtable<String, CodeBean> code = new Hashtable<String, CodeBean>();
//		CodeBean bean = new CodeBean();
//
//		NodeList nodelist = element.getElementsByTagName("Code");
//
//		if (nodelist != null && nodelist.getLength() > 0) {
//			for (int i = 0 ; i < nodelist.getLength(); i++) {
//				Element e = (Element) nodelist.item(i);
//				bean.setCode(e.getAttribute("code"));
//				bean.setCodingScheme(e.getAttribute("codingScheme"));
//				bean.setDisplay(e.getAttribute("display"));
//				code.put(bean.getCode(), bean);
//			}
//		}
//		return code;
//	}
	
//	public Hashtable<String, CodeTypeBean> getCodeTypeList() {
//		if (codeTypeList == null) {
//			parseXmlFile ();
//		}
//		return codeTypeList;
//	}

}
