package gov.nist.toolkit.fhir.simulators.sim.reg.models

import gov.nist.toolkit.metadataModel.Assoc
import gov.nist.toolkit.metadataModel.DocEntry
import gov.nist.toolkit.metadataModel.RegIndex
import gov.nist.toolkit.metadataModel.SubSet
import groovy.transform.TypeChecked

@TypeChecked
class XdsModel {
    Store store

    XdsModel(Store store) {
        this.store = store
    }

    class SSandAssoc {
        SubSet subSet
        Assoc assoc

        SSandAssoc(SubSet s, Assoc a) {
            subSet = s
            assoc = a
        }
    }

    /////////////////////////////////////////
    //
    // Search functions
    //
    /////////////////////////////////////////

    // SubmissionSets linked to de
    List<SSandAssoc> ss(DocEntry de) {
        List<SSandAssoc> col = new ArrayList<>()

        List<Assoc> assocsLinkedToDe = store.mc.assocCollection.getBySourceOrDest(de.id, de.id)
        assocsLinkedToDe.each { Assoc a ->
            if (store.mc.subSetCollection.isSubSet(a.from))
                col.add(new SSandAssoc(store.mc.subSetCollection.getById(a.from), a))
            else if (store.mc.subSetCollection.isSubSet(a.to))
                col.add(new SSandAssoc(store.mc.subSetCollection.getById(a.to), a))
        }
        col
    }

    // Assocs linking de to a SS
    List<Assoc>  subSetAssoc(DocEntry de) {

    }

    // Assocs linking this ss to a DE
    List<Assoc> docEntryAssoc(SubSet ss) {

    }

    // Assocs linking this de to another de
    List<Assoc> docEntryAssoc(DocEntry de) {

    }

    // Assocs linking to this ss
    List<Assoc> subSetAssoc(SubSet ss) {

    }

    List<DocEntry> allDE() {
        store.mc.docEntryCollection.getEntries()
    }

    // DE is linked to a SS
    def rule_DEhasSS() {
        allDE().each { DocEntry de ->
            println "rule_DEhasSS: Processing ${de.objectDescription}"
            List<SSandAssoc> ssa = ss(de)
            if (ssa.empty) {
                error("${de.getObjectDescription()} not linked to a SubmissionSet")
                return
            }
            if (ssa.size() > 1)
                error("${de.getObjectDescription()} is linked to multiple SubmissionSets ${ssa}")
            SubSet ss = ssa[0].subSet
            Assoc a = ssa[0].assoc
            if (a.assocType != RegIndex.AssocType.HASMEMBER)
                error("${de.getObjectDescription()} is linked to ${a.getObjectDescription()} through a ${a.assocType} Association instead of a HASMEMBER")
            if (a.from != ss.id)
                error("SS-DE HasMember must reference SS with sourceObject not targetObject - SS is ${ss.getObjectDescription()}  DE is ${de.getObjectDescription()}")
        }
    }

    def run() {
        rule_DEhasSS()
    }

    boolean hasError = false

    def error(String msg) {
        println msg
        hasError = true
    }
}
