package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.XMLErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.gwt.client.GWTValidationStepResult;
import gov.nist.toolkit.errorrecording.gwt.client.GwtValidatorErrorItem;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrymsg.registry.RegistryResponse;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseSendingSim;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.soap.http.SoapUtil;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmsg.message.*;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valregmsg.validation.engine.ValidateMessageService;
import gov.nist.toolkit.valsupport.engine.ValidationStep;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 *
 */
public class DsSimCommon {
    SimulatorConfig simulatorConfig = null;
    public RegIndex regIndex = null;
    public RepIndex repIndex = null;
    public SimCommon simCommon;
    Map<String, StoredDocument> documentsToAttach = null;  // cid => document
    RegistryErrorListGenerator registryErrorListGenerator = null;

    static Logger logger = Logger.getLogger(DsSimCommon.class);

    public DsSimCommon(SimCommon simCommon, RegIndex regIndex, RepIndex repIndex) throws IOException, XdsException {
        this.simCommon = simCommon;
        this.regIndex = regIndex;
        this.repIndex = repIndex;

        if (regIndex != null) {
            regIndex.setSimDb(simCommon.db);
            regIndex.mc.regIndex = regIndex;
            regIndex.mc.vc = simCommon.vc;
        }

        simCommon.vms = new ValidateMessageService(regIndex);
    }

    // only used to issue Soap Faults
    public DsSimCommon(SimCommon simCommon) {
        this.simCommon = simCommon;
    }

    public void setSimulatorConfig(SimulatorConfig config) { this.simulatorConfig = config; }
    public SimulatorConfig getSimulatorConfig() { return simulatorConfig; }

    /**
     * Starts the validation process by scheduling the HTTP parser. This is called
     * once per input message only.
     * Returns status indicating whether it is ok to continue.  If false then exit
     * immediately without returning a message.  A SOAPFault has already been returned;
     * @return true if successful and false if fault sent
     * @throws IOException
     */
    public boolean runInitialValidationsAndFaultIfNecessary() throws IOException {
        runInitialValidations();
        return !returnFaultIfNeeded();
    }

    /**
     * Runs the validation functions. This is where the ErrorRecorder gets instanciated.
     * @throws IOException
     */
    //TODO XMLErrorRecorder
    public void runInitialValidations() throws IOException {
        XMLErrorRecorderBuilder xerb = new XMLErrorRecorderBuilder();
        runErrorRecorder((ErrorRecorder)xerb);
        // GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
        // runErrorRecorder((ErrorRecorder)gerb);
    }

    /**
     * While in transition between the GWTErrorRecorder and XMLErrorRecorder, this runs any ErrorRecorder
     * @return SimCommon object
     * @throws IOException
     */
    private void runErrorRecorder(ErrorRecorder err) throws IOException {
        simCommon.mvc = simCommon.vms.runValidation(simCommon.vc, simCommon.db, simCommon.mvc, err);
        simCommon.mvc.run();
        simCommon.buildMVR();
        int stepsWithErrors = simCommon.mvc.getErroredStepCount();
        ValidationStep lastValidationStep = simCommon.mvc.getLastValidationStep();
        if (lastValidationStep != null) {
            lastValidationStep.getErrorRecorder().detail
                    (stepsWithErrors + " steps with errors");
            logger.debug(stepsWithErrors + " steps with errors");
        } else {
            logger.debug("no steps with errors");
        }
    }

    public void sendErrorsInRegistryResponse(ErrorRecorder er) {
        if (er == null)
            er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();


        // this works when RegistryResponse is the return message
        // need other options for other messaging environments
        simCommon.mvc.addMessageValidator("Send RegistryResponse with errors", new RegistryResponseSendingSim(simCommon, this), er);

        simCommon.mvc.run();
    }


    /**
     * Wrap response in a SOAP Envelope (and body), header is generated by
     * examining the request header
     * @param body response to be wrapped
     * @return SOAP Envelope
     */
    public OMElement wrapResponseInSoapEnvelope(OMElement body) {
        OMElement env = SoapUtil.buildSoapEnvelope();
        SoapMessageValidator smv = null;

        try {
            smv = (SoapMessageValidator) simCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
        } catch (Exception e) {

        }

        String resp_wsaction = null;
        String messageId = null;

        if (smv != null) {
            String wsaction = smv.getWsAction();
            messageId = smv.getMessageId();

            resp_wsaction = SoapActionFactory.getResponseAction(wsaction);
        }

        SoapUtil.attachSoapHeader(null, resp_wsaction, null, messageId, env);
        SoapUtil.attachSoapBody(body, env);

        return env;
    }

