package gov.nist.toolkit.itTests.plugins

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.services.server.UnitTestEnvironmentManager
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.testengine.engine.fhirValidations.AbstractFhirValidater
import gov.nist.toolkit.testengine.engine.fhirValidations.FhirAssertionLoader
import gov.nist.toolkit.testengine.engine.fhirValidations.SimReference
import gov.nist.toolkit.testengine.engine.fhirValidations.ValidaterResult
import gov.nist.toolkit.testkitutilities.TestKitSearchPath
import spock.lang.Shared
import spock.lang.Specification

class FhirAssertionLoaderSpec extends Specification {
    @Shared Session session
    @Shared TestKitSearchPath path
    @Shared FhirAssertionLoader loader
    @Shared SimId simId
    @Shared TransactionType tType
    @Shared SimReference simReference

    def setupSpec() {
        Installation.instance().setServletContextName("");
        session = UnitTestEnvironmentManager.setupLocalToolkit()
        path = new TestKitSearchPath('default', TestSession.DEFAULT_TEST_SESSION)
        loader = new FhirAssertionLoader(path)
        simId = SimIdFactory.simIdBuilder('default__foo')
        tType = TransactionType.ANY
        simReference = new SimReference(simId, tType)
    }

    def 'Loader Test True' () {
        when:
        Class claz = loader.loadFile('AllTrue.groovy')

        then:
        claz

        when:
        AbstractFhirValidater val = (AbstractFhirValidater) claz.newInstance(simReference, "True Test")
        FhirSimulatorTransaction tr = new FhirSimulatorTransaction(simId, tType)
        ValidaterResult result = val.validate(tr)

        then:
        result.match
    }

    def 'Loader Test False' () {
        when:
        Class claz = loader.loadFile('AllFalse.groovy')

        then:
        claz

        when:
        AbstractFhirValidater val = (AbstractFhirValidater) claz.newInstance(simReference, "False Test")
        FhirSimulatorTransaction tr = new FhirSimulatorTransaction(simId, tType)
        ValidaterResult result = val.validate(tr)

        then:
        !result.match
    }

    def 'Loader Test with direct parameter'() {
        when:
        Class claz = loader.loadFile('StatusValidater.groovy')

        then:
        claz

        when:
        AbstractFhirValidater val = (AbstractFhirValidater) claz.newInstance('300')

        then:
        val.code == 300

    }

    def 'Loader Test with array parameter'() {
        when:
        Class claz = loader.loadFile('StatusValidater.groovy')

        then:
        claz

        when:
        val = (AbstractFhirValidater) claz.newInstance(['300'] as Object[])

        then:
        val.code == 300
    }

    def 'Loader Test with map parameter'() {
        when:
        Class claz = loader.loadFile('StatusValidater.groovy')

        then:
        claz

        when:
        def parms = [statusCode: '300']
        AbstractFhirValidater val = (AbstractFhirValidater) claz.newInstance(parms)

        then:
        val.statusCode == '300'

    }
}
