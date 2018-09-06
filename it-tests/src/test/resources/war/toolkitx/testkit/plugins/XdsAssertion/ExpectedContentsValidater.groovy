package war.toolkitx.testkit.plugins.XdsAssertion


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
class ExpectedContentsValidater extends AbstractSoapValidater {
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
    ValidaterResult validate(SoapSimulatorTransaction sst) {
        boolean requestMatch = false
        boolean responseMatch = false
        if (!requestMsgExpectedContent || !responseMsgExpectedContent) {
            String illegalArg = "Either requestMsgExpectedContent attribute or responseMsgExpectedContent attribute must be specified. See Validator#run_test_assertion for a list of codes."
            error(illegalArg)
            throw new IllegalArgumentException(illegalArg)
        }
        if (requestMsgExpectedContent && sst) {
            //          For Debugging only -- this log creates too many messages
//            log("Processing request from eventId: ${transactionInstance?.simDbEvent?.eventId} simLogUrl: ${transactionInstance?.simDbEvent?.simLogUrl}")
            if (sst.requestBody) {
                Metadata m = MetadataParser.parseNonSubmission(Util.parse_xml(sst.requestBody))
                Validator v = new Validator().setM(m)
                v.run_test_assertion(requestMsgExpectedContent, Integer.parseInt(requestMsgECCount?:"-1"))
                String errors = v.getErrors()

                if (errors.length() > 0) {
                    error("Request", errors)
                }
                requestMatch = sst.request instanceof String && !isErrors()
            } else {
                error("Request","Null transactionInstance or its request body is null")
            }
        }

        if (responseMsgExpectedContent && sst) {
//          For Debugging only -- this log creates too many messages
//  log("Processing response from eventId: ${transactionInstance?.simDbEvent?.eventId} simLogUrl: ${transactionInstance?.simDbEvent?.simLogUrl}")
            if (sst.responseBody) {
                OMElement regresp = Util.parse_xml(sst.responseBody)

                Metadata m = new Metadata(regresp, false, false)
                Validator v = new Validator().setM(m)
                v.run_test_assertion(responseMsgExpectedContent, Integer.parseInt(responseMsgECCount?:"-1"))
                String errors = v.getErrors()

                if (errors.length() > 0) {
                    error("Response", errors)
                }
                responseMatch = sst.response instanceof String && !isErrors()
            } else {
                error("Response", "Null transactionInstance or its response body is null")
            }
        }

        boolean match = false
        if (requestMsgExpectedContent && responseMsgExpectedContent) {
            match = requestMatch && responseMatch
        } else if (requestMsgExpectedContent) {
            match = requestMatch
        } else if (responseMsgExpectedContent) {
            match = responseMatch
        }


        new ValidaterResult(sst, this, match)
    }

}
