package gov.nist.toolkit.fhir.support

import gov.nist.toolkit.actorfactory.Event
import gov.nist.toolkit.actorfactory.PerEvent
import gov.nist.toolkit.actorfactory.client.NoSimException
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.utilities.io.Io
import groovy.json.JsonSlurper

/**
 * Create Lucene index of a simulator
 */
class SimIndexer {
    File indexFile
    Indexer indexer

    def index(SimId simId) {
        if (!new ResDb(simId).isSim())
            throw new NoSimException('Sim ${simId} does not exist')
        indexFile = ResDb.getIndexFile(simId)
        ResDb resDb = new ResDb(simId)

        indexer = new Indexer(indexFile)
        resDb.perEvent(null, null, new EventHandler())
        indexer.finish()
    }

    class EventHandler implements PerEvent {

        @Override
        void event(SimId simId, ActorType actorType, TransactionType transactionType, File eventDir) {
            eventDir.listFiles().each { File jsonFile ->
                if (!jsonFile.name.endsWith('json')) return
                def slurper = new JsonSlurper()
                def resource = slurper.parseText(jsonFile.text)
                String type = resource.resourceType
                String id = resource.id
                if (!id) return
                if (!type) return
                Event event = new Event(actorType, transactionType, eventDir.name)
                indexer.addResource(new ResourceIndexItem('id', id, event.asPath()))
                indexer.addResource(new ResourceIndexItem('type', type, event.asPath()))
            }
        }
    }

    static delete(SimId simId) {
        if (!new ResDb(simId).isSim())
            return
        File index = ResDb.getIndexFile(simId)
        Io.delete(index)
    }
}
