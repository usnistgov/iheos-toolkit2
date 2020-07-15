package gov.nist.toolkit.itTests.xdm

import gov.nist.toolkit.itTests.support.ToolkitSpecification
import gov.nist.toolkit.toolkitApi.SimulatorBuilder
import gov.nist.toolkit.toolkitApi.XdmValidator
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmItem
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmReport
import gov.nist.toolkit.toolkitServicesCommon.resource.xdm.XdmRequestResource
import spock.lang.Shared

import java.nio.file.Paths

/**
 *
 */
class ValidateXdmSpec extends ToolkitSpecification {
    @Shared SimulatorBuilder spi

    @Shared String urlRoot = String.format("http://localhost:%s/xdstools2", remoteToolkitPort)

    def setupSpec() {   // one time setup done when class launched
        startGrizzly('8889')

        // Initialize remote api for talking to toolkit on Grizzly
        // Needed to build simulators
        spi = getSimulatorApi(remoteToolkitPort)
    }

    def cleanupSpec() {  // one time shutdown when everything is done
//        System.gc()
//        server.stop()
//        ListenerFactory.terminateAll()
    }

    def setup() {}

    def 'validate ccda xdm'() {
        when:
        byte[] bytes = Paths.get(this.getClass().getResource('/').toURI()).resolve('testdata/xdm/Ccda.zip').toFile().bytes
        XdmRequestResource request = new XdmRequestResource()
        request.zip = bytes
        XdmValidator validator = spi.createXdmValidator()
        XdmReport report = validator.validate(request)
        println report.report
        report.items.each { XdmItem item ->
            println item.path
        }

        then:
        !report.pass
        report.report.contains('Filename is limited to 8 characters')
    }

}
