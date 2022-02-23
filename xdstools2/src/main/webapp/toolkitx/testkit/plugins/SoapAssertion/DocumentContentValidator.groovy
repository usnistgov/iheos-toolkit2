package war.toolkitx.testkit.plugins.SoapAssertion

import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimDbEvent

import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.soap.AbstractSoapValidater
import gov.nist.toolkit.testengine.engine.Validator

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.registrymetadata.MetadataParser
import gov.nist.toolkit.utilities.xml.Util

import org.apache.axiom.om.OMElement
import java.nio.file.Path

/**
 * Runs an DocumentContent validator through this plugin. @see Validator#run_test_assertions.
 */
class DocumentContentValidator extends AbstractSoapValidater {
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

    String method
    String key
    String value
    String codeValue
    String codingScheme
    String codeDisplayName

    /**
     * Optional parameter
     */
    String metadataValidationFile

    DocumentContentValidator() {
        filterDescription = 'Runs a DocumentContent validator (Validator#run_test_assertions) through this plugin.'
    }

    @Override
    ValidaterResult validate(SoapSimulatorTransaction sst) {
        reset() // Clear log
        explore(sst)
        boolean requestMatch = false
        boolean responseMatch = false
        if (!requestMsgExpectedContent && !responseMsgExpectedContent) {
            String illegalArg = "Either requestMsgExpectedContent attribute or responseMsgExpectedContent attribute must be specified. See Validator#run_test_assertion for a list of codes."
            error(illegalArg)
            throw new IllegalArgumentException(illegalArg)
        }
        if (requestMsgExpectedContent && sst) {
            //          For Debugging only -- this log creates too many messages
//            log("Processing request from eventId: ${transactionInstance?.simDbEvent?.eventId} simLogUrl: ${transactionInstance?.simDbEvent?.simLogUrl}")
            if (sst.requestBody) {
                Metadata m = MetadataParser.parseNonSubmission(Util.parse_xml(sst.requestBody))
                String errors = ""
                if (requestMsgExpectedContent.equals("DocumentContent")) {
                    Validator v = new Validator().setM(m);
                    // Will also want to set transaction so we can find the files
                    switch (method) {
                        case "exists":
                            if (!v.attachmentFileFound(sst)) {
                                errors = v.getErrors()
                            }
                            break;
                        case "isParsableXML":
                            if (!v.attachmentIsParsableXML(sst)) {
                                errors = v.getErrors()
                            }
                            break;
                        case "containsTemplateId":
                            if (!v.containsTemplateId(sst, value)) {
                                errors = v.getErrors()
                            }
                            break;
                        case "single":
                            if (!v.namedXMLDocumentCompare(sst, key, value)) {
                                errors = v.getErrors()
                            }
                            break;
                        case "singleCode":
                            if (!v.namedMetadataCompareCode(key, codeValue, codingScheme, codeDisplayName)) {
                                errors = v.getErrors()
                            }
                            break;
                        case "containsCode":
                            if (!v.namedMetadataContainsCode(key, codeValue, codingScheme, codeDisplayName)) {
                                errors = v.getErrors()
                            }
                            break;
                        default:
                            errors="Unrecognized metadata validation method:" + method + ". Expecting one of single, singleCode.";
                            break;
                    }
                } else {
                    Validator v = new Validator().setM(m)
                    v.run_test_assertion(requestMsgExpectedContent, Integer.parseInt(requestMsgECCount?:"-1"))
                    errors = v.getErrors()
                }

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

        new ValidaterResult(sst, this.copy(), match)
    }

    private void explore(SoapSimulatorTransaction sst) {
        SimDb simDb = null
        SimDbEvent event = sst.getSimDbEvent()
        File f = event.getRequestBodyFile()
        File parentFile = f.getParentFile()
        Path px = parentFile.toPath()
        Path path = event.getRequestBodyFile().getParentFile().toPath()

        path.resolve("Repository")
        String x = path.resolve("Repository").toFile().getAbsolutePath()
        String y = x;
    }

    AbstractSoapValidater copy() {
        DocumentContentValidator mcv = new DocumentContentValidator()
        mcv.responseMsgExpectedContent = responseMsgExpectedContent
        mcv.requestMsgExpectedContent = requestMsgExpectedContent
        mcv.responseMsgECCount = responseMsgECCount
        mcv.requestMsgECCount = requestMsgECCount
        mcv.metadataValidationFile = metadataValidationFile
        mcv.simReference = new SimReference(simReference?.simId, simReference?.transactionType, simReference?.actorType)
        mcv.errors = this.errors
        mcv.setLog(new StringBuilder(this.log))
        mcv
    }
}
