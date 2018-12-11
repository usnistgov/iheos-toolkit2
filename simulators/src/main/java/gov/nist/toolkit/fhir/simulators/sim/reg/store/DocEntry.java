package gov.nist.toolkit.fhir.simulators.sim.reg.store;


import java.io.Serializable;


public class DocEntry extends PatientObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String lid;
	public int version;
	public String objecttype;

	public String hash;
	public String size;
	
	public String classCode;
	public String typeCode;
	public String practiceSettingCode;
	public String creationTime;
	public String serviceStartTime;
	public String serviceStopTime;
	public String healthcareFacilityTypeCode;
	public String[] eventCode;
	public String[] confidentialityCode;
	public String[] authorNames;
	public String formatCode;

	public String sourcePatientId;
	public String documentAvailability;
	public String repositoryUniqueId;

	public String[] referenceIdList;
	
	public String getType() {
		return "DocumentEntry";
	}
	

}

