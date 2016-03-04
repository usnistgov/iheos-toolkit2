package gov.nist.toolkit.itTests.xds

import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.adt.ListenerFactory
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.registrysupport.MetadataSupport
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.results.client.TestLogs
import gov.nist.toolkit.tookitApi.DocumentConsumer
import gov.nist.toolkit.tookitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitServicesCommon.*
import gov.nist.toolkit.toolkitServicesCommon.resource.QueryParametersResource
import gov.nist.toolkit.toolkitServicesCommon.resource.RetrieveRequestResource
import gov.nist.toolkit.toolkitServicesCommon.resource.StoredQueryRequestResource
import spock.lang.Shared
/**
 *
 */
class ConsumerSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder simBuilder
    @Shared Pid pid = new Pid('1.2.360','BR14')
    @Shared SimConfig rrConfig
    @Shared String testSession = 'bill';
    @Shared String envName = 'test'
    @Shared DocumentConsumer docCons
    @Shared TestLogs repTestLogs


    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        println "EC is ${Installation.installation().externalCache().toString()}"
        println "${api.getSiteNames(true)}"
        api.createTestSession(testSession)

        // Connect to remote API
        simBuilder = getSimulatorApi(remoteToolkitPort)

        simBuilder.delete('rr', testSession)
        rrConfig = simBuilder.createDocumentRegRep(
                'rr',
                testSession,
                envName)

        initializeRegistryWithPatientId(testSession, rrConfig, pid)

        repTestLogs = initializeRepository(testSession, rrConfig, pid, new TestInstance('11966'))

        // create document consumer sim
        simBuilder.delete('dc', testSession)
        docCons = simBuilder.createDocumentConsumer('dc', testSession, envName)

        // configure Doc Consumer to point to registry/repository sim
        docCons.setProperty(SimulatorProperties.storedQueryEndpoint, rrConfig.asString(SimulatorProperties.storedQueryEndpoint))
        docCons.setProperty(SimulatorProperties.retrieveEndpoint, rrConfig.asString(SimulatorProperties.retrieveEndpoint))
        docCons.update(docCons.getConfig())
    }

    def cleanupSpec() {  // one time shutdown when everything is done
        server.stop()
        ListenerFactory.terminateAll()
    }

    def setup() {}

    def 'find documents query - returns no patient id parameter error'() {
        when:
        QueryParametersResource parameters = new QueryParametersResource();

        StoredQueryRequestResource sqRequest = new StoredQueryRequestResource()
        sqRequest.id = 'dc'
        sqRequest.user = testSession
        sqRequest.queryId = StoredQueryRequest.FindDocuments
        sqRequest.queryParameters = parameters
        sqRequest.tls = false

        LeafClassRegistryResponse response = docCons.queryForLeafClass(sqRequest)
        if (response.status != ResponseStatusType.SUCCESS) {
            println 'ErrorList is ' + response.errorList
        }

        then:
        response.getStatus() == ResponseStatusType.FAILURE
        response.errorList.size() > 0
        response.leafClasses.size() == 0
    }

    def 'find documents query - correctly'() {
        when:
        QueryParametersResource parameters = new QueryParametersResource();
        parameters.addParameter(QueryParametersValueSet.XDSDocumentEntryPatientId, pid.asString())
        parameters.addParameter(QueryParametersValueSet.XDSDocumentEntryStatus, MetadataSupport.statusType_approved)

        StoredQueryRequestResource sqRequest = new StoredQueryRequestResource()
        sqRequest.id = 'dc'
        sqRequest.user = testSession
        sqRequest.queryId = StoredQueryRequest.FindDocuments
        sqRequest.setQueryParameters(parameters)
        sqRequest.tls = false

        LeafClassRegistryResponse response = docCons.queryForLeafClass(sqRequest)
        if (response.status != ResponseStatusType.SUCCESS) {
            println 'ErrorList is ' + response.errorList
        }

        then:
        response.getStatus() == ResponseStatusType.SUCCESS
        response.errorList.size() == 0
        response.leafClasses.size() == 1
    }

    def 'retrieve' () {
        when:
        def docUid = repTestLogs.getTestLog(1).assignedUids.get('Document01')
        def repUid = rrConfig.asString(SimulatorProperties.repositoryUniqueId)

        then:
        docUid
        repUid

        when:
        RetrieveRequestResource requestResource = new RetrieveRequestResource()
        requestResource.repositoryUniqueId = repUid
        requestResource.documentUniqueId = docUid

        RetrieveResponse retResponse = docCons.retrieve(requestResource)

        then:
        true
    }

}
