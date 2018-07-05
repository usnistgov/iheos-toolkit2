package gov.nist.toolkit.itTests.xdsModel

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.metadataModel.*
import gov.nist.toolkit.testengine.assertionEngine.XdsModel

class XdsModelTest extends ToolkitSpecification {
    String env = 'default'
    TestSession testSession = new TestSession('model')

    def 'Run Rule Test' () {
        setup:
        Store store = new Store()
        XdsModel model = new XdsModel(store)

        DocEntry de1 = new DocEntry().withId('de1')
        SubSet ss1 = new SubSet().withId('ss1')
        Assoc a1 = new Assoc().withFrom(ss1).withTo(de1).withType(RegIndex.AssocType.HASMEMBER).withId('a1')

        DocEntry de2 = new DocEntry().withId('de2')

        when:
        model.store.mc.clear().addAll([de1, ss1, a1])
        println model.run(env, testSession, [:])

        then:
        !model.hasError

        when:
        model.store.mc.clear().addAll([de1, ss1, a1, de2])
        println model.run(env, testSession, [:])

        then:
        model.hasError
    }
}
