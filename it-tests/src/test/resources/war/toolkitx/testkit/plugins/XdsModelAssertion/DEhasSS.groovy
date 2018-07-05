package war.toolkitx.testkit.plugins.XdsModelAssertion

import gov.nist.toolkit.fhir.simulators.sim.reg.models.XdsModel
import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet

class DEhasSS {
    XdsModel model


    def run() {
        model.allDE().each { DocEntry de ->
            println "rule_DEhasSS: Processing ${de.objectDescription}"
            List<XdsModel.SSandAssoc> ssa = model.ss(de)
            if (ssa.empty) {
                model.error("${de.getObjectDescription()} not linked to a SubmissionSet")
                return
            }
            if (ssa.size() > 1)
                model.error("${de.getObjectDescription()} is linked to multiple SubmissionSets ${ssa}")
            SubSet ss = ssa[0].subSet
            Assoc a = ssa[0].assoc
            if (a.assocType != RegIndex.AssocType.HASMEMBER)
                model.error("${de.getObjectDescription()} is linked to ${a.getObjectDescription()} through a ${a.assocType} Association instead of a HASMEMBER")
            if (a.from != ss.id)
                model.error("SS-DE HasMember must reference SS with sourceObject not targetObject - SS is ${ss.getObjectDescription()}  DE is ${de.getObjectDescription()}")
        }
    }
}
