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
class SingleDocTest extends Specification {
    List<RO> o = []
    Engine engine


    def setup() {
        o.add(new SubmissionSet('SS'))
        o.add(new DocumentEntry('DE'))
        o.add(new Association('assn1', 'SS', 'DE', AssnType.HasMember))
        println o
    }

    def 'remove all'() {
        when:
        List<Uuid> removeSet = o.collect { it.id }  // delete all
        println "remove set is ${removeSet}"
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses == []
    }

    def 'remove assn'() {
        when:
        List<Uuid> removeSet = [new Uuid('assn1')]
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses.size() == 1
        e.responses.contains(new Response(ErrorType.ObjectNotInDeletionSet, 'DE', 'ruleDanglingSS_DE_HasMember'))

    }

    def 'remove DE'() {
        when:
        List<Uuid> removeSet = [new Uuid('DE')]
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses.size() == 1
        e.responses.contains(new Response(ErrorType.ObjectNotInDeletionSet, 'assn1', 'ruleDeletingDocumentEntry'))
    }

    def 'remove SS'() {
        when:
        List<Uuid> removeSet = [new Uuid('SS')]
        Engine e = new Engine(o, removeSet).run()

        then:
        e.responses.size() == 1
        e.responses.contains(new Response(ErrorType.ObjectNotInDeletionSet, 'assn1', 'ruleDeletingSubmissionSet'))
    }
}
