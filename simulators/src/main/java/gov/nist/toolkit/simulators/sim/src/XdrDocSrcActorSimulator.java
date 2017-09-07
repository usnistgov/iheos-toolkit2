package gov.nist.toolkit.simulators.sim.src;

import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.securityCommon.SecurityParamsFactory;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.soap.DocumentMap;
import gov.nist.toolkit.soap.axis2.MtomBuilder;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsConfigurationException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class XdrDocSrcActorSimulator extends BaseDsActorSimulator {
    static final Logger logger = Logger.getLogger(XdrDocSrcActorSimulator.class);
    OMElement messageBody = null;
    DocumentMap documentMap = null;
    List<OMElement> extraSoapHeaderElements = new ArrayList<>();

    // not used
    Map<String, ByteArrayDataSource> documents = new HashMap<>();

    static List<TransactionType> transactions = new ArrayList<>();

    static {
        transactions.add(TransactionType.PROVIDE_AND_REGISTER);
    }

    public boolean supports(TransactionType transactionType) {
        return transactions.contains(transactionType);
    }


    /**
     * This would be used if this were a server sim.  It is useless as a client sim
     * @param transactionType transaction code
     * @param mvc MessageValidatorEngine - execution engine for validators and simulators
     * @param validation name of special validation to be run. Allows simulators to be extended
     * to perform test motivated validations
     * @return
     * @throws IOException
     */
    @Override
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        return false;
    }

    @Override
    public void init() {

    }

    public XdrDocSrcActorSimulator() {}

    /**
     * Send the XDR.  The Document Source config contains the endpoints of the Document Recipient
     * @param config of the Document Source sim
     * @param transactionType
     * @return
     * @throws AxisFault
     * @throws LoadKeystoreException
     * @throws XdsInternalException
     * @throws XdsFormatException
     * @throws XdsConfigurationException
     */
    public OMElement run(SimulatorConfig config, TransactionType transactionType, DocumentMap documentMap, boolean isTls, String environmentName) throws AxisFault, LoadKeystoreException, XdsInternalException, XdsFormatException, XdsConfigurationException {
        GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

        logger.debug("XDR Doc Src starting transaction " + transactionType);

        if (!transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER))
            throw new XdsConfigurationException(String.format("Do not understand transaction type %s", transactionType), "");

        if (messageBody == null)
            throw new XdsInternalException("XdrDocSrcActorSimulator: message body is null");

        String endpoint =
            config.get(
                    (isTls) ? SimulatorProperties.pnrTlsEndpoint : SimulatorProperties.pnrEndpoint
            ).asString();

        MtomBuilder mtom = new MtomBuilder();
        mtom.setMetadataEle(messageBody);
        mtom.setDocumentMap(documentMap);

        Soap soap = new Soap();
        for (OMElement ele : extraSoapHeaderElements) {
            soap.addHeader(ele);
        }

        soap.setSecurityParams(SecurityParamsFactory.getSecurityParams(environmentName));

        return soap.soapCall(mtom.getBody(),
                endpoint,
                true, // mtom
                true, // addressing
                true, // SOAP1.2
                SoapActionFactory.pnr_b_action,
                SoapActionFactory.getResponseAction(SoapActionFactory.pnr_b_action));
    }

    public void addDocument(String id, ByteArrayDataSource dataSource) {
        documents.put(id, dataSource);
    }

    public OMElement getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(OMElement messageBody) {
        this.messageBody = messageBody;
    }

    public DocumentMap getDocumentMap() {
        return documentMap;
    }

    public void setDocumentMap(DocumentMap documentMap) {
        this.documentMap = documentMap;
    }

    public void addSoapHeaderElement(OMElement ele) { extraSoapHeaderElements.add(ele); }
}
