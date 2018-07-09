package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class AssocReferencesValid extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'Association references are valid - objects exist'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

            model.store.mc.assocCollection.assocs.each { Assoc assoc ->
                processed++
                result.trace("${this.getClass().simpleName}: Processing ${assoc.objectDescription}")
                if (!model.store.mc.exists(assoc.from))
                    result.error("$assoc.objectDescription references object $assoc.from with its sourceObject attribute - it does not exist")
                if (!model.store.mc.exists(assoc.to))
                    result.error("$assoc.objectDescription references object $assoc.to with its targetObject attribute - it does not exist")
            }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