    public OMElement wrapResponseInRetrieveDocumentSetResponse(OMElement regResp) {
        OMElement rdsr = MetadataSupport.om_factory.createOMElement(MetadataSupport.retrieve_document_set_response_qnamens);
        rdsr.addChild(regResp);
        return rdsr;
    }

    public RegistryResponse getRegistryResponse () throws XdsInternalException {
        simCommon.buildMVR();
        return getRegistryResponse(simCommon.mvr.getResults());
    }

    public RegistryErrorListGenerator getRegistryErrorList() throws XdsInternalException {
        return getRegistryErrorList(simCommon.getErrors());
    }

    public boolean hasErrors() { return simCommon.hasErrors(); }

    public void setRegistryErrorListGenerator(RegistryErrorListGenerator relg) {
        registryErrorListGenerator = relg;
    }

    public RegistryErrorListGenerator getRegistryErrorList(List<GWTValidationStepResult> results) throws XdsInternalException {
        try {
            RegistryErrorListGenerator rel = registryErrorListGenerator;
            if (rel == null)
                rel = new RegistryErrorListGenerator(Response.version_3, false);

            rel.setPartialSuccess(simCommon.mvc.isPartialSuccess());

            for (GWTValidationStepResult vsr : results) {
                for (GwtValidatorErrorItem vei : vsr.er) {
                    if (vei.level == GwtValidatorErrorItem.ReportingLevel.ERROR) {
                        String msg = vei.msg;
                        if (vei.resource != null && !vei.resource.equals(""))
                            msg = msg + " (" + vei.resource + ")";
                        rel.add_error(vei.getCodeString(), vei.msg, vei.location, null, null);
                    }
                    if (vei.level == GwtValidatorErrorItem.ReportingLevel.WARNING) {
                        String msg = vei.msg;
                        if (vei.resource != null && !vei.resource.equals(""))
                            msg = msg + " (" + vei.resource + ")";
                        rel.add_warning(vei.getCodeString(), vei.msg, vei.location, null, null);
                    }
                }
            }
            return rel;
        }
        finally {
            registryErrorListGenerator = null;
        }
    }

    public RegistryResponse getRegistryResponse(List<GWTValidationStepResult> results) throws XdsInternalException {
        RegistryErrorListGenerator rel = getRegistryErrorList(results);
        RegistryResponse rr = new RegistryResponse(Response.version_3, rel);
        return rr;
    }

    public void addDocumentAttachments(Metadata m, ErrorRecorder er) {
        try {
            List<String> uids = new ArrayList<String>();
            for (OMElement eo : m.getExtrinsicObjects() ) {
                String uid = m.getUniqueIdValue(eo);
                uids.add(uid);
            }
            addDocumentAttachments(uids, er);
        } catch (Exception e) {
            logger.error("Cannot extract DocumentEntry.uniqueId from metadata stored in simulator", e);
            er.err(XdsErrorCode.Code.XDSRepositoryError, "SimCommon#addDocumentAttachment: Cannot extract DocumentEntry.uniqueId from metadata stored in simulator", this, "Internal Error");
        }
    }

    public void addDocumentAttachments(List<String> uids, ErrorRecorder er) throws XdsInternalException {
        int notFound = 0;
        for (String uid : uids) {
            StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);
            // if (sd == null)
            //   continue;
            // addDocumentAttachment(sd);
            // Fix Issue 70
            if (sd == null) {
                notFound++;
                er.err(XdsErrorCode.Code.XDSDocumentUniqueIdError, "DsSimCommon#addDocumentAttachments: Not found.", uid, "Error");
            } else
                addDocumentAttachment(sd);
        }

        int foundDocuments = 0;
        if (documentsToAttach!=null)
            foundDocuments = documentsToAttach.size();

