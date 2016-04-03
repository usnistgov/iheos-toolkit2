package gov.nist.toolkit.valregmetadata.newvalidators;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.valregmetadata.model.ExternalIdentifier;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class ExternalIdentifierValidator implements ObjectValidator {
    ExternalIdentifier mo;
    static public String table415 = "ITI TF-3: Table 4.2.3.2-1"; // Rev 12.1 Final Text

    public ExternalIdentifierValidator(ExternalIdentifier mo) {
        this.mo = mo;
    }

    public void validateStructure(ErrorRecorder er, ValidationContext vc) {
        new RegistryObjectValidator(mo, this).validateId(er, vc, "entryUUID", mo.getId(), null);
        OMElement parentEle = (OMElement) mo.getRo().getParent();
        String parentEleId = ((parentEle == null) ? "null" :
                parentEle.getAttributeValue(MetadataSupport.id_qname));
        String registryObject = mo.getRo().getAttributeValue(MetadataSupport.registry_object_qname);

        if (parentEle != null && !parentEleId.equals(registryObject))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": is a child of model " + parentEleId + " but the registryObject value is " +
                    registryObject + ", they must match", this, "ITI TF-3: 4.1.12.5");

        if (mo.getValue() == null || mo.getValue().equals(""))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": value attribute missing or empty", this, "ebRIM 3.0 section 2.11.1");

        if (mo.getName() == null || mo.getName().equals(""))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": display name (Name element) missing or empty", this, "ITI TF-3: 4.1.12.5");
    }

    public void validateRequiredSlotsPresent(ErrorRecorder er,
                                             ValidationContext vc) {
    }

    public void validateSlotsCodedCorrectly(ErrorRecorder er,
                                            ValidationContext vc) {
    }

    public void validateSlotsLegal(ErrorRecorder er) {
    }

}
