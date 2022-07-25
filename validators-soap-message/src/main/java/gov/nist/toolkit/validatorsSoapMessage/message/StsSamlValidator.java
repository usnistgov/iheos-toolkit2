package gov.nist.toolkit.validatorsSoapMessage.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.testengine.transactions.RetrieveTransaction;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * We use only a single security header.
 * Verify only one element and get validation results from Gazelle.
 * --
 * For WSSE, See https://docs.oasis-open.org/wss/v1.1/wss-v1.1-spec-os-SOAPMessageSecurity.pdf
 * For Gazelle STS, See https://github.com/usnistgov/iheos-toolkit2/wiki/SAML-Validation-against-Gazele
 * And https://gazelle.ihe.net/content/sts
 */
public class StsSamlValidator extends AbstractMessageValidator {
    OMElement header;
    OMElement securityElement;
    ErrorRecorderBuilder erBuilder;
    MessageValidatorEngine mvc;
    RegistryValidationInterface rvi;
    TestSession testSession;
    static Logger logger = Logger.getLogger(StsSamlValidator.class.getName());

    public StsSamlValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi, final OMElement header, TestSession testSession) {
        super(vc);
        this.erBuilder = erBuilder;
        this.mvc = mvc;
        this.rvi = rvi;
        this.header = header;
        this.testSession = testSession;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {

            try {
                this.er = er;
                er.registerValidator(this);

                List<OMElement> secEls = XmlUtil.decendentsWithLocalName(header, "Security", -1);
                if (secEls.size() == 1) {
                    er.detail("Found one Security element.");
                    securityElement = secEls.get(0);
                    validateNs(er, securityElement, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                } else {
                    er.err(XdsErrorCode.Code.SoapFault, new Exception("Security element (SAML) was expected. Security element count is "+secEls.size()+", should be one."));
                }
                if (securityElement != null) {
                    List<OMElement> assertionEls = XmlUtil.decendentsWithLocalName(securityElement, "Assertion", 1);
                    if (assertionEls!=null) {
                        if (assertionEls.size()!=1) {
                            er.err(XdsErrorCode.Code.SoapFault, new Exception("SAML assertion was expected. Assertion element count is "+assertionEls.size()+", should be one."));
                        }
                        er.detail("Found one Assertion element.");
                        OMElement samlAssertion = assertionEls.get(0);
                        validateNs(er, samlAssertion, "urn:oasis:names:tc:SAML:2.0:assertion");

                        String samlAssertionStr = null;
                        boolean isRemoveWhitespace = Installation.instance().propertyServiceManager().isStsRemoveWhitespace();
                        if (isRemoveWhitespace) {
                            logger.fine("Removing whitespace from assertion string.");
                            samlAssertionStr = samlAssertion.toString().replaceAll("[\t\r\n\f\n]", ""); // Strip whitespace/newlines as per Gazelle Picketlink STS requirement. Whitespace should be already taken care of by OMElement.toString.
                            samlAssertionStr = samlAssertionStr.replaceAll(">\\s*<", "><");
                        } else {
                            logger.fine("Not removing whitespace from assertion string.");
                            samlAssertionStr = samlAssertion.toString();
                        }

                        Map<String, String> params = new HashMap<>();
                        logger.fine("SAML assertion: " + samlAssertionStr);
                        samlAssertionStr = samlAssertionStr.replace("&amp;","&");
                        params.put("$saml-assertion$", samlAssertionStr); // Params replacement will take care of this by restoring it to proper format

                        String query = "samlassertion-validate";

                        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(null);
                        String stsActor = Installation.instance().propertyServiceManager().getStsActorName(); // Actor xml system name
                        SiteSpec stsSpec =  new SiteSpec(stsActor, TestSession.DEFAULT_TEST_SESSION);
                        stsSpec.setGazelleXuaUsername("valid");
                        List<Result> results = xdsTestServiceManager.querySts(stsSpec, query, params, false, TestSession.DEFAULT_TEST_SESSION);

                        if (results.size() == 1) {
                            if (!results.get(0).passed()) {
                                boolean validateStepFound = true;
                                for (StepResult stepResult : results.get(0).getStepResults()) {
                                    if ("validate".equals(stepResult.stepName)) {
                                        validateStepFound = true;
                                        List<String> soapFaults = stepResult.getSoapFaults();
                                        if (soapFaults != null && soapFaults.size() > 0) {
                                            er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException(soapFaults.get(0).toString()));
                                            return;
                                        }
                                    }
                                }
                                if (!validateStepFound) {
                                    er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS: Step named 'validate' was not found in the Result set."));
                                }
                                er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS: failed. Assertions: " + results.get(0).assertions.toString())); // XML Parse error? Is the patient Id ^^^[&] escaped?
                            }
                        } else {
                            er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS: No result."));
                        }
                    }
                }
            } catch (Exception ex) {
                er.err(XdsErrorCode.Code.SoapFault, ex);
            } finally {
                er.unRegisterValidator(this);
            }
        }

    protected void validateNs(ErrorRecorder er, OMElement el, String ns)  {
        OMNamespace omns = el.getNamespace();
        String nsuri = omns.getNamespaceURI();
        if (!ns.equals(nsuri)) {
            er.err(XdsErrorCode.Code.SoapFault, new Exception(el.getLocalName() + " Element namespace was not recognized. Was expecting: " + ns));
        }
    }

    public OMElement getSecurityElement() {
        return securityElement;
    }

}
