package gov.nist.toolkit.server.server.gazelle.sysconfig

import groovy.transform.ToString

/**
 *
 */
@ToString
class V2ResponderDef {
    String configType
    String company
    String system
    String host
    String actor
    boolean secured
    boolean approved
    String comment
    String assigningAuthority
    String rcvApplication
    String rcvFacility
    String port
    String proxyPort
    String portSecured

    boolean isPIF() {
        actor == 'DOC_REGISTRY' && comment.contains('ITI-8 Patient Identity Feed')
    }
}
