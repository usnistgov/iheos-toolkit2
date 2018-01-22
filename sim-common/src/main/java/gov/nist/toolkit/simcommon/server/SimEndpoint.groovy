package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.configDatatypes.client.FhirVerb
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.server.Installation
import org.apache.http.HttpHost

/**
 *
 */
class SimEndpoint {
    String schemeName = 'http'
    String hostName
    int port = 80
    String service
    String simServiceType = 'sim'
    String actorType
    String transactionTypeName
    String simIdString
    String resourceType = null
    String query = null
    String id = null
    String baseAddress = null
    FhirVerb fhirVerb = FhirVerb.NONE
    TransactionType transactionType

    def resourceNames = [
            'DocumentReference',
            'DocumentManifest',
            'Binary'
     //       'Patient'
    ]

    String toString() {
        "service=${service} actorType=${actorType} transactionType=${transactionType} simIdString=${simIdString} resourceType=${resourceType} fhirVerb=${fhirVerb}"
    }

    SimEndpoint(String endpoint) {
        assert endpoint
        assert endpoint.indexOf(' ') == -1
        int i;
        i = endpoint.indexOf('://')
        if (i > -1) {
            schemeName = endpoint.substring(0, i)
            i += 3
        } else i = 0
        service = endpoint.substring(i)
        assert service.size() > 0
        i = service.indexOf('/')
        if (i == -1)
            throw new InvalidSimEndpointException(endpoint, 'Cannot find delimiting /')
        hostName = Installation.instance().propertyServiceManager().getToolkitHost()
        setPort(Installation.instance().propertyServiceManager().getProxyPort())

        service = service.substring(i)
        String[] parts = service.substring(1).split('/')
        if (parts.size() < 3) throw new InvalidSimEndpointException(endpoint, 'Must be at least 3 / delimited parts.')
        int simStart = 0
        if (!parts[simStart].contains('sim')) simStart++
        simServiceType = parts[simStart]

        if (!simServiceType.contains('sim')) throw new InvalidSimEndpointException(endpoint, 'Service name must start with /sim/')
        List partsList = parts as List  // index beyond end with list -> returns null instead of exception
        simIdString = partsList[simStart+1]
        actorType = partsList[simStart+2]
        def contextName = Installation.instance().servletContextName
        if (contextName)
            baseAddress = "${schemeName}://${hostName}:${port}/${partsList.subList(0,4).join('/')}"
        else
            baseAddress = "${schemeName}://${hostName}:${port}/${partsList.subList(0,3).join('/')}"

        transactionTypeName = partsList[simStart+3]// with FHIR this is sometimes null
        if (!transactionTypeName)
            transactionTypeName = 'fhir'
        if (resourceNames.contains(transactionTypeName))
            id = partsList[simStart+4]
        def isQuery = service.contains('?')
        if (partsList.size() > simStart+3) {
            if (isQuery) {
                def resAndQuery = partsList[simStart+3]
                (resourceType, query) = resAndQuery.split('\\?', 2)
                transactionTypeName = resourceType
                fhirVerb = FhirVerb.QUERY
            }
            else {
                transactionTypeName = partsList[simStart + 3]
                if (!transactionTypeName)
                    transactionTypeName = 'fhir'
                if (resourceNames.contains(transactionTypeName))
                    fhirVerb = FhirVerb.READ
                else
                    fhirVerb = FhirVerb.TRANSACTION
            }
        }
        if (transactionTypeName){
            if (fhirVerb == FhirVerb.READ && transactionTypeName == 'DocumentReference')
                transactionType = TransactionType.READ_DOC_REF
            else  if (fhirVerb == FhirVerb.READ && transactionTypeName == 'Binary')
                transactionType = TransactionType.READ_BINARY
            else if (fhirVerb == FhirVerb.QUERY && transactionTypeName == 'DocumentReference')
                transactionType = TransactionType.FIND_DOC_REFS
            else
                transactionType = TransactionType.find(transactionTypeName, fhirVerb)
        }

        def x = 0
    }

    HttpHost getHost() {
        return new HttpHost(hostName, port, schemeName)
    }

    def setHostName(HttpHost host) {
        hostName = host.hostName
        port = host.port
        schemeName = host.schemeName
    }

    def setHostName(String hostname) {
        hostName = hostname
    }

    def setPort(String portStr) {
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException var7) {
            throw new IllegalArgumentException("Invalid HTTP port: " + portStr);
        }
    }
}
