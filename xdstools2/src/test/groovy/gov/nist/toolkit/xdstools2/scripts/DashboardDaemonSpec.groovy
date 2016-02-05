package gov.nist.toolkit.xdstools2.scripts

import spock.lang.Specification

/**
 *
 */
class DashboardDaemonSpec extends Specification {

    def 'run' () {
        when:
        def pid = '911^^^&1.3.6.1.4.1.21367.13.20.1000&ISO'
        def warHome = '/Users/bill/t2/xdstools2/src/test/resources/war'
        def outDir = '/Users/bill/tmp/toolkit2/Dashboard'
        def env = 'NA2016'
        def ec = '/Users/bill/tmp/toolkit2'
        DashboardDaemon dd = new DashboardDaemon(warHome, outDir, env, ec)
        dd.run(pid)

        then:
        true
    }
}
