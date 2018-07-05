package gov.nist.toolkit.testengine.assertionEngine

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.metadataModel.*
import gov.nist.toolkit.testengine.engine.AbstractValidater
import gov.nist.toolkit.testkitutilities.TestKit
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

    XdsModelValidationResults run(String env, TestSession testSession, Map<String, String> parameters) {
        AssertionContext.Context context = AssertionContext.get(TestKit.PluginType.XDSMODEL_ASSERTION, env, testSession)
        List<AbstractValidater> validaters = context.getAllValidaters(parameters)

        List<XdsModelValidationResult> results = validaters.collect { AbstractValidater aval ->
            if (!(aval instanceof AbstractXdsModelValidater))
                return new XdsModelValidationResult()
            AbstractXdsModelValidater validater = (AbstractXdsModelValidater)aval
            XdsModelValidationResult result = new XdsModelValidationResult()
            result.assertionName = validater.getClass().getSimpleName()
            validater.validate(this, result)
            if (result.hasErrors())
                hasError = true
            result
        }
        new XdsModelValidationResults(results)

    }

    boolean hasError = false
}
