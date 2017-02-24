package gov.nist.toolkit.itTests.interactionsequence

import gov.nist.toolkit.interactionmodel.client.InteractingEntity
import gov.nist.toolkit.interactionmodel.server.InteractionSequences
import gov.nist.toolkit.itTests.support.ToolkitSpecification

class InteractionSequenceSpec extends ToolkitSpecification {

    def setupSpec() {
    }
    def cleanupSpec() {
    }

    def setup() {

    }

    def 'Test transformation'() {

        when:
        InteractionSequences.init()

        then:
        InteractionSequences.getSequencesMap().size() > 0

        List<InteractingEntity> seq = InteractionSequences.getInteractionSequenceByTransactionType("ProvideAndRegisterTransaction")

        InteractingEntity docSrc = seq.get(0)
        docSrc != null
        docSrc.role == "Document Source"

        docSrc.getInteractions().size()==1
        InteractingEntity repos = docSrc.getInteractions().get(0)
        repos.role == "Repository"

        InteractingEntity reg = repos.getInteractions().get(0)
        reg.role == "Registry"

    }
}