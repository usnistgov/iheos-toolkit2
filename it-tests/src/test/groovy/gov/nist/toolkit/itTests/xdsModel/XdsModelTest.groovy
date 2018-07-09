package gov.nist.toolkit.itTests.xdsModel

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.metadataModel.*
import gov.nist.toolkit.testengine.assertionEngine.XdsModel
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResult
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationResults
import gov.nist.toolkit.testengine.assertionEngine.XdsModelValidationSummary
import spock.lang.Shared

class XdsModelTest extends ToolkitSpecification {
    @Shared Store store = new Store()
    @Shared XdsModel model = new XdsModel(store)
    @Shared String env = 'default'
    @Shared TestSession testSession = new TestSession('model')
    @Shared singleDocSubmit = [
            new DocEntry().withId('de1'),
            new SubSet().withId('ss1'),
            new Assoc().withFrom('ss1').withTo('de1').withType(RegIndex.AssocType.HASMEMBER).withId('a1')
    ]

    def mkSingleDocSubmit(ssID, deID, assocID) {
        [
                new DocEntry().withId(deID),
                new SubSet().withId(ssID),
                new Assoc().withFrom(ssID).withTo(deID).withType(RegIndex.AssocType.HASMEMBER).withId(assocID)
        ]
    }

    List<DocEntry> filterDEs(List<Ro> objects) {
        objects.findAll { Ro ro -> ro instanceof DocEntry} as List<DocEntry>
    }

    List<Ro> deprecateDEs(List<Ro> objects) {
        List<Ro> a =
        objects.collect { Ro ro ->
            if (ro instanceof DocEntry)
                ro.deprecate()
            ro
        } as List<Ro>
        return a
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    def 'Single Doc Submit' () {
        when:
        XdsModelValidationResults results = run(singleDocSubmit)

        then:
        results.failingValidaters.empty
    }

    def 'DE no SS' () {
        when:
        XdsModelValidationResults results = run([
                new DocEntry().withId('de2')
        ])

        then:
        results.getFailingValidaterNames() == ['DEhasSS'] as Set
    }

    def 'SS without content'() {
        when:
        XdsModelValidationResults results = run([
                new SubSet().withId('SS1')
        ])

        then:
        results.getFailingValidaterNames() == ['SShasContent'] as Set
    }

    def 'SS referenced by Assoc.targetObject' () {
        when:
        XdsModelValidationResults results = run([
                new DocEntry().withId('de1'),
                new SubSet().withId('ss1'),
                new Assoc().withFrom('de1').withTo('ss1').withType(RegIndex.AssocType.HASMEMBER).withId('a1')
        ])

        then:
        results.getFailingValidaterNames() == ['DEhasSS', 'SSassoc_direction'] as Set
    }

    def 'Replaced DEs are deprecated'() {
        when:  // good
        XdsModelValidationResults results = run(
                deprecateDEs(mkSingleDocSubmit('ss1', 'de1', 'hm1')) +
                        mkSingleDocSubmit('ss2', 'de2', 'hm2')
                        + [new Assoc().withFrom('de2').withTo('de1').withType(RegIndex.AssocType.RPLC).withId('rplc1')]
        )

        then:
        results.failingValidaters.empty

        when:  // not deprecated
        XdsModelValidationResults results2 = run(
                mkSingleDocSubmit('ss1', 'de1', 'hm1') +
                        mkSingleDocSubmit('ss2', 'de2', 'hm2')
                        + [new Assoc().withFrom('de2').withTo('de1').withType(RegIndex.AssocType.RPLC).withId('rplc1')]
        )

        then:
        results2.getFailingValidaterNames() == ['ReplacedDEDeprecated'] as Set
    }

    def 'Check assoc references valid'() {
        when:
        XdsModelValidationResults results = run(
                mkSingleDocSubmit('ss2', 'de2', 'hm2') +
                        [new Assoc().withFrom('de2').withTo('de1').withType(RegIndex.AssocType.APND).withId('rplc1')]
        )

        then:
        results.getFailingValidaterNames() == ['AssocReferencesValid'] as Set
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    XdsModelValidationResults run(objects) {
        model.store.mc.clear().addAll(objects)
        XdsModelValidationResults results = model.run(env, testSession, [:])
        report(results)
        results
    }

    def report(results) {
        println '***********************************************'
        println results.toString(XdsModelValidationResult.Level.INFO)
        println '++++++++++++++++++++++'
        println new XdsModelValidationSummary(results, model.objectCount()).toString()
        println '***********************************************'
    }
}
