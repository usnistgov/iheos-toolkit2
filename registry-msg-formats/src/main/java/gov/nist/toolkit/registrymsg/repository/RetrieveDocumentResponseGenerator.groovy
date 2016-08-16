package gov.nist.toolkit.registrymsg.repository

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMAttribute
import org.apache.axiom.om.OMElement

/**
 *
 */
@TypeChecked
public class RetrieveDocumentResponseGenerator {
    RetrievedDocumentsModel model;
    OMAttribute statusAtt;
    RegistryErrorListGenerator registryErrorListGenerator;

    public RetrieveDocumentResponseGenerator(RetrievedDocumentsModel model, RegistryErrorListGenerator registryErrorListGenerator) {
        this.model = model
        this.registryErrorListGenerator = registryErrorListGenerator
    }

    public RetrieveDocumentResponseGenerator(RetrievedDocumentsModel model) {
        this.model = model
        this.registryErrorListGenerator = null
    }

    public OMElement get() {
        return generate();
    }

   OMElement generate() {
      OMElement response;
      response = MetadataSupport.om_factory.createOMElement("RegistryResponse", MetadataSupport.ebRSns3);
      OMElement rdsr = MetadataSupport.om_factory.createOMElement("RetrieveDocumentSetResponse", MetadataSupport.xdsB);
      rdsr.addChild(response);

      if (registryErrorListGenerator && registryErrorListGenerator.hasErrors()) {
         String status = MetadataSupport.status_failure;
         if (model.size() > 0) status = MetadataSupport.status_partial_success;
         statusAtt = MetadataSupport.om_factory.createOMAttribute("status", null, status);
         response.addChild(registryErrorListGenerator.registryErrorList)
      } else {
         statusAtt = MetadataSupport.om_factory.createOMAttribute("status", null, MetadataSupport.status_success);
      }

      response.addAttribute(statusAtt);

      for (RetrievedDocumentModel item : model.values()) {
         rdsr.addChild(new DocumentResponseGenerator(item).get());
      }
      return rdsr;
   }

    public OMAttribute getStatusAtt() { return statusAtt; }
}
