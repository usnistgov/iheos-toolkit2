package gov.nist.toolkit.validatorsSoapMessage.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
                    er.err(XdsErrorCode.Code.SoapFault, new Exception("Security element count is zero or more than 1. Was only expecting one."));
                }
                if (securityEl != null) {
                    List<OMElement> assertionEls = XmlUtil.decendentsWithLocalName(securityEl, "Assertion");
                    if (assertionEls!=null) {
                        if (assertionEls.size()!=1) {
                            er.err(XdsErrorCode.Code.SoapFault, new Exception("Assertion element count is zero or more than 1. Was only expecting one."));
                        }
                        er.detail("Found one Assertion element.");
                        OMElement assertionEl = assertionEls.get(0);
                        validateNs(er, assertionEl, "urn:oasis:names:tc:SAML:2.0:assertion");

                        String samlAssertion = assertionEl.toString().replaceAll("[\t\r\n\f\n]",""); // Strip whitespace/newlines as per Gazelle Picketlink STS requirement. Whitespace should be already taken care of by OMElement.toString.
                        samlAssertion = samlAssertion.replaceAll(">\\s*<", "><");

                        String sessionName = "default";
                        String environmentName = "default";

                        setTruststore();

                        Session mySession = new Session(Installation.instance().warHome(), sessionName);
                        mySession.setEnvironment(environmentName);

                        // TODO: make siteName dynamic.
                        // This must exist in the EC Dir.
                        SiteSpec stsSpec =  new SiteSpec("GazelleSts");
                        if (mySession.getMesaSessionName() == null)
                            mySession.setMesaSessionName(sessionName);
                        mySession.setSiteSpec(stsSpec);
                        mySession.setTls(true); // Required for Gazelle

                        TestInstance testInstance = new TestInstance("GazelleSts");
                        Map<String, String> params = new HashMap<>();
                        params.put("$saml-assertion$", samlAssertion);

                        XdsTestServiceManager xdsTestServiceManager = new XdsTestServiceManager(mySession);

                        List<String> sections = new ArrayList<String>();
                        sections.add("samlassertion-validate");
                        List<Result> results =  xdsTestServiceManager.runTestplan(environmentName,sessionName,stsSpec,testInstance,sections,params,true,mySession,xdsTestServiceManager);

                        if (results.size() == 1) {
                            if (!results.get(0).passed()) {
                                er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS SAML validation failed: Step failed."));
                                // TODO: soapFault
//                                SoapFault soapFault = new SoapFault(SoapFault.FaultCodes.DataEncodingUnknown)
                            }
                        } else {
                            er.err(XdsErrorCode.Code.SoapFault, new ToolkitRuntimeException("STS SAML validation failed: No result."));
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


    void setTruststore() {
        String tsSysProp =
         System.getProperty("javax.net.ssl.trustStore");

        if (tsSysProp==null) {
            String tsFileName = "/gazelle_sts_cert_truststore";
            URL tsURL = getClass().getResource(tsFileName); // Should this be a toolkit system property variable?
            if (tsURL!=null) {
                File tsFile = new File(tsURL.getFile());
                System.setProperty("javax.net.ssl.trustStore",tsFile.toString());
                System.setProperty("javax.net.ssl.trustStorePassword","changeit");
                System.setProperty("javax.net.ssl.trustStoreType","JKS");
            } else {
                throw new ToolkitRuntimeException("Cannot find truststore by URL: " + tsURL);
            }

        }

    }

}
