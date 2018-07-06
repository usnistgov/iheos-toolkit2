package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class SShasContent extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'SubmissionSets have something linked to them'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

        model.allSS().each { SubSet ss ->
            processed++
            result.trace("${this.getClass().simpleName}: Processing ${ss.objectDescription}")
            List<Assoc> assocs = model.subSetAssoc(ss)
            if (assocs.empty) {
                result.error("${ss.objectDescription} has no Associations linked to it")
            }
        }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
