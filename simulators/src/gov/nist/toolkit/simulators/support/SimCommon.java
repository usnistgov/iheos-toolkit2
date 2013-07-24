package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.ValidationStepResult;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrymsg.registry.RegistryResponse;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseSendingSim;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.soap.http.SoapUtil;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valregmsg.message.HttpMessageValidator;
import gov.nist.toolkit.valregmsg.message.MtomMessageValidator;
import gov.nist.toolkit.valregmsg.message.SimpleSoapMessageValidator;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.service.SoapActionFactory;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine.ValidationStep;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.message.NullMessageValidator;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

/**
 * All simulators are passed an instance of this class in the constructor giving
 * the simulator access to all the necessary goodies.
 * @author bill
 *
 */
public class SimCommon {
	public SimDb db = null;
	boolean tls = false;
	MessageValidationResults mvr = null;
	public MessageValidatorEngine mvc = null;
	//	SoapMessageValidator smv = null;
	public ValidationContext vc = null;
	HttpServletResponse response = null;
	OutputStream os = null;
	ValidateMessageService vms = null;
	public RegIndex regIndex = null;
	public RepIndex repIndex = null;
	boolean faultReturned = false;
	boolean responseSent = false;
	Map<String, StoredDocument> documentsToAttach = null;  // cid => document
	static Logger logger = Logger.getLogger(SimCommon.class);
	RegistryErrorListGenerator relGen = null;
	

	public boolean isResponseSent() {
		return faultReturned || responseSent;
	}

	public void sendErrorsInRegistryResponse(ErrorRecorder er) {
		if (er == null)
			er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();


		// this works when RegistryResponse is the return message
		// need other options for other messaging environments
		mvc.addMessageValidator("Send RegistryResponse with errors", new RegistryResponseSendingSim(this), er);

		mvc.run();
	}

	public void intallDocumentsToAttach(StoredDocumentMap docmap) {
		documentsToAttach = new HashMap<String, StoredDocument>();
		for (StoredDocument stor : docmap.docs) {
			documentsToAttach.put(stor.cid, stor);
		}
	}


	/**
	 * Build a new simulator support object
	 * @param db the simulator database object supporting this simulator
	 * @param tls is tls employed
	 * @param vc validation context used to validate the input message
	 * @param mvc message validation engine
	 * @param regIndex registry index if needed (caller decides)
	 * @param repIndex repository index if needed (caller decides)
	 * @param response HttpServletResponse object for accepting eventual output 
	 * @throws IOException
	 * @throws XdsException 
	 */
	public SimCommon(SimDb db, boolean tls, ValidationContext vc, MessageValidatorEngine mvc, RegIndex regIndex, RepIndex repIndex, HttpServletResponse response) throws IOException, XdsException {

		this.db = db;
		this.tls = tls;
		this.vc = vc;
		this.mvc = mvc;
		this.regIndex = regIndex;
		this.repIndex = repIndex;
		this.response = response;
		if (response != null)
			this.os = response.getOutputStream();

		if (regIndex != null) {
			regIndex.setSimDb(db);
			regIndex.mc.regIndex = regIndex;
			regIndex.mc.vc = vc;
		}

		vms = new ValidateMessageService(null, regIndex);

	}

	/**
	 * Is TLS enabled?
	 * @return
	 */
	public boolean isTls() { return tls; }

	/**
	 * Return current validation context.
	 * @return
	 */
	public ValidationContext getValidationContext() {
		return vc;
	}


	/**
	 * Used only to issue soap faults, don't have enough context to do more
	 * @param response
	 * @throws IOException
	 */

	public SimCommon(HttpServletResponse response) throws IOException {
		this.response = response;
		if (response != null)
			os = response.getOutputStream();
	}

	/**
	 * Starts the validation process by scheduling the HTTP parser. This is called
	 * once per input message only.
	 * Returns status indicating whether it is ok to continue.  If false then exit
	 * immediately without returning a message.  A SOAPFault has already been returned;
	 * @return true if successful and false if fault sent
	 * @throws IOException
	 */
	public boolean runInitialValidations() throws IOException {
		mvc = vms.runValidation(vc, db, mvc);
		mvc.run();
		buildMVR();

		int stepsWithErrors = mvc.getErroredStepCount();
		ValidationStep lastValidationStep = mvc.getLastValidationStep();
		if (lastValidationStep != null) {
			lastValidationStep.getErrorRecorder().detail
			(stepsWithErrors + " steps with errors");
			logger.debug(stepsWithErrors + " steps with errors");
		} else {
			logger.debug("no steps with errors");
		}

		boolean sent = returnFaultIfNeeded();
		if (sent)
			faultReturned = true;
		return !sent;
	}

