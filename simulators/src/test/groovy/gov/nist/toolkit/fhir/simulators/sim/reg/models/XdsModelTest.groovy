package gov.nist.toolkit.fhir.simulators.sim.reg.models

import gov.nist.toolkit.fhir.simulators.sim.reg.store.Assoc
import gov.nist.toolkit.fhir.simulators.sim.reg.store.DocEntry
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex
import gov.nist.toolkit.fhir.simulators.sim.reg.store.SubSet
import spock.lang.Specification

class XdsModelTest extends Specification {

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
        model.run()

        then:
        !model.hasError

        when:
        model.store.mc.clear().addAll([de1, ss1, a1, de2])
        model.run()

        then:
        model.hasError
    }
}
