package gov.nist.toolkit.itTests.xc

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.itSupport.xc.GatewayBuilder
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrymetadata.client.MetadataCollection
import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.results.client.CodesConfiguration
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType
import spock.lang.Shared

/**
 * Cross community query to multiple communities
 */
class MultiQuerySpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def patientId = 'br14^^^&1.2.3&ISO'

    def 'single rg'() {
        when:
        def igConfig
        def rgConfigs
        (igConfig, rgConfigs) = GatewayBuilder.build(api, spi, 1, 'bill', 'test', patientId)
        def igSite = new SiteSpec(igConfig.fullId, ActorType.INITIATING_GATEWAY, null)

        Map<String, List<String>> selectedCodes = new HashMap<>()
        selectedCodes.put(CodesConfiguration.DocumentEntryStatus, [MetadataSupport.statusType_approved])
        selectedCodes.put(CodesConfiguration.ReturnsType, [QueryReturnType.LEAFCLASS.getReturnTypeString()])
        List<Result> results = api.findDocuments(igSite, patientId, selectedCodes)

        then:
        results.size() == 1
        results.get(0).passed()

        when:
        List<MetadataCollection> metadataCollections = results.get(0).getMetadataContent()

        then:
        metadataCollections.size() == 1
        metadataCollections.get(0).docEntries.size() == 2
    }

    def 'two rgs'() {
        when:
        def igConfig
        def rgConfigs
        (igConfig, rgConfigs) = GatewayBuilder.build(api, spi, 2, 'bill', 'test', patientId)
        def igSite = new SiteSpec(igConfig.fullId, ActorType.INITIATING_GATEWAY, null)

        Map<String, List<String>> selectedCodes = new HashMap<>()
        selectedCodes.put(CodesConfiguration.DocumentEntryStatus, [MetadataSupport.statusType_approved])
        selectedCodes.put(CodesConfiguration.ReturnsType, [QueryReturnType.LEAFCLASS.getReturnTypeString()])
        List<Result> results = api.findDocuments(igSite, patientId, selectedCodes)

        then:
        results.size() == 1
        results.get(0).passed()

        when:
        List<MetadataCollection> metadataCollections = results.get(0).getMetadataContent()

        then:
        metadataCollections.size() == 1
        metadataCollections.get(0).docEntries.size() == 4
    }

}