	ErrorRecorder er = null;

	public ErrorRecorder getCommonErrorRecorder() {
		if (er == null) {
			er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
			NullMessageValidator val = new NullMessageValidator(vc);
			mvc.addMessageValidator("Default ErrorRecorder", val, er);
		}

		return er;
	}


	/**
	 * Build the collection of error/statuses/messages for the validation steps
	 * so far. 
	 */

	void buildMVR() {
		mvr = vms.getMessageValidationResults(mvc);
	}


	/**
	 * Get the collection of error/statuses/messages for the validation steps
	 * recorded so far. 
	 */

	public MessageValidationResults getMessageValidationResults() {
		buildMVR();
		return mvr;
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
		//		smv = (SoapMessageValidator) getMessageValidator(SoapMessageValidator.class);
		//
		//		if (smv == null) {
		//			SoapFault fault = new SoapFault(SoapFault.FaultCodes.Receiver, "InternalError: Simulator: Did not find SoapMessageValidator on validator stack");
		//			List<String> names =  mvc.getValidatorNames();
		//			fault.addDetail("Validators run: " + names);
		//			return fault;
		//		}

		//		if (smv == null) {
		//			SoapFault fault = new SoapFault(SoapFault.FaultCodes.Receiver, "InternalError: Simulator: Did not find SoapMessageValidator on validator stack");
		//			List<String> names =  mvc.getValidatorNames();
		//			fault.addDetail("Validators run: " + names);
		//			return fault;
		//		}

		//TODO CHECK IF DESIRABLE: WE SEEMS TO RETURN ONLY THR FIRST ERROR FOUND. - @Antoine
		
		SoapFault sf;
		
		sf = getFaultFromMessageValidator(HttpMessageValidator.class);
		if (sf != null) return sf;

		sf = getFaultFromMessageValidator(SoapMessageValidator.class);
		if (sf != null) return sf;

		sf = getFaultFromMessageValidator(SimpleSoapMessageValidator.class);
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
		MessageValidator mv = getMessageValidator(clas);
		//		SoapMessageValidator smv = (SoapMessageValidator) getMessageValidator(SoapMessageValidator.class);


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
		if (ger.hasErrors()) {

			logger.debug("has errors");
			SoapFault fault = new SoapFault(SoapFault.FaultCodes.Sender, "Problem parsing input in validator " + clas.getName());
			for (ValidatorErrorItem vei : ger.getValidatorErrorInfo()) {

				String msg = vei.msg;
				String resource = vei.resource;
				if (resource == null)
					fault.addDetail(msg);
				else
					fault.addDetail(msg + "(" + resource + ")");
			}

			return fault;
		} 

		return null;
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
			smv = (SoapMessageValidator) getMessageValidator(SoapMessageValidator.class);
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

	/**
	 * Return the collection of results/status/errors
	 * @return
	 */
	public List<ValidationStepResult> getErrors() {
		buildMVR();
		return mvr.getResults();
	}

	/**
	 * Examine simulator stack - errors found?
	 * @return
	 */
	public boolean hasErrors() {
		buildMVR();
		return mvr.hasErrors();
	}

	//	AdhocQueryResponse getQueryResponse() throws XdsInternalException {
	//		buildMVR();
	//		return new AdhocQueryResponse(Response.version_3,SimUtil.getRegistryErrorList(mvr.getResults()));
	//	}
	//
	public RegistryResponse getRegistryResponse () throws XdsInternalException {
		buildMVR();
		return getRegistryResponse(mvr.getResults());
	}

	public RegistryErrorListGenerator getRegistryErrorList() throws XdsInternalException {
		return getRegistryErrorList(getErrors());
	}

	public void setRegistryErrorListGenerator(RegistryErrorListGenerator relg) {
		relGen = relg;
	}

	public RegistryErrorListGenerator getRegistryErrorList(List<ValidationStepResult> results) throws XdsInternalException {
		try {
			RegistryErrorListGenerator rel = relGen;
			if (rel == null)
				rel = new RegistryErrorListGenerator(Response.version_3, false);

			for (ValidationStepResult vsr : results) {
				for (ValidatorErrorItem vei : vsr.er) {
					if (vei.level == ValidatorErrorItem.ReportingLevel.ERROR) {
						String msg = vei.msg;
						if (vei.resource != null && !vei.resource.equals(""))
							msg = msg + " (" + vei.resource + ")";
						rel.add_error(vei.getCodeString(), vei.msg, vei.location, null, null);
					}
					if (vei.level == ValidatorErrorItem.ReportingLevel.WARNING) {
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
			relGen = null;
		}
	}

	public RegistryResponse getRegistryResponse(List<ValidationStepResult> results) throws XdsInternalException {
		RegistryErrorListGenerator rel = getRegistryErrorList(results); 
		RegistryResponse rr = new RegistryResponse(Response.version_3, rel);
		return rr;
	}

	/**
	 * Send a SOAP Fault if soap errors are present
	 * @return fault sent?
	 * @throws IOException
	 */
	public boolean returnFaultIfNeeded() throws IOException {
		SoapFault fault = getSoapErrors();
		if (fault != null) {
			sendFault(fault);
			return true;
		}
		return false;
	}

	/**
	 * Send a SOAP Fault 
	 * @param description description of problem
	 * @param e exception causing fault
	 */
	public void sendFault(String description, Exception e) {
		SoapFault fault = new SoapFault(SoapFault.FaultCodes.Receiver, "InteralError: Exception building Response: " + description + " : " + ((e == null) ? "" : e.getMessage()));
		sendFault(fault);
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
			er.err(Code.XDSRepositoryError, "SimCommon#addDocumentAttachment: Cannot extract DocumentEntry.uniqueId from metadata stored in simulator", this, "Internal Error");
		}
	}

	public void addDocumentAttachments(List<String> uids, ErrorRecorder er) {
		for (String uid : uids) {
			StoredDocument sd = repIndex.getDocumentCollection().getStoredDocument(uid);
			if (sd == null)
				continue;
			addDocumentAttachment(sd);
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

		List<OMElement> docResponses = MetadataSupport.decendentsWithLocalName(env, "DocumentResponse");
		Map<String, OMElement> uidToDocResp = new HashMap<String, OMElement>();

		for (OMElement docResp : docResponses) {
			OMElement docUidEle = MetadataSupport.firstChildWithLocalName(docResp, "DocumentUniqueId");
			if (docUidEle == null) {
				er.err(Code.XDSRepositoryError, "Internal Error: response does not have DocumentUniqueId element", "SimCommon#insertDocumentIncludes", null);
				continue;
			}
			String docUid = docUidEle.getText();
			uidToDocResp.put(docUid, docResp);
		}

		for (String cid : documentsToAttach.keySet()) {
			String uid = documentsToAttach.get(cid).uid;
			OMElement docResp = uidToDocResp.get(uid);
			if (docResp == null) {
				er.err(Code.XDSRepositoryError, "Internal Error: response does not have Document for " + uid, "SimCommon#insertDocumentIncludes", null);
				continue;
			}
			OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.document_qnamens);
			OMElement incl = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
			incl.addAttribute("href", "cid:" + cid, null);
			doc.addChild(incl);
			docResp.addChild(doc);
		}

		//		Metadata m = null;
		//		m = MetadataParser.parseNonSubmission(env);
		//
		//		int i=1;
		//
		//		for (OMElement eo : m.getExtrinsicObjects()) {
		//			eo.setNamespace(MetadataSupport.epsos_rimext_ns);
		//			String uid = m.getUniqueIdValue(eo);
		//			if (uid == null || uid.equals(""))
		//				break;
		//
		//			for (String cid : documentsToAttach.keySet()) {
		//				StoredDocument sd = documentsToAttach.get(cid);
		//				if (uid.equals(sd.uid)) {
		//					OMElement doc = MetadataSupport.om_factory.createOMElement(MetadataSupport.epsos_document_qnamens);
		//					OMElement incl = MetadataSupport.om_factory.createOMElement(MetadataSupport.xop_include_qnamens);
		//					doc.addChild(incl);
		//					incl.addAttribute("href", "cid:" + cid, null);
		//
		//					eo.addChild(doc);
		//				}
		//				i++;
		//			}
		//		}

	}

	/**
	 * Used to build RetrieveDocumentSetRespoinse
	 * @param env
	 * @param er
	 * @return
	 */
	public StringBuffer wrapSoapEnvelopeInMultipartResponse(OMElement env, ErrorRecorder er) {

		er.detail("Wrapping in Multipart");
		//		if (documentsToAttach != null)
		//			er.detail("Inserting XOP Includes");
		//		try {
		//			insertDocumentIncludes(env, er);
		//		} catch (MetadataException e) {
		//			logger.error("Wrap in multipart failed", e);
		//			er.err(Code.XDSRepositoryError, "SimCommon#wrapSoapEnvelopeInMultipartResponse: wrap in multipart failed", this, "Internal Error: " + e.getMessage());
		//		}


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

		response.setHeader("Content-Type", contentTypeBuffer.toString());

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
				body.append("Content-Type: ").append(sd.mimeType).append(rn);
				body.append("Content-Transfer-Encoding: binary").append(rn);
				body.append("Content-ID: <" + cid + ">").append(rn);
				body.append(rn);
				try {
					String contents;
					if (sd.charset != null) {
						contents = new String(sd.getDocumentContents(), sd.charset);
					} else {
						contents = new String(sd.getDocumentContents());
					}
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

	void sendFault(SoapFault fault) {
		OMElement env = wrapResponseInSoapEnvelope(fault.getXML());
		sendHttpResponse(env, getUnconnectedErrorRecorder(), false);
		//		response.setContentType("application/soap+xml");
		//		String respStr = env.toString();
		//		logger.debug("Sending SOAP Fault:\n" + respStr);
		//		
		//		try {
		//			os.write(respStr.getBytes());
		//		} catch (Exception e) {}
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

	/**
	 * Generate HTTP wrapper in correct format (SIMPLE or MTOM)
	 * @param env SOAPEnvelope
	 * @param er
	 * @param multipartOk
	 * @throws IOException
	 */
	public void sendHttpResponse(OMElement env, ErrorRecorder er, boolean multipartOk)  {
		if (responseSent) {
			// this should never happen
			logger.fatal(ExceptionUtil.here("Attempted to send second response"));
			return;
		}
		responseSent = true;
		String respStr;
		if (vc != null && vc.requiresMtom() && multipartOk) {
			StringBuffer body = wrapSoapEnvelopeInMultipartResponse(env, er);  

			respStr = body.toString();
		} else {
			respStr = new OMFormatter(env).toString();
		}
		try {
			if (db != null)
				Io.stringToFile(db.getResponseBodyFile(), respStr);
			os.write(respStr.getBytes());
			generateLog();
		} catch (IOException e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

	void generateLog() throws IOException {
		if (mvc == null || db == null)
			return;
		StringBuffer buf = new StringBuffer();

		//		buf.append(mvc.toString());

		Enumeration<ValidationStep> steps = mvc.getValidationStepEnumeration();
		while (steps.hasMoreElements()) {
			ValidationStep step = steps.nextElement();
			buf.append(step).append("\n");
			ErrorRecorder er = step.getErrorRecorder();
			if (er instanceof GwtErrorRecorder) {
				GwtErrorRecorder ger = (GwtErrorRecorder) er;
				buf.append(ger);
			}
		}


		Io.stringToFile(db.getLogFile(), buf.toString());
	}

	/**
	 * Get MessageValidator off validation queue that is an instance of clas.
	 * @param clas
	 * @return Matching MessageValidator
	 */
	public MessageValidator getMessageValidator(@SuppressWarnings("rawtypes") Class clas) {
		return mvc.findMessageValidator(clas.getCanonicalName());
	}

	public List<String> getValidatorNames() {
		return mvc.getValidatorNames();
	}

	public static ErrorRecorder getUnconnectedErrorRecorder() {
		return new GwtErrorRecorder();
	}

	public ErrorRecorder registryResponseAsErrorRecorder(OMElement regResp) {
		ErrorRecorder er = getUnconnectedErrorRecorder();

		for (OMElement re : MetadataSupport.decendentsWithLocalName(regResp, "RegistryError")) {
			String errorCode   = re.getAttributeValue(MetadataSupport.error_code_qname);
			String codeContext = re.getAttributeValue(MetadataSupport.code_context_qname);
			String location    = re.getAttributeValue(MetadataSupport.location_qname);
			String severity    = re.getAttributeValue(MetadataSupport.severity_qname);
			String resource    = "";
			er.err(errorCode, codeContext, location, severity, resource);
		}

		return er;
	}


}
