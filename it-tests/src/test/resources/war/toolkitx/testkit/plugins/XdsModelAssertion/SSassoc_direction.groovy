package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class SSassoc_direction extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'Associations referencing SubmissionSets use correct attribute'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

        model.allSS().each { SubSet ss ->
            processed++
            result.trace("${this.getClass().simpleName}: Processing ${ss.objectDescription}")
            List<Assoc> assocs = model.subSetAssoc(ss)
            assocs.each { Assoc a ->
                if (a.from != ss.id)
                    result.error("$ss.objectDescription has Association ($a.id) but sourceObject does not reference SubmissionSet")
                if (a.to == ss.id)
                    result.error("$ss.objectDescription has Association ($a.id) that references it with the targetobject attribute")
            }
        }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
