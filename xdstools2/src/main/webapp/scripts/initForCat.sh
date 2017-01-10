#!/bin/bash
//usr/bin/env groovy "$0" $@; exit $?

println 'Starting...'

import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.xc.GatewayBuilder
import gov.nist.toolkit.results.client.TestLogs
import gov.nist.toolkit.toolkitApi.DocumentConsumer
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.SimConfig
/**
 * Initialize toolkit to support XDS-XCA-I_homeCommunityID test
 */
    SimulatorBuilder simBuilder
    String pid = 'P20170106143728.2^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO'
    SimConfig rrConfig
     String testSession = 'cat';
     String envName = 'default'
     DocumentConsumer docCons
     TestLogs repTestLogs


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        Installation.instance().externalCache(new File('/home/bill/tmp/toolkit2a'))

        println "EC is ${Installation.instance().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)

        // Connect to remote API
        simBuilder = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def buildIgandRgSim() {

        simBuilder.delete('rg', testSession)
        simBuilder.delete('ig', testSession)

        // 'build and link simulators'
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

        // 'lock rg'
        rgConfig.setProperty(SimulatorProperties.locked, true)
        simBuilder.update(rgConfig)

    }

setupSpec()
buildIgandRgSim()
cleanupSpec()

