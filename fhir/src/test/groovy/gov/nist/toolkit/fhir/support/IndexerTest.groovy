package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.testSupport.InitEC
import spock.lang.Specification

/**
 *
 */
class IndexerTest extends Specification {
    SimId simId = new SimId('fhir')

    def setup() {
        InitEC.init()
        new ResDb(simId).delete()  // delete simuilator
        new ResDb().mkSim(simId)   // build new one
    }

    def 'index single resource'() {
        when: '''create data'''
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'Patient'))

        and:  'index it'
        new SimIndexer(simId).create()

        and: '''search'''
        SimIndexer index = new SimIndexer(simId).open()
        List<String> paths = index.lookupByTypeAndId('Patient', 'foo')
        println "found path is ${paths[0]}"

        then:
        paths.size() == 1
    }

    def 'index multiple resources from one event'() {
        when: '''create data'''
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'Patient'))
        resDb.storeNewResource('simple', buildResource('bar', 'Patient'))
        resDb.storeNewResource('simple', buildResource('bar', 'Patient'))

        and: 'index it'
        new SimIndexer(simId).create()

        and: '''get searchable index'''
        SimIndexer index = new SimIndexer(simId).open()

        then:
        index.lookupByTypeAndId('Patient', 'foo').size() == 1
    }

    def 'search based only on type'() {
        when: '''create data'''
        ResDb resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'Patient'))
        resDb.storeNewResource('simple', buildResource('bar', 'Patient'))
        resDb.storeNewResource('simple', buildResource('bar', 'Base'))

        and: 'index it'
        new SimIndexer(simId).create()

        and: '''get searchable index'''
        SimIndexer index = new SimIndexer(simId).open()

        then:
        index.lookupByTypeAndId('Patient', null).size() == 2
    }

    def 'index resources from multiple events'() {
        when: '''create data'''
        ResDb resDb
        resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('foo', 'Patient'))

        resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('bar', 'Patient'))

        resDb = new ResDb(simId, ResDb.BASE_TYPE, ResDb.STORE_TRANSACTION)
        resDb.storeNewResource('simple', buildResource('bar', 'Base'))

        and: 'index it'
        new SimIndexer(simId).create()

        and: '''get searchable index'''
        SimIndexer index = new SimIndexer(simId).open()

        then:
        index.lookupByTypeAndId('Patient', 'foo').size() == 1
    }

    def buildResource(String id, String type) {
        "{ \"id\": \"${id}\" ,  \"resourceType\": \"${type}\" }"
    }


}