        if (notFound > 0 && foundDocuments > 0) {
            // Only some documents were found.
            simCommon.mvc.setPartialSuccess(true);
        }


    }

    public void addImagingDocumentAttachments(List<String> imagingDocumentUids, List<String> transferSyntaxUids, ErrorRecorder er) {
	logger.debug("DsSimComon#addImagingDocumentAttachments");
        for (String uid : imagingDocumentUids) {
            //StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);
            StoredDocument sd = this.getStoredImagingDocument(uid, transferSyntaxUids);
	    logger.debug(" uid=" + uid);
            if (sd == null)
                continue;
            addDocumentAttachment(sd);
	    logger.debug(" Added document for this uid");
        }
    }

    public Collection<StoredDocument> getAttachments() {
        if (documentsToAttach == null)
            return new ArrayList<StoredDocument>();
        return documentsToAttach.values();
    }

    String mkCid(int i) {
        return "doc" + i +"@ihexds.nist.gov";
    }

    public void addDocumentAttachment(StoredDocument sd) {
        if (documentsToAttach == null)
            documentsToAttach = new HashMap<String, StoredDocument>();

        sd.cid = mkCid(documentsToAttach.size() + 1);

        documentsToAttach.put(sd.cid, sd);
    }

    /**
     * Insert document Includes into DocumentResponse cluster
     * @param env
     * @throws MetadataException
     */
    void insertDocumentIncludes(OMElement env, ErrorRecorder er) throws MetadataException {
        if (documentsToAttach == null)
            return;

        List<OMElement> docResponses = XmlUtil.decendentsWithLocalName(env, "DocumentResponse");
        Map<String, OMElement> uidToDocResp = new HashMap<String, OMElement>();

        for (OMElement docResp : docResponses) {
            OMElement docUidEle = XmlUtil.firstChildWithLocalName(docResp, "DocumentUniqueId");
            if (docUidEle == null) {
                er.err(XdsErrorCode.Code.XDSRepositoryError, "Internal Error: response does not have DocumentUniqueId element", "SimCommon#insertDocumentIncludes", null);
                continue;
            }
            String docUid = docUidEle.getText();
            uidToDocResp.put(docUid, docResp);
        }

        for (String cid : documentsToAttach.keySet()) {
            String uid = documentsToAttach.get(cid).getUid();
            OMElement docResp = uidToDocResp.get(uid);
            if (docResp == null) {
                er.err(XdsErrorCode.Code.XDSRepositoryError, "Internal Error: response does not have Document for " + uid, "SimCommon#insertDocumentIncludes", null);
                continue;
            }
            OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
            OMElement incl = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
            incl.addAttribute("href", "cid:" + cid, null);
            doc.addChild(incl);
            docResp.addChild(doc);
        }
    }

    /**
     * Used to build RetrieveDocumentSetRespoinse
     * @param env
     * @param er
     * @return
     */
    public StringBuffer wrapSoapEnvelopeInMultipartResponse(OMElement env, ErrorRecorder er) {
	logger.debug("DsSimCommon#wrapSoapEnvelopeInMultipartResponse");

        er.detail("Wrapping in Multipart");

        // build body
        String boundary = "MIMEBoundary112233445566778899";
        StringBuffer contentTypeBuffer = new StringBuffer();
        String rn = "\r\n";

        contentTypeBuffer
                .append("multipart/related")
                .append("; boundary=")
                .append(boundary)
                .append(";  type=\"application/xop+xml\"")
                .append("; start=\"<" + mkCid(0) + ">\"")
                .append("; start-info=\"application/soap+xml\"");

        simCommon.response.setHeader("Content-Type", contentTypeBuffer.toString());

        StringBuffer body = new StringBuffer();

        body.append("--").append(boundary).append(rn);
        body.append("Content-Type: application/xop+xml; charset=UTF-8; type=\"application/soap+xml\"").append(rn);
        body.append("Content-Transfer-Encoding: binary").append(rn);
        body.append("Content-ID: <" + mkCid(0) + ">").append(rn);
        body.append(rn);

        body.append(env.toString());

        body.append(rn);
        body.append(rn);

        if (documentsToAttach != null) {
            er.detail("Attaching " + documentsToAttach.size() + " documents as separate Parts in the Multipart");
            for (String cid : documentsToAttach.keySet()) {
                StoredDocument sd = documentsToAttach.get(cid);

                body.append("--").append(boundary).append(rn);
                body.append("Content-Type: ").append(sd.getMimeType()).append(rn);
                body.append("Content-Transfer-Encoding: binary").append(rn);
                body.append("Content-ID: <" + cid + ">").append(rn);
                body.append(rn);
                try {
                    String contents;
                    if (sd.getCharset() != null) {
                        contents = new String(sd.getContent(), sd.getCharset());
                    } else {
                        contents = new String(sd.getContent());
                    }
		    logger.debug("Attaching " + cid + " length " + contents.length());
                    body.append(contents);
                } catch (Exception e) {
                    er.err(XdsErrorCode.Code.XDSRepositoryError, e);
                }
                body.append(rn);
            }
        }


        body.append("--").append(boundary).append("--").append(rn);

        return body;
    }

    /**
     * Used to build RetrieveDocumentSetRespoinse
     * @param env
     * @param er
     * @return
     */
    public StringBuffer wrapSoapEnvelopeInMultipartResponseBinary(OMElement env, ErrorRecorder er) {
	logger.debug("DsSimCommon#wrapSoapEnvelopeInMultipartResponseBinary");

        er.detail("Wrapping in Multipart");

        // build body
        String boundary = "MIMEBoundary112233445566778899";
        StringBuffer contentTypeBuffer = new StringBuffer();
        String rn = "\r\n";

        contentTypeBuffer
                .append("multipart/related")
                .append("; boundary=")
                .append(boundary)
                .append(";  type=\"application/xop+xml\"")
                .append("; start=\"<" + mkCid(0) + ">\"")
                .append("; start-info=\"application/soap+xml\"");

        simCommon.response.setHeader("Content-Type", contentTypeBuffer.toString());

        StringBuffer body = new StringBuffer();

        body.append("--").append(boundary).append(rn);
        body.append("Content-Type: application/xop+xml; charset=UTF-8; type=\"application/soap+xml\"").append(rn);
        body.append("Content-Transfer-Encoding: binary").append(rn);
        body.append("Content-ID: <" + mkCid(0) + ">").append(rn);
        body.append(rn);

        body.append(env.toString());

        body.append(rn);
        body.append(rn);

/*
        if (documentsToAttach != null) {
            er.detail("Attaching " + documentsToAttach.size() + " documents as separate Parts in the Multipart");
            for (String cid : documentsToAttach.keySet()) {
                StoredDocument sd = documentsToAttach.get(cid);

                body.append("--").append(boundary).append(rn);
                body.append("Content-Type: ").append(sd.getMimeType()).append(rn);
                body.append("Content-Transfer-Encoding: binary").append(rn);
                body.append("Content-ID: <" + cid + ">").append(rn);
                body.append(rn);
                try {
                    String contents;
                    if (sd.getCharset() != null) {
                        contents = new String(sd.getContent(), sd.getCharset());
                    } else {
                        contents = new String(sd.getContent());
                    }
		    logger.debug("Attaching " + cid + " length " + contents.length());
                    body.append(contents);
                } catch (Exception e) {
                    er.err(XdsErrorCode.Code.XDSRepositoryError, e);
                }
                body.append(rn);
            }
        }
*/


//        body.append("--").append(boundary).append("--").append(rn);

        return body;
    }
    public StringBuffer getTrailer() {
        String rn = "\r\n";
        String boundary = "MIMEBoundary112233445566778899";
	StringBuffer body = new StringBuffer();
        body.append("--").append(boundary).append("--").append(rn);
	return body;
    }

    public void sendFault(SoapFault fault) {
        OMElement env = wrapResponseInSoapEnvelope(fault.getXML());
        sendHttpResponse(env, simCommon.getUnconnectedErrorRecorder(), true);
    }

    /**
     * Generate HTTP wrapper in correct format (SIMPLE or MTOM)
     * @param env SOAPEnvelope
     * @param er
     * @param multipartOk
     * @throws IOException
     */
    public void sendHttpResponse(OMElement env, ErrorRecorder er, boolean multipartOk)  {
        if (simCommon.responseSent) {
            // this should never happen
            logger.fatal(ExceptionUtil.here("Attempted to send second response"));
            return;
        }
        simCommon.responseSent = true;
        String respStr;
        logger.info("vc is " + simCommon.vc);
        logger.info("multipartOk is " + multipartOk);
        if (simCommon.vc != null && simCommon.vc.requiresMtom() && multipartOk) {
            StringBuffer body = wrapSoapEnvelopeInMultipartResponseBinary(env, er);

            respStr = body.toString();
        } else {
            respStr = new OMFormatter(env).toString();
        }
        try {
            if (simCommon.db != null)
                Io.stringToFile(simCommon.db.getResponseBodyFile(), respStr);
            simCommon.os.write(respStr.getBytes());
            if (simCommon.vc.requiresMtom()) {
                this.writeAttachments(simCommon.os, er);
                simCommon.os.write(getTrailer().toString().getBytes());
            }
            simCommon.generateLog();
//            SimulatorConfigElement callbackElement = getSimulatorConfig().getRetrievedDocumentsModel(SimulatorConfig.TRANSACTION_NOTIFICATION_URI);
//            if (callbackElement != null) {
//                String callbackURI = callbackElement.asString();
//                if (callbackURI != null && !callbackURI.equals("")) {
//                    new Callback().notify(simCommon.db, getSimulatorConfig(), callbackURI);
//                }
//            }
        } catch (IOException e) {
            logger.fatal(ExceptionUtil.exception_details(e));
        }
    }
    private void writeAttachments(OutputStream os, ErrorRecorder er) {
        String boundary = "MIMEBoundary112233445566778899";
        String rn = "\r\n";
    try {

        if (documentsToAttach != null) {
            er.detail("Attaching " + documentsToAttach.size() + " documents as separate Parts in the Multipart");
            for (String cid : documentsToAttach.keySet()) {
		StringBuffer body = new StringBuffer();
                StoredDocument sd = documentsToAttach.get(cid);

                body.append("--").append(boundary).append(rn);
                body.append("Content-Type: ").append(sd.getMimeType()).append(rn);
                body.append("Content-Transfer-Encoding: binary").append(rn);
                body.append("Content-ID: <" + cid + ">").append(rn);
                body.append(rn);
		os.write(body.toString().getBytes());
		os.write(sd.getContent());
		os.write(rn.getBytes());
/*
                try {
                    String contents = "ZZZ";

                    if (sd.getCharset() != null) {
                        contents = new String(sd.getContent(), sd.getCharset());
                    } else {
                        contents = new String(sd.getContent());
                    }

		    logger.debug("Attaching " + cid + " length " + contents.length());
                    body.append(contents);
                } catch (Exception e) {
                    er.err(XdsErrorCode.Code.XDSRepositoryError, e);
                }
                body.append(rn);
*/
            }
        }

    } catch (Exception e) {
            logger.fatal(ExceptionUtil.exception_details(e));
    }
    }

    // TODO Assertions
    public ErrorRecorder registryResponseAsErrorRecorder(OMElement regResp) {
        ErrorRecorder er = simCommon.getUnconnectedErrorRecorder();

        for (OMElement re : XmlUtil.decendentsWithLocalName(regResp, "RegistryError")) {
            String errorCode   = re.getAttributeValue(MetadataSupport.error_code_qname);
            String codeContext = re.getAttributeValue(MetadataSupport.code_context_qname);
            String location    = re.getAttributeValue(MetadataSupport.location_qname);
            String severity    = re.getAttributeValue(MetadataSupport.severity_qname);
            String resource    = "";
            er.err(errorCode, codeContext, location, severity, resource);
        }

        return er;
    }

    /**
     * Send a SOAP Fault if soap errors are present
     * @return fault sent?
     * @throws IOException
     */
    public boolean returnFaultIfNeeded() throws IOException {
        if (simCommon.faultReturned) return false;
        SoapFault fault = getSoapErrors();
        if (fault != null) {
            sendFault(fault);
            simCommon.faultReturned = true;
            return true;
        }
        return false;
    }

    public boolean isFaultNeeded() {
        SoapFault fault = getSoapErrors();
        return fault != null;
    }

    /**
     * Send a SOAP Fault
     * @param description description of problem
     * @param e exception causing fault
     */
    public void sendFault(String description, Exception e) {
        logger.info("Sending SoapFault - " + description + " - " + ((e == null) ? "" : ExceptionUtil.exception_details(e)));
        SoapFault fault = new SoapFault(SoapFault.FaultCodes.Receiver, "InternalError: Exception building Response: " + description + " : " + ((e == null) ? "" : e.getMessage()));
        sendFault(fault);
    }



    /**
     * Generate HTTP wrapper in correct format (SIMPLE or MTOM)
     * @param env SOAPEnvelope
     * @param er
     * @throws IOException
     */
    public void sendHttpResponse(OMElement env, ErrorRecorder er) throws IOException {
        sendHttpResponse(env, er, true);
    }

    public void intallDocumentsToAttach(StoredDocumentMap docmap) {
        documentsToAttach = new HashMap<>();
        for (StoredDocument stor : docmap.docs) {
            documentsToAttach.put(stor.cid, stor);
        }
    }

    /**
     * Return a SoapFault instance containing the errors logged so far.
     * Note that only errors from certain validators cause SOAP Faults.
     * Errors from other validators should be returned in a way that
     * is specific to that messaging architecture.
     * Returns null
     * if no errors found.
     * @return
     */
    SoapFault getSoapErrors() {

        SoapFault sf;

        sf = getFaultFromMessageValidator(HttpMessageValidator.class);
        if (sf != null) return sf;

        sf = getFaultFromMessageValidator(SoapMessageValidator.class);
        if (sf != null) return sf;

        sf = getFaultFromMessageValidator(SimpleSoapHttpHeaderValidator.class);
        if (sf != null) return sf;

        sf = getFaultFromMessageValidator(MtomMessageValidator.class);
        if (sf != null) return sf;

        return null;

    }

    /**
     * Examine simulator/validator step defined by designated class
     * and if error(s) is found then generate SOAP fault message.
     * @param clas Java class to look for on simulator stack
     * @return SoapFault instance
     */
    SoapFault getFaultFromMessageValidator(Class clas) {
        AbstractMessageValidator mv = simCommon.getMessageValidatorIfAvailable(clas);
        if (mv == null) {
            logger.debug("MessageValidator for " + clas.getName() + " not found");
            return null;
        }

        ErrorRecorder er = mv.getErrorRecorder();
        if (!(er instanceof GwtErrorRecorder)) {
            SoapFault fault = new SoapFault(SoapFault.FaultCodes.Receiver, "InternalError: Simulator: ErrorRecorder not instance of GwtErrorRecorder");
            return fault;
        }

        logger.debug("Found error recorder for " + clas.getName());
        GwtErrorRecorder ger = (GwtErrorRecorder) er;
        if (ger.hasErrorsOrContext()) {
            logger.debug("has errors or context");
            SoapFault fault = new SoapFault(SoapFault.FaultCodes.Sender, "Header/Format Validation errors reported by " + clas.getSimpleName());
            for (GwtValidatorErrorItem vei : ger.getValidatorErrorItems()) {
                logger.debug(vei.toString());
                String resource = vei.resource;
                if (!vei.isErrorOrContext())
                    continue;
                if (resource == null || "".equals(resource))
                    fault.addDetail(vei.getReportable());
                else
                    fault.addDetail(vei.getReportable() + "(" + resource + ")");
            }
            return fault;
        }
        return null;
    }

	public StoredDocument getStoredImagingDocument(String compositeUid, List<String> transferSyntaxUids) {
		logger.debug("DsSimCommon#getStoredImagingDocument: " + compositeUid);
		String[] uids = compositeUid.split(":");
		String path = "/opt/xdsi/storage/ids-repository/" + uids[0] + "/" + uids[1] + "/" + uids[2];
		logger.debug(" " + path);
		File folder = new File(path);
		if (!folder.exists()) {
			logger.debug("Could not find file folder for composite UID: " + compositeUid);
			return null;
		}
		boolean found = false;
		Iterator<String> it = transferSyntaxUids.iterator();
		String finalPath = null;
		while (it.hasNext() && !found) {
			String x = it.next();
			finalPath = path + "/" + x;
			File f = new File(finalPath);
			if (f.exists()) {
				found = true;
			}
		}
		StoredDocument sd = null;
		if (found) {
			logger.debug("Found path to file: " + finalPath);
			StoredDocumentInt sdi = new StoredDocumentInt();
//			sdi.pathToDocument = "/tmp/000000.dcm";
			sdi.pathToDocument = finalPath;
			sdi.uid = uids[2];
			logger.debug(" Instance UID: " + sdi.uid);
			sdi.mimeType = "application/dicom";
			sdi.charset = "UTF-8";
//			sdi.hash="0000";
//			sdi.size = "4";
			sdi.content = null;
			sd = new StoredDocument(sdi);
//			sd.cid = mkCid(5);
		} else {
			logger.debug("Did not find an image file that matched transfer syntax");
			logger.debug(" Composite UID: " + compositeUid);
			it = transferSyntaxUids.iterator();
			while (it.hasNext()) {
				logger.debug("  Xfer syntax: " + it.next());
			}
		}
		return sd;
	}

/*
	public StoredDocument getStoredImagingDocument(String uid) {
		logger.debug("DsSimCommon#getStoredImagingDocument(1 arg): " + uid);
		StoredDocumentInt sdi = new StoredDocumentInt();
		sdi.pathToDocument = "/tmp/000000.dcm";
		sdi.uid = uid;
		sdi.mimeType = "application/dicom";
		sdi.charset = "UTF-8";
//		sdi.hash="0000";
//		sdi.size = "4";
		sdi.content = null;
		StoredDocument sd = new StoredDocument(sdi);
//		sd.cid = mkCid(5);
		return sd;
	}
*/




}
