package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.metadataModel.Ro
import gov.nist.toolkit.metadataModel.StatusValue
import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class ReplacedDEDeprecated extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'Replaced DocumentEntries are deprecated'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

            model.store.mc.assocCollection.getBySourceDestAndType(null, null, RegIndex.AssocType.RPLC).each { Assoc rplc ->
                processed++
                result.trace("${this.getClass().simpleName}: Processing ${rplc.objectDescription}")
                String targetId = rplc.to
                Ro ro = model.store.mc.getObjectById(targetId)
                if (!(ro instanceof DocEntry))
                    return
                DocEntry de = ro
                if (de.availabilityStatus != StatusValue.DEPRECATED)
                    result.error("${de.objectDescription} is replaced by object ${model.store.mc.getObjectById(rplc.from)?.objectDescription} but has status ${de.availabilityStatus} instead of DEPRECATED")
            }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
