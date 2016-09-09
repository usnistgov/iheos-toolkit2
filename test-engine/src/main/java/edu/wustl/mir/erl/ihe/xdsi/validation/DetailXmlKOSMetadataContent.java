/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;

import javax.xml.namespace.QName;

/**
 * Content evaluation for KOS Metadata
 */
public class DetailXmlKOSMetadataContent extends DetailXmlContent {
   
   @Override
   protected void initializeTests() {
      // TODO Is 2.1 is the right one ???
      qnames = new QName[] 
         {  new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "dummy", "lcm"),
            new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "dummy", "rs"),
            new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "dummy", "rim") 
          // new QName("urn:oasis:names:tc:ebxml-regrep:registry:xsd:2.1", "dummy", "rs"),
          // new QName("urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1", "dummy", "rim") 
         };
      desc = "KOS Metadata";
      assertions = new ArrayList<>();
      assertions.add(
         new XMLAssertion("patient ID", TYPE.SAME, "//rim:ExternalIdentifier[@identificationScheme='urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446']/@value", 
                  null));
   }
}
