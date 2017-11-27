package gov.nist.toolkit.simcommon.server

import spock.lang.Specification

class SimEndpointTest extends Specification {
    def y= '/fsim/bill__mhd/mhddocrec/fhir/pdb'
    def x = '/fsim/bill__mhd/mhddocrec/fhir/DocumentReference?patient.identifier=urn:oid:1.2.3.4.5.6%7CMRN'

    def 'pdb'() {
        when:
        def endpoint = '/fsim/bill__mhd/mhddocrec/fhir/pdb'
        SimEndpoint ep = new SimEndpoint(endpoint)

        then:
        ep.actorType == 'mhddocrec'
        ep.transactionType == 'pdb'
        ep.simIdString == 'bill__mhd'
    }


    def 'fdr'() {
        when:
        def endpoint = '/fsim/bill__mhd/mhddocrec/fhir/DocumentReference?patient.identifier=urn:oid:1.2.3.4.5.6%7CMRN'
        SimEndpoint ep = new SimEndpoint(endpoint)

        then:
        ep.actorType == 'mhddocrec'
        ep.transactionType == 'fhir'
        ep.simIdString == 'bill__mhd'
        ep.resourceType == 'DocumentReference'
        ep.query == 'patient.identifier=urn:oid:1.2.3.4.5.6%7CMRN'
    }
}
