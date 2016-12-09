package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.testengine.engine.ReportManager;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.testengine.engine.TestSupportTransactions;
import gov.nist.toolkit.testengine.engine.Transactions;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage HTTP only transactions, for example: 
 * <pre>{@code
 * <TestStep id="submit">
    <ExpectedStatus>Success</ExpectedStatus>
    <HttpTransaction type="pr.b">
      <Headers>
        Content-Type: multipart/related; boundary="MIMEBoundary_95b57d7287e4fa4b528a8f050c41f8ad829c20332f23b48d"; type="application/xop+xml"; start-info="application/soap+xml"; action="urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b"
      </Headers>
      <BodyFile>body.txt</BodyFile>
    </HttpTransaction>
  </TestStep>
 * }
 * </pre>
 * <p/>Notes:<ol>
 * <li/>The type attribute is mandatory, and must match to a value of the {@link
 * gov.nist.toolkit.configDatatypes.client.TransactionType TransactionType} 
 * enum. The endpoint to which the http request is sent is determined by this
 * transaction.
 * <li/>Text content of Headers element forms headers for the HTTP Request. Each
 * header must appear on a separate line and be of the form:<br/>
 * Header-name : header-body
 * <li/>BodyFile element is required. Its text content must be a path relative
 * to the test directory which gives the file which will be the body of the 
 * HTTP Request.
 * <li/>Report and UseReport elements may also be present, and if so, are 
 * processed in the usual way.</ol>
 */
public class HTTPTransaction extends BasicTransaction {
    Map<String, List<String>> headers = new HashMap<>();
    File file;
    String transType;
    String stsQuery;
    boolean hasLinkage;

