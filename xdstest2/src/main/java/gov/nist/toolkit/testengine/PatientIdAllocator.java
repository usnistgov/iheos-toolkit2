package gov.nist.toolkit.testengine;

import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;


/**
 * @author bill
 *
 */
public class PatientIdAllocator extends IdAllocator {
	static String current_pid = null; 

	static Logger logger = Logger.getLogger(PatientIdAllocator.class);

	static public void reset() {
		current_pid = null;  // done at end of each test step
	}

	void loadCurrent() throws IOException, XdsInternalException  {
		current_pid =  loadPatientId();
	}

	public String useAlternatePatientId() throws IOException, XdsInternalException  {
		current_pid = loadAltPatientId();
		return current_pid;
	}

	public String useNewAlternatePatientId() throws XdsInternalException, FileNotFoundException {
		current_pid = getNewPatientId();
		saveAltPatientId(current_pid);
		return current_pid;
	}

	public PatientIdAllocator(TestConfig config) throws XdsInternalException {
		super(config);
	}


	public PatientIdAllocator(TestConfig config, String patient_id) {
		super(config);
		current_pid = patient_id;
	}

	void savePatientId(String patientId) throws XdsInternalException, FileNotFoundException {
		PrintStream ps = new PrintStream(patientIdFile);
		ps.print(patientId);
		ps.close();
	}

	String loadPatientId() throws IOException, XdsInternalException {
		//return Io.stringFromFile(patientIdFile).trim();
		String id = null;

		try {
			id = Io.stringFromFile(patientIdFile).trim();
		} catch (FileNotFoundException e) {
			// try to initialize 
			try {
				id = getNewPatientId();
			} catch (XdsInternalException e1) {
				// give up
				throw new XdsInternalException("Cannot allocate Patient ID", e1);
			}
			savePatientId(id);
		}
		return id;
	}

	void saveAltPatientId(String patientId) throws XdsInternalException, FileNotFoundException {
		PrintStream ps = new PrintStream(altPatientIdFile);
		ps.print(patientId);
		ps.close();
	}

	String loadAltPatientId() throws IOException, XdsInternalException {
		String id = null;

		try {
			id = Io.stringFromFile(altPatientIdFile).trim();
		} catch (FileNotFoundException e) {
			// try to initialize 
			try {
				id = getNewPatientId();
			} catch (XdsInternalException e1) {
				// give up
				throw new XdsInternalException("Cannot allocate Patient ID", e1);
			}
			saveAltPatientId(id);
		}
		return id;
	}

	public String getAltPatientId() throws XdsInternalException {
		try {
		return loadAltPatientId();
		} catch (IOException e) {
			throw new XdsInternalException("Cannot get alternate patient id", e);
		}
	}

	/**
	 * Return current Patient Id. This method is badly misnamed because it is part
	 * of a larger id allocation scheme.
	 */
	public String allocate() throws XdsInternalException {
		try {
			if (current_pid == null) {
				loadCurrent();
			}
			return current_pid;
		} catch (Exception e) {
			throw new XdsInternalException("Cannot load current patient id", e);
		}
	}

	public String useNewPatientId() throws XdsInternalException, FileNotFoundException {
		current_pid = getNewPatientId();
		this.savePatientId(current_pid);
		return current_pid;
	}

	public String getNewPatientId() throws XdsInternalException {
		String newPid = "";
		if (testConfig.pid_allocate_endpoint != null && !testConfig.pid_allocate_endpoint.equals("")) {
			try {
				logger.debug("Requesting new Patient ID from " + testConfig.pid_allocate_endpoint);
				newPid = HttpClient.httpGet(testConfig.pid_allocate_endpoint);
				newPid = newPid.trim();
			} catch (Exception e) {
				throw new XdsInternalException("Call to allocate new Patient Id failed: URI was " + testConfig.pid_allocate_endpoint ,e);
			}
		}
		else 
			System.out.println("WARNING: PID allocation service not configured (or disabled in code), using Patient ID coded in test or tool");
		return newPid;

	}

}
