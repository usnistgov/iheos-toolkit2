package gov.nist.toolkit.registrymetadata.deletion

import gov.nist.toolkit.registrymetadata.deletion.objects.Association
import gov.nist.toolkit.registrymetadata.deletion.objects.DocumentEntry
import gov.nist.toolkit.registrymetadata.deletion.objects.RO
import gov.nist.toolkit.registrymetadata.deletion.objects.SubmissionSet
import gov.nist.toolkit.registrymetadata.deletion.support.Engine
import spock.lang.Specification

/**
 *
 */
class ApndTest extends Specification {
    List<RO> o = []
    Engine engine

    def setup() {
        o.add(new SubmissionSet('SS1'))
        o.add(new DocumentEntry('DE1'))
        o.add(new Association('assn1', 'SS1', 'DE1', AssnType.HasMember))

        o.add(new SubmissionSet('SS2'))
        o.add(new DocumentEntry('DE2'))
        o.add(new Association('assn2', 'SS2', 'DE2', AssnType.HasMember))

        o.add(new Association('apnd', 'DE2', 'DE1', AssnType.APND))
        println o
    }

    def 'remove entire apnd'() {
        when:
        List<Uuid> removeSet = [new Uuid('SS2'), new Uuid('DE2'), new Uuid('assn2'), new Uuid('apnd')]
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses.size() == 0
    }

    def 'remove apnd'() {
        when:
        List<Uuid> removeSet = [new Uuid('apnd')]
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses.size() == 0
    }
}
