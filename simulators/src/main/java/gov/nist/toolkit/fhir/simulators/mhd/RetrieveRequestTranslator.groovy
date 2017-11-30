package gov.nist.toolkit.fhir.simulators.mhd

import groovy.xml.MarkupBuilder

class RetrieveRequestTranslator {

    // model is [documentUniqueId: [repositoryUniqueId, homeCommunityId]]
    static String toXml(Map theModel) {

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
//xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
        xml.'xds:RetrieveDocumentSetRequest'('xmlns:xds':'urn:ihe:iti:xds-b:2007') {
            theModel.each { documentUniqueId, location ->
                def (repositoryUniqueId, homeCommunityId) = location
                'xds:DocumentRequest'() {
                    'xds:RepositoryUniqueId'(repositoryUniqueId)
                    'xds:DocumentUniqueId'(documentUniqueId)
                    if (homeCommunityId)
                        'xds:HomeCommunityId'(homeCommunityId)
                }
            }
        }

//        <RetrieveDocumentSetRequest xmlns="urn:ihe:iti:xds-b:2007">
//          <DocumentRequest>
//            <RepositoryUniqueId>1.19.6.24.109.42.1.5</RepositoryUniqueId>
//            <DocumentUniqueId>1.42.20101110141555.15</DocumentUniqueId>
//          </DocumentRequest>
//        </RetrieveDocumentSetRequest>
        return writer.toString()
    }
}
