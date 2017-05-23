package gov.nist.toolkit.server.server.gazelle.sysconfig

import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager

/**
 * Pull system configuration or OIDs from Gazelle into local cache.
 *
 * gazelleBaseUrl must be of the form
 *   https://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=35
 */
class GazellePull {
    String gazelleBaseUrl

    GazellePull(String _gazelleBaseUrl) {
        gazelleBaseUrl = _gazelleBaseUrl
        initTls()
    }

    String getConfigs() {
        println "Pulling configs from " + gazelleBaseUrl + '&configurationType=WebServiceConfiguration'
        return (gazelleBaseUrl + '&configurationType=WebServiceConfiguration').toURL().text
    }

    String getSingleConfig(String _systemName) {
        String systemName = java.net.URLEncoder.encode(_systemName, "UTF-8")
        println "Pulling configs from " + gazelleBaseUrl + '&configurationType=WebServiceConfiguration' + '&systemKeyword=' + systemName
        return (gazelleBaseUrl + '&configurationType=WebServiceConfiguration' + '&systemKeyword=' + systemName).toURL().text
    }

    String getV2Responder() {
        println "Pulling configs from " + gazelleBaseUrl + '&configurationType=HL7V2ResponderConfiguration'
        return (gazelleBaseUrl + '&configurationType=HL7V2ResponderConfiguration').toURL().text
    }

    String getOIDs() {
        println "Pulling OIDs from " + gazelleBaseUrl + '&configurationType=WebServiceConfiguration'
        return (gazelleBaseUrl + '&oid=true').toURL().text
    }

    def initTls() {
        new XdsTestServiceManager(null).setGazelleTruststore()
    }

}
