package gov.nist.toolkit.valregmetadata.validators;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.valregmetadata.model.Author;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class AuthorValidator implements ObjectValidator {
    Author mo;
    static public String table415 = "ITI TF-3: Table 4.2.3.2-1"; // Rev 12.1 Final Text

    public AuthorValidator(Author mo) {
        this.mo = mo;
    }

    public void validateStructure(ErrorRecorder er, ValidationContext vc)  {
        new RegistryObjectValidator(mo, this).validateId(er, vc, "entryUUID", mo.getId(), null);
        OMElement parentEle = (OMElement) mo.getRo().getParent();
        String parentEleId =  ((parentEle == null) ? "null" :
                parentEle.getAttributeValue(MetadataSupport.id_qname));
        String classifiedObjectId = mo.getRo().getAttributeValue(MetadataSupport.classified_object_qname);

        if (parentEle != null && !parentEleId.equals(classifiedObjectId))
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": is a child of model " + parentEleId + " but the classifiedObject value is " +
                    classifiedObjectId + ", they must match", this, "ITI TF-3: 4.1.12.2");

        try {
            if (mo.getClassificationScheme() == null || mo.getClassificationScheme().equals(""))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": does not have a value for the classificationScheme attribute", this, "ebRIM 3.0 section 4.3.1");
            else if (!mo.getClassificationScheme().startsWith("urn:uuid:"))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, mo.identifyingString() + ": classificationScheme attribute value is not have urn:uuid: prefix", this, "ITI TF-3: 4.3.1");
        } catch (XdsInternalException e) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
        }
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
