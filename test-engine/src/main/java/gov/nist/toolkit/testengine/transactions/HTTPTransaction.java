package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.HttpParserBa;
import gov.nist.toolkit.registrymsg.registry.RegistryResponseParser;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jaxen.JaxenException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HTTPTransaction extends BasicTransaction {
    Map<String, List<String>> headers = new HashMap<>();
    File file;
    String transType;

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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(endpoint);

        for (String name : headers.keySet()) {
            List<String> values = headers.get(name);
            for (String value : values) {
                httpPost.addHeader(name, value);
            }
        }
        testLog.add_name_value(instruction_output, "OutHeader", headersToString());
        testLog.add_name_value(instruction_output, "InputMetadata", Io.stringFromFile(file));

        try {
            InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            httpPost.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(httpPost);

            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                testLog.add_name_value(instruction_output, "InHeader", response.getStatusLine().toString());
                Header[] responseHeaders = response.getAllHeaders();
                HttpEntity responseEntity = response.getEntity();
                String responseString = EntityUtils.toString(responseEntity);
                testLog.add_name_value(instruction_output, "Result", responseString);

                Map<String, List<String>> rspHeaders = new HashMap<>();
                for (int i=0; i<responseHeaders.length; i++) {
                    Header h = responseHeaders[i];
                    addHeader(rspHeaders, h.getName(), h.getValue());
                }

                HttpParserBa parser = new HttpParserBa(rspHeaders, responseString.getBytes());

                if (!parser.isMultipart()) {
                    s_ctx.set_error("Response is not multipart");
                    failed();
                    return;
                }
                if (parser.getMultipart().getPartCount() != 1) {
                    s_ctx.set_error("Expected single part in multipart response");
                    failed();
                    return;
                }
                byte[] partContent = parser.getMultipart().getPart(0).getBody();

                loadStepContextwithResponseErrors(new String(partContent));
            } catch (Exception e) {
                s_ctx.set_error(e.getMessage());
                failed();
            } finally {
                //response.close();
            }
        } catch (Exception e) {
            s_ctx.set_error(e.getMessage());
            failed();
        } finally {
            //httpclient.close();
        }
    }

    private void loadStepContextwithResponseErrors(String responseString) throws XdsInternalException, JaxenException {
        System.out.println(responseString);
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

        if (part_name.equals("BodyFile")) {
            file = new File(testConfig.testplanDir, part.getText());
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
}
