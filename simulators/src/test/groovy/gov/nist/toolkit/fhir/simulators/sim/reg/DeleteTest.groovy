package gov.nist.toolkit.fhir.simulators.sim.reg

import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.fhir.simulators.sim.reg.mu.RMSim
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Assoc
import gov.nist.toolkit.fhir.simulators.sim.reg.store.DocEntry
import gov.nist.toolkit.fhir.simulators.sim.reg.store.MetadataCollection
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex
import gov.nist.toolkit.fhir.simulators.sim.reg.store.Ro
import gov.nist.toolkit.fhir.simulators.sim.reg.store.SubSet
import spock.lang.Specification

class DeleteTest extends Specification{

    def 'GetAll DocEntry'() {
        setup:
        MetadataCollection mc = new MetadataCollection()
        MetadataCollection delta = mc.mkDelta()
        DocEntry de1 = new DocEntry()
        de1.id = 'urn:uuid:xxx'
        DocEntry de2 = new DocEntry()
        de2.id = 'urn:uuid:yyy'

        when:
        mc.add(de1)
        mc.add(de2)

        then:
        delta.docEntryCollection.getAll().size() == 2

        when:
        delta.deleting = ['urn:uuid:yyy']

        then:
        delta.docEntryCollection.getAll().size() ==1
    }

    def 'Delete parts of submission' () {
        setup:
        ErrorRecorder er = new TextErrorRecorder()
        MetadataCollection mc = new MetadataCollection()
        MetadataCollection delta = mc.mkDelta()
        DocEntry de = new DocEntry()
        de.id = 'urn:uuid:de'
        SubSet ss = new SubSet()
        ss.id = 'urn:uuid:ss'
        Assoc a = new Assoc()
        a.id = 'urn:uuid:a'

        a.from = ss.id
        a.to = de.id
        a.type = RegIndex.AssocType.HasMember

        when:
        mc.add(ss)
        mc.add(de)
        mc.add(a)

        Map<String, Ro> typeMap = delta.buildTypeMap()

        List<String> toDelete = ['urn:uuid:de', 'urn:uuid:a']

        delta.deleting = toDelete

        RMSim.handleDanglingObjects(er, delta, typeMap, toDelete)

        then:
        er.hasErrors()


    }

}
