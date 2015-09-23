package gov.nist.toolkit.actorfactory.client

import spock.lang.Specification

/**
 * Created by bill on 9/23/15.
 */
class PidTest extends Specification {

    def 'Simple parse test'() {
        when:
        String ad = "1.2.3"
        String id = "P45"
        Pid pid1 = new Pid(ad, id);
        String pid1String = pid1.asParsableString();
        Pid pid2 = PidBuilder.createPid(pid1String)

        then:
        pid1.ad.equals(ad)
        pid1.id.equals(id)
        pid1.equals(pid2)
        pid1.validate()
        pid2.validate()
    }

    def 'Null parse test1'() {
        when:
        String ad = null
        String id = "P45"
        Pid pid1 = new Pid(ad, id);
        String pid1String = pid1.asParsableString();
        Pid pid2 = PidBuilder.createPid(pid1String)

        then:
        pid1.ad.equals(ad)
        pid1.id.equals(id)
        pid1.equals(pid2)
        !pid1.validate()
        !pid2.validate()
    }

    def 'Null parse test2'() {
        when:
        String ad = "1.2.3"
        String id = null
        Pid pid1 = new Pid(ad, id);
        String pid1String = pid1.asParsableString();
        Pid pid2 = PidBuilder.createPid(pid1String)

        then:
        pid1.ad.equals(ad)
        pid1.id.equals(id)
        pid1.equals(pid2)
        !pid1.validate()
        !pid2.validate()
    }

    def 'Extra parse test1'() {
        when:
        String ad = '1.2.3'
        String id = 'P38'
        String pidString = "${id}^^^&${ad}&ISO^PI"
        Pid pid = PidBuilder.createPid(pidString)
        String pidString2 = pid.asParsableString();
        Pid pid2 = PidBuilder.createPid(pidString2)

        then:
        ad.equals(pid.ad)
        id.equals(pid.id)
        pid.validate()
        pid.equals(pid2)
        !pidString.equals(pidString2)  // loose ^PI on end - not XDS-ish
    }

    def 'Whitespace parse test'() {
        when:
        String ad = "1.2.3"
        String id = "P45"
        Pid pid1 = new Pid(' ' + ad + ' ', ' ' + id + ' ');
        String pid1String = pid1.asParsableString();
        Pid pid2 = PidBuilder.createPid(pid1String)

        then:
        pid1.ad.equals(ad)
        pid1.id.equals(id)
        pid1.equals(pid2)
        pid1.validate()
        pid2.validate()
    }

    def 'Extra parse test2'() {
        when:
        String ad = "1.2.3"
        String id = "P45"
        String extra = 'Walter Reed'
        Pid pid1 = new Pid(ad, id);
        pid1.setExtra(extra)
        String pid1String = pid1.asParsableString();
        Pid pid2 = PidBuilder.createPid(pid1String)

        then:
        pid1.ad.equals(ad)
        pid1.id.equals(id)
        pid1.equals(pid2)
        pid1.validate()
        pid2.validate()
        pid1.asParsableString().contains(extra)
    }

    def 'Contains test'() {
        when:
        Pid pid1 = PidBuilder.createPid('P45^^^&1.2&ISO')
        Pid pid2 = PidBuilder.createPid(pid1.asParsableString())
        List<Pid> pids = new ArrayList<>()
        pids.add(pid1)

        then:
        pids.contains(pid1)
        pids.contains(pid2)
    }

}
