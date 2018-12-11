package war.toolkitx.testkit.plugins.SoapAssertion

import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.soap.AbstractSoapValidater

/**
 * Runs an ExpectedContent validater through this plugin. @see Validator#run_test_assertions.
 */
class DummyValidater extends AbstractSoapValidater {
    String testAttribute;

    DummyValidater() {
        filterDescription = 'Runs a DummyValidater through this plugin.'
    }

    @Override
    ValidaterResult validate(SoapSimulatorTransaction sst) {

        new ValidaterResult(sst, this, true)
    }
}