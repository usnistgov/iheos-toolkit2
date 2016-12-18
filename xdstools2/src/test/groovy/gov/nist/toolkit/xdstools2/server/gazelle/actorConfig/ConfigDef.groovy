package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

/**
 * This matches the WebService configuration offered by Gazelle.
 * Updates to this must be matched in ConfigParser.groovy
 */
class ConfigDef {
    String configType
    String company
    String system
    String host
    String actor
    boolean secured
    boolean approved
    String comment
    String url
    String assigningAuthority
    String wsType
    String port
    String proxyPort
    String portSecured
}
