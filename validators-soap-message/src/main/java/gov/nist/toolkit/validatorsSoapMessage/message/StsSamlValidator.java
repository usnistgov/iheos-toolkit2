package gov.nist.toolkit.validatorsSoapMessage.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
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
    ErrorRecorderBuilder erBuilder;
    MessageValidatorEngine mvc;
    RegistryValidationInterface rvi;

    public StsSamlValidator(ValidationContext vc, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi, final OMElement header) {
        super(vc);
        this.erBuilder = erBuilder;
        this.mvc = mvc;
        this.rvi = rvi;
        this.header = header;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {

            try {
                this.er = er;
                er.registerValidator(this);

                OMElement securityEl = null;
                List<OMElement> secEls = XmlUtil.decendentsWithLocalName(header, "Security");
                if (secEls.size() == 1) {
                    er.detail("Found one Security element.");
                    securityEl = secEls.get(0);
                    validateNs(er, securityEl, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                } else {
                    er.err(XdsErrorCode.Code.SoapFault, new Exception("Security element (SAML) was expected. Security element count is "+secEls.size()+", should be one."));
                }
                if (securityEl != null) {
                    List<OMElement> assertionEls = XmlUtil.decendentsWithLocalName(securityEl, "Assertion");
                    if (assertionEls!=null) {
                        if (assertionEls.size()!=1) {
                            er.err(XdsErrorCode.Code.SoapFault, new Exception("SAML assertion was expected. Assertion element count is "+assertionEls.size()+", should be one."));
                        }
                        er.detail("Found one Assertion element.");
                        OMElement assertionEl = assertionEls.get(0);
                        validateNs(er, assertionEl, "urn:oasis:names:tc:SAML:2.0:assertion");

                        String samlAssertion = assertionEl.toString().replaceAll("[\t\r\n\f\n]",""); // Strip whitespace/newlines as per Gazelle Picketlink STS requirement. Whitespace should be already taken care of by OMElement.toString.
                        samlAssertion = samlAssertion.replaceAll(">\\s*<", "><");

                        Map<String, String> params = new HashMap<>();
                        params.put("$saml-assertion$", samlAssertion.replace("&amp;","&")); // Params replacement will take care of this by restoring it to proper format

                        String query = "samlassertion-validate";

                        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(null);
                        List<Result> results = xdsTestServiceManager.querySts("GazelleSts","default",query,params, false);

                        if (results.size() == 1) {
                            if (!results.get(0).passed()) {
                                for (StepResult stepResult : results.get(0).getStepResults()) {
                                    if ("validate".equals(stepResult.stepName)) {
                                        List<String> soapFaults = stepResult.getSoapFaults();
                                        if (soapFaults != null && soapFaults.size() > 0) {
                                            er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException(soapFaults.get(0).toString()));
                                            return;
                                        }
                                    }
                                }
                                er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS: 'validate' step result not found."));
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

}
