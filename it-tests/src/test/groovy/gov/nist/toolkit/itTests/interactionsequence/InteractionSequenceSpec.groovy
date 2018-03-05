package gov.nist.toolkit.itTests.interactionsequence

import gov.nist.toolkit.interactionmodel.client.InteractingEntity
import gov.nist.toolkit.interactionmodel.server.InteractionSequences
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.installation.server.Installation

class InteractionSequenceSpec extends ToolkitSpecification {

    def setupSpec() {
    }
    def cleanupSpec() {
    }

    def setup() {

    }

    def 'Test PnR transformation'() {

        when:
        InteractionSequences.init(Installation.instance().getInteractionSequencesFile())

        then:
        InteractionSequences.getSequencesMap().size() > 0

        List<InteractingEntity> seq = InteractionSequences.getInteractionSequenceById("ProvideAndRegisterTransaction")

        seq != null && seq.size() == 1

        InteractingEntity docSrc = seq.get(0)
        docSrc != null
        docSrc.role == "Document Source"

        System.out.println(docSrc.toString())

        docSrc.getInteractions().size()==1
        InteractingEntity repos = docSrc.getInteractions().get(0)
        repos.role == "Repository"

        InteractingEntity reg = repos.getInteractions().get(0)
        reg.role == "Registry"

    }

    def 'Test XCR transformation'() {

        when:
        InteractionSequences.init(Installation.instance().getInteractionSequencesFile())

        then:
        InteractionSequences.getSequencesMap().size() > 0

        List<InteractingEntity> seq = InteractionSequences.getInteractionSequenceById("XCRTransaction")

        seq != null && seq.size() == 1

        InteractingEntity ig = seq.get(0)
        ig != null
        ig.role == "Initiating Gateway"

        System.out.println(ig.toString())

        ig.getInteractions().size() == 2

        InteractingEntity rg1 = ig.getInteractions().get(0)
        rg1.role == "Responding Gateway"
        System.out.println(rg1.toString())

        InteractingEntity rg2 = ig.getInteractions().get(1)
        rg2.role == "Responding Gateway"
        System.out.println(rg2.toString())


    }

}