package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig

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

    String getOIDs() {
        println "Pulling OIDs from " + gazelleBaseUrl + '&configurationType=WebServiceConfiguration'
        return (gazelleBaseUrl + '&oid=true').toURL().text
    }

    def initTls() {
        new XdsTestServiceManager(null).setGazelleTruststore()
    }

}
