package gov.nist.toolkit.itTests.cat.homeCommunityId

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.itSupport.xc.GatewayBuilder
import gov.nist.toolkit.results.client.TestLogs
import gov.nist.toolkit.toolkitApi.DocumentConsumer
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
import spock.lang.Shared
/**
 * Initialize toolkit to support XDS-XCA-I_homeCommunityID test
 */
class InitializeForCat extends ToolkitSpecification {
    @Shared SimulatorBuilder simBuilder
    @Shared String pid = 'P20170106143728.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO'
    @Shared SimConfig rrConfig
    @Shared String testSession = 'cat';
    @Shared String envName = 'default'
    @Shared DocumentConsumer docCons
    @Shared TestLogs repTestLogs


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        Installation.instance().externalCache(new File('/Users/bill/tmp/toolkit2a'))

        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)

        // Connect to remote API
        simBuilder = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        System.gc()
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {}

    def 'build ig and rg sim'() {

        when: 'delete old simulators'
        simBuilder.delete('rg', testSession)
        simBuilder.delete('ig', testSession)

        then:
        true

        when: 'build and link simulators'
        def iConfig, rConfigs

        // builds IG and RG and loads regrep behing RG with two documents
        (iConfig, rConfigs) = GatewayBuilder.build(api, simBuilder, 1, 'cat', 'default', pid)
        SimConfig igConfig = iConfig
        SimConfig[] rgConfigs = rConfigs
        SimConfig rgConfig = rgConfigs[0]
        def igId = igConfig.id
        def rgId = rgConfig.id
        igConfig.setProperty(SimulatorProperties.locked, true)
        simBuilder.update(igConfig)

        then:
        true

        when: 'lock rg'
        rgConfig.setProperty(SimulatorProperties.locked, true)
        simBuilder.update(rgConfig)

        then:
        true

//        when:
//        InitiatingGateway ig = simBuilder.asInitiatingGateway(igConfig)
//        RefList rlr = ig.FindDocuments(pid)
//
//        then:
//        rlr.refs.size() == 1
    }
}
