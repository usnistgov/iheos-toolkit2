package gov.nist.toolkit.actorfactory.client

import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.PidBuilder
import gov.nist.toolkit.configDatatypes.client.PidSet
import spock.lang.Specification

/**
 * Created by bill on 9/23/15.
 */
public class PidSetTest extends Specification {

    def 'Zero'() {
        when:
        Set<Pid> pids = new HashSet<>()
        PidSet pidSet = new PidSet(pids)
        String parsableString = pidSet.asParsableString()
        PidSet pidSet2 = new PidSet(parsableString)

        then:
        pidSet.pids.size() == 0
        parsableString == '[]'
        pidSet == pidSet2
    }

    def 'One'() {
        when:
        Set<Pid> pids = new HashSet<>()
        String pid1Str = 'P1^^^&1.2&ISO'
        pids.add(PidBuilder.createPid(pid1Str))
        PidSet pidSet = new PidSet(pids)
        String parsableString = pidSet.asParsableString()
        PidSet pidSet2 = new PidSet(parsableString)

        then:
        pidSet.pids.size() == 1
        parsableString != '[]'
        pidSet == pidSet2
    }

    def 'Two'() {
        when:
        Set<Pid> pids = new HashSet<>()
        String pid1Str = 'P1^^^&1.2&ISO'
        String pid2Str = 'P2^^^&1.2&ISO'
        pids.add(PidBuilder.createPid(pid1Str))
        pids.add(PidBuilder.createPid(pid2Str))
        PidSet pidSet = new PidSet(pids)
        String parsableString = pidSet.asParsableString()
        PidSet pidSet2 = new PidSet(parsableString)

        then:
        pidSet.pids.size() == 2
        parsableString != '[]'
        pidSet == pidSet2
    }

    def 'Dup'() {
        when:
        Set<Pid> pids = new HashSet<>()
        String pid1Str = 'P1^^^&1.2&ISO'
        String pid2Str = 'P2^^^&1.2&ISO'
        pids.add(PidBuilder.createPid(pid1Str))
        pids.add(PidBuilder.createPid(pid2Str))
        pids.add(PidBuilder.createPid(pid2Str))
        PidSet pidSet = new PidSet(pids)
        String parsableString = pidSet.asParsableString()
        PidSet pidSet2 = new PidSet(parsableString)

        then:
        pidSet.pids.size() == 2
        parsableString != '[]'
        pidSet == pidSet2
    }
}
