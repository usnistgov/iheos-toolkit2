package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.simcommon.client.SimId

/**
 * API for working with SimDb Events
 *
 * A Marker is an event inserted into the event log of each listed
 * simulator.  This collection of events all have the same time stamp.
 * A marker set before some single- or multi-sim operation enables the
 * searching for all events created by that operation.
 *
 * This can work even for events between two vendor systems by placing a
 * SimProxy between the two systems.
 */
class SimDbEvents {
    List<SimId> simIds

    /**
     *
     * @param simIds - collection of simulators being searched
     */
    SimDbEvents(List<SimId> simIds) {
        this.simIds = simIds
    }

    /**
     * Set marker in all gathered sims.  Guarenteed to have same timestamp
     * on all the marker events
     */
    void createMarker() {
        if (simIds.size() == 0) return
        SimId first = simIds.get(0)
        List<SimId> rest = simIds.subList(1, simIds.size())

        SimDb firstDb = SimDb.createMarker(first)
        rest.each { new SimDb(it).mirrorEvent(firstDb, SimDb.MARKER, SimDb.MARKER)}
    }

    /**
     * gather all events since last marker
     * @return
     */
    List<SimDbEvent> getEventsSinceMarker() {
        if (simIds.size() == 0) return []
        SimId first = simIds.get(0)
        List<SimId> rest = simIds.subList(1, simIds.size())
        def events = new SimDb(first).getEventsSinceMarker()
        rest.each { events.addAll(new SimDb(it).getEventsSinceMarker())}
        return events

    }
}
