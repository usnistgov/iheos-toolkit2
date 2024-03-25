/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import gov.nist.toolkit.testengine.engine.SimulatorTransaction;

/**
 * Content evaluation for KOS Metadata
 */
public class DetailXmlKOSMetadataContent extends DetailXmlContent {
   
   SimulatorTransaction trn;
   
   /**
    * @param simTran SimulatorTransaction
    */
   public DetailXmlKOSMetadataContent(SimulatorTransaction simTran) {
      trn = simTran;
   }

   @Override
   protected void initializeTests() {
      String siuid = trn.resolve("siuid");
      qnames = new QName[] 
         {  new QName("urn:ihe:iti:xds-b:2007", "dummy", "xdsb"),
            new QName("urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", "dummy", "lcm"),
            new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "dummy", "rs"),
            new QName("urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "dummy", "rim")
         };
      String root = "/xdsb:ProvideAndRegisterDocumentSetRequest/lcm:SubmitObjectsRequest/rim:RegistryObjectList";
      desc = "KOS Metadata";
      assertions = new ArrayList<>();
      assertions.add(
         new XMLAssertion("patient ID", TYPE.SAME, root + 
            "/rim:RegistryPackage/rim:ExternalIdentifier[@identificationScheme='urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446']/@value", 
                  null));
      assertions.add(
         new XMLAssertion("Mime Type", TYPE.SAME, root + 
            "/rim:ExtrinsicObject[@objectType='urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1']/@mimeType", 
                  null));
      assertions.add(
         new XMLAssertion("Format Code", TYPE.SAMECLASSIFICATIONCODE, root + 
            "/rim:ExtrinsicObject/rim:Classification[@classificationScheme='urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d']", 
                  null));
      assertions.add(
         new XMLAssertion("Unique ID", TYPE.CONSTANT, root + 
            "/rim:ExtrinsicObject/rim:ExternalIdentifier[@identificationScheme='urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab']/@value", 
                  siuid));
   }
}
