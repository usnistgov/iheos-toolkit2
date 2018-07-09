package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.metadataModel.Fol
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.Ro
import gov.nist.toolkit.metadataModel.SubSet
import gov.nist.toolkit.testengine.assertionEngine.AbstractXdsModelValidater
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult

class AssocReferencesTypeValid extends AbstractXdsModelValidater {
    int processed = 0


    String description = 'Association references are type valid - objects are of proper type given the type of Association'

    List<RegIndex.AssocType> deOnlyType = [
            RegIndex.AssocType.RPLC,
            RegIndex.AssocType.APND,
            RegIndex.AssocType.RPLC_XFRM,
            RegIndex.AssocType.XFRM,
            RegIndex.AssocType.ISSNAPSHOTOF,
            RegIndex.AssocType.SIGNS
    ]

    @Override
    void validate(XdsModel model, XdsModelValidationResult result) {

            model.store.mc.assocCollection.assocs.each { Assoc assoc ->
                processed++
                result.trace("${this.getClass().simpleName}: Processing ${assoc.objectDescription}")
                Ro fromobj = model.store.mc.getObjectById(assoc.from)
                if (fromobj) {
                    if (assoc.assocType == RegIndex.AssocType.HASMEMBER) {
                        if (!(fromobj instanceof SubSet || fromobj instanceof Fol))
                            result.error("${assoc.objectDescription} (type ${assoc.assocType}) references ${fromobj.objectDescription} with its sourceObject attribute - must reference SubmissionSet or Folder")
                    } else {
                        if (fromobj instanceof SubSet || fromobj instanceof Fol) {
                            result.error("${assoc.objectDescription} (type ${assoc.assocType}) references ${fromobj.objectDescription} with its sourceObject attribute - must reference a DocumentEntry")
                    }
                }
                    Ro toobj = model.store.mc.getObjectById(assoc.to)
                    if (toobj) {
                        if (assoc.assocType == RegIndex.AssocType.HASMEMBER) {
                            if (toobj instanceof SubSet)
                                result.error("${assoc.objectDescription} (type ${assoc.assocType}) references ${toobj.objectDescription} with its targetObject attribute - must reference DocumentEntry, Folder, or Association")
                        } else {
                            if (fromobj instanceof SubSet || fromobj instanceof Fol) {
                                result.error("${assoc.objectDescription} (type ${assoc.assocType}) references ${fromobj.objectDescription} with its soureceObject attribute - must reference a DocumentEntry")
                            }
                        }
                    }
                }
            }
    }

    @Override
    int getNumberObjectsProcessed() {
        return processed
    }
}
