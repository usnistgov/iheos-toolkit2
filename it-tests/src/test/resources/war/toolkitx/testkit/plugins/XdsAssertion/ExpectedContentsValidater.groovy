package war.toolkitx.testkit.plugins.XdsAssertion

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.xds.AbstractXdsValidater
import gov.nist.toolkit.testengine.engine.Validator
import gov.nist.toolkit.testengine.transactions.BasicTransaction
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.utilities.xml.XmlUtil
import org.apache.axiom.om.OMElement

/**
 * Runs an ExpectedContent validater through this plugin. @see Validator#run_test_assertions.
 */
class ExpectedContentsValidater extends AbstractXdsValidater {
    String expectedContentCode
    String count

    ExpectedContentsValidater() {
        filterDescription = 'Runs an ExpectedContent validater (Validator#run_test_assertions) through this plugin.'
    }

    @Override
    ValidaterResult validate(SimulatorTransaction transactionInstance) {

        if (expectedContentCode != null ) {

            Metadata m = MetadataParser.parseNonSubmission(Util.parse_xml(transactionInstance.request))
            Validator v = new Validator().setM(m)
            v.run_test_assertion(expectedContentCode, Integer.parseInt(count?:"-1"))
            String errors = v.getErrors()

            if (errors.length() > 0) {
                error(errors)
            }
        } else {
            throw new IllegalArgumentException("ecCode attribute value must specify one of the Validator codes. See Validator#run_test_assertion for a list of codes.")
        }

        boolean match = transactionInstance.request instanceof String && !isErrors()
        new ValidaterResult(transactionInstance, this, match)
    }

}

