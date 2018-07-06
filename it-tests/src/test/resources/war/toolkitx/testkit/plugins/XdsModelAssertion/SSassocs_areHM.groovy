package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class SSassocs_areHM extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'Associations referencing SubmissionSets are HasMember'

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

        model.allSS().each { SubSet ss ->
            processed++
            result.trace("${this.getClass().simpleName}: Processing ${ss.objectDescription}")
            List<Assoc> assocs = model.subSetAssoc(ss)
            assocs.each { Assoc a ->
                if (a.assocType != RegIndex.AssocType.HASMEMBER)
                    result.error("$ss.objectDescription has Association ($a.id) of type $a.assocType referencing it")
            }
        }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
