package gov.nist.toolkit.simcommon.server;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.ServiceRequestContainer;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * All simulators are passed an instance of this class in the constructor giving
 * the simulator access to all the necessary goodies.
 * @author bill
 *
 */

// NOTE: This should be limited to supporting functions for all simulators.
    // References to Regindex, RepIndex, RegistryErrorListGenerator, documentsToAttach should be refactored out
public class SimCommon {
	public SimDb db = null;
	private boolean tls = false;
	public ValidationContext vc = null;
	public SimulatorConfig simConfig = null;
	public HttpServletRequest request = null;
	public HttpServletResponse response = null;
	public OutputStream os = null;
	public boolean faultReturned = false;
	public boolean responseSent = false;
	private static Logger logger = Logger.getLogger(SimCommon.class);
	public MessageValidatorEngine mvc;
	public TransactionType transactionType;
	public ActorType actorType;

	public boolean isResponseSent() {
		return faultReturned || responseSent;
	}

   public SimCommon(SimDb db, SimulatorConfig simConfig, boolean tls, ValidationContext vc,
      HttpServletRequest request, HttpServletResponse response, MessageValidatorEngine mvc) throws IOException, XdsException {
      this(db, tls, vc, response, mvc);
      this.simConfig = simConfig;
      this.request = request;
      this.mvc = mvc;
   }


	/**
	 * Build a new simulator support model
	 * @param db the simulator database model supporting this simulator
	 * @param tls is tls employed
	 * @param vc validation context used to validate the input message
	 * @param response HttpServletResponse model for accepting eventual output
	 * @throws IOException
	 * @throws XdsException 
	 */
	public SimCommon(SimDb db, boolean tls, ValidationContext vc, HttpServletResponse response, MessageValidatorEngine mvc) throws IOException, XdsException {

		this.db = db;
		this.tls = tls;
		this.vc = vc;
		this.response = response;
		this.mvc = mvc;
		if (response != null)
			this.os = response.getOutputStream();
        logger.info(String.format("Connection is %s secure", (tls) ? "" : "not"));
	}

	public ErrorRecorder getCommonErrorRecorder() {
		if (er == null) {
			er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
			ServiceRequestContainer val = new ServiceRequestContainer(vc);
			mvc.addMessageValidator("Default ErrorRecorder", val, er);
		}

		return er;
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

	public void setValidationContext(ValidationContext vc) {
		this.vc = vc;
	}

	ErrorRecorder er = null;




	public static ErrorRecorder getUnconnectedErrorRecorder() {
		return new GwtErrorRecorder();
	}


	static public void deleteSim(SimId simulatorId) {
		try {
			logger.info("Delete sim " + simulatorId);
			SimDb simdb = new SimDb(simulatorId);
			File simdir = simdb.getIpDir();
			Io.delete(simdir);
		} catch (Exception e) {
			// doesn't exist - ok
		}
	}
	
	public void setLogger(Logger log) {
	   logger = log;
	}
	
	public void sendHttpFault(String em) {
	   sendHttpFault(400, em);
	}
	public void sendHttpFault(int status, String em) {
	   logger.info("HTTP Error response: " + status + " " + em);
	   try {
         response.sendError(status, em);
      } catch (IOException e) {
         logger.warn("IO error sending http response");
      }
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public ActorType getActorType() {
		return actorType;
	}

	public void setActorType(ActorType actorType) {
		this.actorType = actorType;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
	}
}
