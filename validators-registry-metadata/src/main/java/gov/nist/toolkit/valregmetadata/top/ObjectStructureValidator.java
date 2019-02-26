package gov.nist.toolkit.valregmetadata.top;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valregmetadata.model.Association;
import gov.nist.toolkit.valregmetadata.model.DocumentEntry;
import gov.nist.toolkit.valregmetadata.model.Folder;
import gov.nist.toolkit.valregmetadata.model.SubmissionSet;
import gov.nist.toolkit.valregmetadata.validators.AssociationValidator;
import gov.nist.toolkit.valregmetadata.validators.DocumentEntryValidator;
import gov.nist.toolkit.valregmetadata.validators.FolderValidator;
import gov.nist.toolkit.valregmetadata.validators.SubmissionSetValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ObjectStructureValidator {
    Metadata m;
    ValidationContext vc;
    RegistryValidationInterface rvi;

    public ObjectStructureValidator(Metadata m, ValidationContext vc, RegistryValidationInterface rvi) {
        this.m = m;
        this.vc = vc;
        this.rvi = rvi;
    }

    public void run(ErrorRecorder er)   {

        if (vc.skipInternalStructure)
            return;

        er.sectionHeading("Evaluating metadata model structure");

        Set<String> knownIds = new HashSet<String>();

        SubmissionSet s = null;

        for (OMElement ssEle : m.getSubmissionSets()) {
            er.sectionHeading("SubmissionSet(" + ssEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            try {
                s = new SubmissionSet(m, ssEle);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            new SubmissionSetValidator(s).validate(er, vc, knownIds);
        }

        for (OMElement deEle : m.getExtrinsicObjects() ) {
            er.sectionHeading("DocumentEntry(" + deEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            DocumentEntry de = null;
            try {
                de = new DocumentEntry(m, deEle);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            new DocumentEntryValidator(de).validate(er, vc, knownIds);
        }

        for (OMElement fEle : m.getFolders()) {
            er.sectionHeading("Folder(" + fEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            Folder f = null;
            try {
                f = new Folder(m, fEle);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            new FolderValidator(f).validate(er, vc, knownIds);
        }

        for (OMElement aEle : m.getAssociations()) {
            er.sectionHeading("Association(" + aEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            Association a = null;
            try {
                a = new Association(m, aEle, vc);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            new AssociationValidator(a).validate(er, vc, knownIds);

            String targetUuid = a.getTarget();
            if (s != null) {
                if (targetUuid.equals(s.getId()))
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "Association(" + a.identifyingString() + ") : references a SubmissionSet as a target.", this, "ITI TF-3: 4.1.9.1");
            }
        }

        er.sectionHeading("Other metadata objects");

        for (String id : m.getRegistryPackageIds()) {
            if (m.getSubmissionSetIds().contains(id))
                ;
            else if (m.getFolderIds().contains(id))
                ;
            else
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryPackage(" + id + ") : is not classified as SubmissionSet or Folder", this, "ITI TF-3: 4.1.9.1");
        }
    }

}
