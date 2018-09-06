package war.toolkitx.testkit.plugins.XdsAssertion

import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDbEvent
import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.xds.AbstractSoapValidater

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.testengine.engine.Validator
import org.apache.axiom.om.OMElement


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