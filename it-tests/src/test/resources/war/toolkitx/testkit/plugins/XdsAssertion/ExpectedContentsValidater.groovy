package war.toolkitx.testkit.plugins.XdsAssertion


import gov.nist.toolkit.testengine.engine.SimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.xds.AbstractXdsValidater

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.testengine.engine.Validator

import gov.nist.toolkit.valsupport.client.ValidationContext
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.valregmsg.message.RegistryResponseValidator
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.factories.TextErrorRecorderBuilder
import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine

import org.apache.axiom.om.OMElement


/**
 * Runs an ExpectedContent validater through this plugin. @see Validator#run_test_assertions.
 */
class ExpectedContentsValidater extends AbstractXdsValidater {
    /**
     * Required parameter
     */
    String requestMsgExpectedContent
    /**
     * Optional parameter
     */
    String requestMsgECCount
    /**
     * Required parameter
     */
    String responseMsgExpectedContent
    /**
     * Optional parameter
     */
    String responseMsgECCount

    ExpectedContentsValidater() {
        filterDescription = 'Runs an ExpectedContent validater (Validator#run_test_assertions) through this plugin.'
    }


    @Override
    ValidaterResult validate(SimulatorTransaction transactionInstance) {
        boolean match
        if (!requestMsgExpectedContent || !responseMsgExpectedContent) {
            throw new IllegalArgumentException("Either requestMsgExpectedContent attribute or responseMsgExpectedContent attribute must be specified. See Validator#run_test_assertion for a list of codes.")
        }
        if (requestMsgExpectedContent) {
            Metadata m = MetadataParser.parseNonSubmission(Util.parse_xml(transactionInstance.requestBody))
            Validator v = new Validator().setM(m)
            v.run_test_assertion(requestMsgExpectedContent, Integer.parseInt(requestMsgECCount?:"-1"))
            String errors = v.getErrors()

            if (errors.length() > 0) {
                error(errors)
            }
            match = transactionInstance.request instanceof String && !isErrors()
        }

        if (match && responseMsgExpectedContent) {
            OMElement regresp = Util.parse_xml(transactionInstance.responseBody)

            Metadata m = new Metadata(regresp, false, false)
            Validator v = new Validator().setM(m)
            v.run_test_assertion(responseMsgExpectedContent, Integer.parseInt(responseMsgECCount?:"-1"))
            String errors = v.getErrors()

            if (errors.length() > 0) {
                error(errors)
            }
            match = transactionInstance.response instanceof String && !isErrors()
        }

        new ValidaterResult(transactionInstance, this, match)
    }

}

