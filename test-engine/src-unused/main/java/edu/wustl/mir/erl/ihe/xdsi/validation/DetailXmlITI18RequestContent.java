/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import edu.wustl.mir.erl.ihe.xdsi.validation.DetailXmlContent.TYPE;
import edu.wustl.mir.erl.ihe.xdsi.validation.DetailXmlContent.XMLAssertion;

/**
 * Content Evaluation for Registry Stored Query ITI-18 Request
 */
public class DetailXmlITI18RequestContent extends DetailXmlContent {

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.DetailXmlContent#initializeTests()
    */
   @Override
   protected void initializeTests() {
      
      qnames = new QName[] {
         new QName("urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0", "dummy", "query"),
         new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "dummy", "rim"),
         new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "dummy", "rs"),
         new QName("http://www.w3.org/2001/XMLSchema-instance", "dummy", "xsi")
      };
      String root1 = "/query:AdhocQueryRequest";
      String root2 = root1 + "/rim:AdhocQuery";
      desc = "KOS Metadata";
      assertions = new ArrayList<>();
      assertions.add(
         new XMLAssertion("patient ID", TYPE.SAME, root2 + 
            "/rim:Slot[@name='$XDSDocumentEntryPatientId']/rim:ValueList/rim:Value/text()", 
                  null));
   }
}
