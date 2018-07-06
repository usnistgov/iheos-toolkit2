package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class DEhasSS extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'DocumentEntries are properly linked to SubmissionSets via a HasMember Association'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

        model.allDE().each { DocEntry de ->
            processed++
            result.trace("${this.getClass().simpleName}: Processing ${de.objectDescription}")
            List<XdsModel.SSandAssoc> ssa = model.ss(de)
            if (ssa.empty) {
                result.error("${de.getObjectDescription()} not linked to a SubmissionSet")
                return
            }
            if (ssa.size() > 1)
                result.error("${de.getObjectDescription()} is linked to multiple SubmissionSets ${ssa}")
            SubSet ss = ssa[0].subSet
            Assoc a = ssa[0].assoc
            if (a.assocType != RegIndex.AssocType.HASMEMBER)
                result.error("${de.getObjectDescription()} is linked to ${a.getObjectDescription()} through a ${a.assocType} Association instead of a HASMEMBER")
            if (a.from != ss.id)
                result.error("SS-DE HasMember must reference SS with sourceObject not targetObject - SS is ${ss.getObjectDescription()}  DE is ${de.getObjectDescription()}")
        }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
