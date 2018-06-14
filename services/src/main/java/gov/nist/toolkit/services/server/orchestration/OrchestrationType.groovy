package gov.nist.toolkit.services.server.orchestration

import groovy.transform.TypeChecked

@TypeChecked
class OrchestrationType {
    def actor
    def transaction
    def profile

    OrchestrationType(def actor, def transaction, def profle) {
        this.actor = actor
        this.transaction = transaction
        this.profile = profle
    }

    // reg_xds(Required)
    OrchestrationType(String label) {

    }
}
