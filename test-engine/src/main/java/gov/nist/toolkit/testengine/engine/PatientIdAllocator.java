package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * @author bill
 *
 */
public class PatientIdAllocator extends IdAllocator {
	private static String current_pid = null;

	static Logger logger = Logger.getLogger(PatientIdAllocator.class);

	static public void reset() {
		current_pid = null;  // done at end of each test step
	}

	private void loadCurrent() throws IOException, XdsInternalException  {
//		current_pid =  loadPatientId();
	}

	public PatientIdAllocator(TestConfig config) throws XdsInternalException {
		super(config);
	}

	private PatientIdAllocator() { }

	public PatientIdAllocator(TestConfig config, String patient_id) {
		super(config);
		current_pid = patient_id;
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

	private String base;
	private int cnt;
	private static PatientIdAllocator patientIdAllocator;

	static {
		patientIdAllocator = new PatientIdAllocator();
		patientIdAllocator.base = "P" + new Hl7Date().now().substring(4);  //was too long - chop off the year
		patientIdAllocator.cnt = 1;
	}

	synchronized String alloc() {
		cnt++;
		return base + "." + String.valueOf(cnt);
	}

	static public Pid getNew(String assigningAuthorityOid) {
		return new Pid(assigningAuthorityOid, patientIdAllocator.alloc());
	}


}
