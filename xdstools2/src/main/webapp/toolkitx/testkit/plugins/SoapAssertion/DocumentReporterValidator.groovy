package war.toolkitx.testkit.plugins.SoapAssertion

import gov.nist.toolkit.registrymsg.repository.ProvideAndRegisterModel
import gov.nist.toolkit.registrymsg.repository.ProvideAndRegisterParser
import gov.nist.toolkit.registrymsg.repository.RetrieveRequestParser
import gov.nist.toolkit.registrymsg.repository.RetrieveRequestModel
import gov.nist.toolkit.testengine.engine.Validator
import gov.nist.toolkit.testengine.engine.SimReference
import gov.nist.toolkit.testengine.engine.SoapSimulatorTransaction
import gov.nist.toolkit.testengine.engine.validations.ValidaterResult
import gov.nist.toolkit.testengine.engine.validations.soap.AbstractSoapValidater
import gov.nist.toolkit.testengine.engine.Validator

import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.utilities.xml.Util
import org.apache.axiom.om.OMElement

import groovy.transform.MapConstructor

/**
 * Runs an MetadataContent validator through this plugin. @see Validator#run_test_assertions.
 */
@MapConstructor
class DocumentReporterValidator extends AbstractSoapValidater {
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
    String key1
    String value1
    String key2
    String value2
    String key3
    String value3
    String transactionBase

    /**
     * Optional parameter
     */
    String metadataValidationFile;

    DocumentReporterValidator() {
        filterDescription = 'Uses the validator plugin structure to locate/report a submitted document.'
    }

    @Override
    ValidaterResult validate(SoapSimulatorTransaction sst) {
        reset() // Clear log
        boolean requestMatch = false
        boolean responseMatch = false
        if (!requestMsgExpectedContent && !responseMsgExpectedContent) {
            String illegalArg = "Either requestMsgExpectedContent attribute or responseMsgExpectedContent attribute must be specified. See Validator#run_test_assertion for a list of codes."
            error(illegalArg)
            throw new IllegalArgumentException(illegalArg)
        }
        boolean match = true
        if (requestMsgExpectedContent && sst) {
            //          For Debugging only -- this log creates too many messages
//            log("Processing request from eventId: ${transactionInstance?.simDbEvent?.eventId} simLogUrl: ${transactionInstance?.simDbEvent?.simLogUrl}")
            if (sst.requestBody) {
                String x = "abc"
                String y = "def"
                ProvideAndRegisterParser parser = new ProvideAndRegisterParser(Util.parse_xml(sst.requestBody))
                ProvideAndRegisterModel provideAndRegisterModel = parser.getModel();
                String errors = ""
                if (requestMsgExpectedContent.equals("ProvideAndRegister")) {
                    Validator v = new Validator().setProvideAndRegisterModel(provideAndRegisterModel)
                    switch (method) {
                        case "prbMatch":
                            errors = this.matchMultipleKeys(v)
                            break;
                        default:
                            errors="Unrecognized Document Retrieve validation method:" + method + ". Expecting one of single, singleCode, containsCode, contains.";
                            break;
                    }
                } else {
                    error("NA", "This validator expects requestMsgExpectedContent=ProvideAndRegister. Testplan provided: " + requestMsgExpectedContent)
                }

                if (errors.length() > 0) {
                    error("Request", errors)
                    match = false
                } else {
                    this.recordTransaction(
                            sst.simReference.simId.testSession.value,
                            provideAndRegisterModel.getDocumentEntryUniqueId(),
                            sst.requestBody)
                }
            } else {
                error("Request","Null transactionInstance or its request body is null")
            }
        }

        return new ValidaterResult(sst, this.copy(), match)
    }

    private void recordTransaction(String simId, String documentEntryUniqueId, String transactionXML) {
        String outputPathXML = "/tmp/" + simId + "/" + transactionBase + ".xml"
        String outputPathTxt = "/tmp/" + simId + "/" + transactionBase + ".txt"
        new File("/tmp/" + simId).mkdirs()
        try {
            PrintWriter writerXML = new PrintWriter(outputPathXML, "UTF-8")
            PrintWriter writerTxt = new PrintWriter(outputPathTxt, "UTF-8")

            writerXML.println(transactionXML)
            writerTxt.println(documentEntryUniqueId)

            writerXML.close()
            writerTxt.close()
        } catch (Exception e) {
            System.out.println("Could not write transaction xml or text to: " + outputPathXML)
            e.printStackTrace()
        }
    }

    private String matchMultipleKeys(Validator v) {
        String errors = ""

        errors += matchSingleKey(v, this.key1, this.value1)
        errors += matchSingleKey(v, this.key2, this.value2)
        errors += matchSingleKey(v, this.key3, this.value3)


        return errors
    }

    private String matchSingleKey(Validator v, String key, String value) {
        String errors = ""

        if (key != null) {
            if (!v.namedFieldCompare(key, value)) {
                errors = v.getErrors()
            }
        }

        return errors
    }

    AbstractSoapValidater copy() {
        DocumentReporterValidator mcv = new DocumentReporterValidator()
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