    public HTTPTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
    }

    private String headersToString() {
        StringBuilder buf = new StringBuilder();
        for (String name : headers.keySet()) {
            List<String> values = headers.get(name);
            for (String value : values) {
                buf.append(name).append(": ").append(value).append("\n");
            }
        }

        return buf.toString();
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Override
    protected void run(OMElement request) throws Exception {
        if (endpoint == null || endpoint.equals("")) {
            if (transType == null)
                throw new XdsInternalException("HttpTransaction - type is null");
            TransactionType ttype = TransactionType.find(transType);
            if (ttype == null)
                throw new XdsInternalException("HttpTransaction - transaction type " + transType + " does not map to a defined transaction type");

            parseEndpoint(ttype);

            if (endpoint == null || endpoint.equals(""))
                throw new XdsInternalException("Endpoint is null");
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(endpoint);

            for (String name : headers.keySet()) {
                List<String> values = headers.get(name);
                for (String value : values) {
                    httpPost.addHeader(name, value);
                }
            }
            testLog.add_name_value(instruction_output, "OutHeader", headersToString());


            InputStream inputStream;
            inputStream = prepareInputStream(file);

            InputStreamEntity reqEntity = new InputStreamEntity(inputStream, -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            httpPost.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(httpPost);

                try {
                    System.out.println("----------------------------------------");
                    StatusLine statusLine = response.getStatusLine();
                    System.out.println(statusLine);
                    testLog.add_name_value(instruction_output, "InHeader", statusLine.toString());
                    Header[] responseHeaders = response.getAllHeaders();
                    HttpEntity responseEntity = response.getEntity();
                    String responseString = EntityUtils.toString(responseEntity);
                    System.out.println(responseString);
                    testLog.add_name_value(instruction_output, "Result", responseString);

                    if (Transactions.ProvideAndRegister_b.equals(transType)) {
                        if (validatePnr(responseHeaders, responseString)) return;
                    } else if (TestSupportTransactions.SecureTokenService.equals(transType)) {
                        if (200 == statusLine.getStatusCode()) {
                            if ("issue".equals(stsQuery)) {
                                if (validateStsIssue(responseHeaders, responseString)) return;
                            } else if ("validate".equals(stsQuery)) {
                                if (validateStsValidate(responseHeaders, responseString)) return;
                            }
                        } else {
                            String message = "HTTP error code: " + statusLine.getStatusCode()  + " Reason: " + statusLine.getReasonPhrase();
                            s_ctx.set_error(message);
                            s_ctx.set_fault(SoapFault.FaultCodes.Receiver.toString(), message);
                            failed();
                            return;
                        }
                    }

                } catch (Exception e) {
                    s_ctx.set_error(e.getMessage());
                    failed();
                } finally {
                    //response.close();
                    inputStream.close();
                }

        } catch (Exception e) {
            String message = e.toString() + ":\n" + ExceptionUtil.exception_details(e);
            s_ctx.set_error(message);
            s_ctx.set_fault(SoapFault.FaultCodes.Receiver.toString(), message);
            failed();
        } finally {
            //httpclient.close();
        }
    }


    private boolean validatePnr(Header[] responseHeaders, String responseString) throws HttpParseException, XdsInternalException, JaxenException {
        Map<String, List<String>> rspHeaders = new HashMap<>();
        for (int i = 0; i < responseHeaders.length; i++) {
            Header h = responseHeaders[i];
            addHeader(rspHeaders, h.getName(), h.getValue());
        }

        HttpParserBa parser = new HttpParserBa(rspHeaders, responseString.getBytes());

        if (!parser.isMultipart()) {
            s_ctx.set_error("Response is not multipart");
            failed();
            return false;
        }
        if (parser.getMultipart().getPartCount() != 1) {
            s_ctx.set_error("Expected single part in multipart response");
            failed();
            return false;
        }
        byte[] partContent = parser.getMultipart().getPart(0).getBody();

        loadStepContextwithResponseErrors(new String(partContent));
        return true;
    }

    private void loadStepContextwithResponseErrors(String responseString) throws XdsInternalException, JaxenException {
        OMElement envelopeEle = Util.parse_xml(responseString);

        AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='RegistryResponse']");
        List nodeList = xpathExpression.selectNodes(envelopeEle);
        if (nodeList.size() != 1) {
            s_ctx.set_error("Cannot extract RegistryResponse from SOAP Message");
            failed();
            return;
        }
        OMElement responseEle = (OMElement) nodeList.get(0);

        RegistryResponseParser responseParser = new RegistryResponseParser(responseEle);
        if(responseParser.is_error()) {
            s_ctx.set_error(responseParser.get_regrep_error_msgs());
            failed();
        }
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {

        String part_name = part.getLocalName();
        if (part_name.equals("Headers")) {
            String text = part.getText();
            String lines[] = text.split("\\r?\\n");
            for (int i=0; i<lines.length; i++) {
                String line = lines[i].trim();
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String value = parts[1].trim();
                    addHeader(headers, name, value);
                }
            }
        }
        else  if (part_name.equals("BodyFile")) {
            file = new File(testConfig.testplanDir, part.getText());
        }
        else if (part_name.equals("Report")) {
            parseReportInstruction(part);
        }
        else if (part_name.equals("UseReport")) {
            parseUseReportInstruction(part);
        }
    }

    private void addHeader(Map<String, List<String>> headers, String name, String value) {
        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            headers.put(name, values);
            values.add(value);
        } else {
            values.add(value);
        }
    }

    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return transType;
    }


    private InputStream prepareInputStream(File file) throws XdsInternalException, FileNotFoundException {
        boolean isSts = TestSupportTransactions.SecureTokenService.equals(transType);
        boolean basicLinkage = (!isSts && isHasLinkage());
        if (isSts || basicLinkage) {
            OMElement ele = Util.parse_xml(file);
            Map<String,String> linkage =  getExternalLinkage();
            if (linkage!=null && linkage.size()>0) {
                applyLinkage(ele);
                compileExtraLinkage(ele);
            } else
                reportManagerPreRun(ele);

                String body = ele.toString();
                body = ReportManager.getDecodedStr(body);
                testLog.add_name_value(instruction_output, "InputMetadata", body);
                return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

        } else {
                return new FileInputStream(file);
        }
    }

    private boolean validateStsValidate(Header[] responseHeaders, String responseString) throws Exception {
        OMElement envelopeEle = Util.parse_xml(responseString);

        AXIOMXPath xpathExpression = new AXIOMXPath("//*[local-name()='Body']/*[local-name()='RequestSecurityTokenResponseCollection']/*[local-name()='RequestSecurityTokenResponse']/*[local-name()='Status']/*[local-name()='Code']");
        String codeValue = xpathExpression.stringValueOf(envelopeEle);

        if (codeValue==null || !"http://docs.oasis-open.org/ws-sx/ws-trust/200512/status/valid".equals(codeValue)) {
            xpathExpression = new AXIOMXPath("//*[local-name()='Body']/*[local-name()='RequestSecurityTokenResponseCollection']/*[local-name()='RequestSecurityTokenResponse']/*[local-name()='Status']/*[local-name()='Reason']");
            String reasonValue = xpathExpression.stringValueOf(envelopeEle);

            String message = "Reason: " + reasonValue + ". Code: " + codeValue;
            s_ctx.set_fault(SoapFault.FaultCodes.Sender.toString(), message);
            failed();
            return false;
        }

        return true;
    }

    private boolean validateStsIssue(Header[] responseHeaders, String responseString) throws Exception {
        OMElement envelopeEle = Util.parse_xml(responseString);

        AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='Body']/*[local-name()='RequestSecurityTokenResponseCollection']/*[local-name()='RequestSecurityTokenResponse']/*[local-name()='RequestedSecurityToken']/*[local-name()='Assertion']");
        List nodeList = xpathExpression.selectNodes(envelopeEle);
        if (nodeList.size() != 1) {
            s_ctx.set_error("Cannot extract STS SAML Assertion from SOAP Message");
            failed();
            return false;
        }
        OMElement responseEle = (OMElement) nodeList.get(0);
        String assertionId = responseEle.getAttributeValue(new QName("ID")).toString();
        String issueInstant = responseEle.getAttributeValue(new QName("IssueInstant"));

        if (assertionId==null && issueInstant==null) {
            s_ctx.set_error("SAML Assertion: Null ID or IssueInstant attribute value.");
            failed();
            return false;
        }

        return true;
    }

    public String getStsQuery() {
        return stsQuery;
    }

    public void setStsQuery(String stsQuery) {
        this.stsQuery = stsQuery;
    }

    public boolean isHasLinkage() {
        return hasLinkage;
    }

    public void setHasLinkage(boolean hasLinkage) {
        this.hasLinkage = hasLinkage;
    }
}
