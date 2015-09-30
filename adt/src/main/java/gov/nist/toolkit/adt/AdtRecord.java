package gov.nist.toolkit.adt;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Currently for the purposes of XDS, an ADT patient record consists of only a
 * patient ID and the patient's name.  This class gives access to both pieces of
 * information.
 * July 2005 -- we're adding to it now. Now it is more than just id/name.
 * @author Andrew McCaffrey
 */
public class AdtRecord implements Serializable {

    private String patientId = null;
    private Collection patientNames = null;             // Collection of Hl7Name
    private String patientBirthDateTime = null;
    private String patientAdminSex = null;
    private String patientAccountNumber = null;
    private String patientBedId = null;
    private Collection patientRace = null;              // Collection of Hl7Race
    private Collection patientAddresses = null;         // Collection of Hl7Address

    /** Creates a new instance of AdtRecord */
    public AdtRecord() throws ClassNotFoundException, SQLException {
    }

    /**
     * Creates a new instance of AdtRecord, using parameters for patient ID
     * and patient name.
     * @param patientId The patient ID to set.
     * @param patientNames The patient name to set.
     */
    public AdtRecord(String patientId, Collection patientNames) {
        setPatientId(patientId);
        setPatientNames(patientNames);
    }

    public AdtRecord(String patientId) throws ClassNotFoundException, SQLException {
        this.setPatientId(patientId);
    }

    //FIX ME
    public AdtRecord(String patientId, Collection patientNames, String patientBirthDateTime, String patientAdminSex,
                     Collection patientAddresses, String patientAccountNumber, Collection patientRace, String patientBedId) {

        this.setPatientId(patientId);
        this.setPatientNames(patientNames);
        this.setPatientBirthDateTime(patientBirthDateTime);
        this.setPatientAdminSex(patientAdminSex);
        this.setPatientAddresses(patientAddresses);
        this.setPatientRace(patientRace);
        this.setPatientAccountNumber(patientAccountNumber);
        this.setPatientBedId(patientBedId);
    }

    /**
     * Getter for property patientId.
     * @return Value of property patientId.
     */
    public java.lang.String getPatientId() {
        return patientId;
    }

    /**
     * Setter for property patientId.
     * @param patientId New value of property patientId.
     */
    public void setPatientId(java.lang.String patientId) {
        this.patientId = patientId;
    }

    /**
     * Getter for property patientName.
     * @return Value of property patientName.
     */
    public Collection getPatientNames() {
        if(patientNames == null)
            patientNames = new ArrayList();

        return patientNames;
    }

    /**
     * Setter for property patientName.
     * @param patientNames New value of property patientName.
     */
    public void setPatientNames(Collection patientNames) {
        this.patientNames = patientNames;
    }

    public String getPatientBirthDateTime() {
        return patientBirthDateTime;
    }

    public void setPatientBirthDateTime(String patientBirthDateTime) {
        this.patientBirthDateTime = patientBirthDateTime;
    }

    public String getPatientAdminSex() {
        return patientAdminSex;
    }

    public void setPatientAdminSex(String patientAdminSex) {
        this.patientAdminSex = patientAdminSex;
    }

    public Collection getPatientAddresses() {
        if(patientAddresses == null)
            patientAddresses = new ArrayList();
        return patientAddresses;
    }

    public void setPatientAddresses(Collection patientAddresses) {
        this.patientAddresses = patientAddresses;
    }

    public String getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(String patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public String getPatientBedId() {
        return patientBedId;
    }

    public void setPatientBedId(String patientBedId) {
        this.patientBedId = patientBedId;
    }

    //   public String getTimestamp() {
    //       return timestamp;
    //   }

    //   public void setTimestamp(String timestamp) {
    //       this.timestamp = timestamp;
    //   }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Patient ID = " + this.getPatientId() + '\n');
        //       sb.append("Patient Name = " + this.getFullName().getPrefix() + " " + this.getFullName().getGivenName() + " ");
        //       sb.append(this.getFullName().getSecondAndFurtherName() + " " + this.getFullName().getFamilyName() + " ");
        //       sb.append(this.getFullName().getSuffix() + " " + this.getFullName().getDegree() + '\n');
        sb.append("Patient Birth Date/Time = " + this.getPatientBirthDateTime() + '\n');
        sb.append("Patient Admin Sex = " + this.getPatientAdminSex() + '\n');
        //       sb.append("Patient Address = " + this.getPatientAddress() + '\n');
        sb.append("Patient Account Number = " + this.getPatientAccountNumber() + '\n');
        sb.append("Patient Bed ID = " + this.getPatientBedId() + '\n');
        for(int i = 0; i < this.getPatientRace().size(); i++) {
            sb.append("Patient Race #" + (i + 1) + " = " + this.getPatientRace().toArray()[i] + '\n');
        }
        for(int i = 0; i < this.getPatientAddresses().size(); i++) {
            HL7Address address = (HL7Address) this.getPatientAddresses().toArray()[i];
            sb.append("Patient Address #" + (i + 1) + " = " + address.toString());


        }
        return sb.toString();
    }

    public Collection getPatientRace() {
        if(patientRace == null)
            patientRace = new ArrayList();
        return patientRace;
    }

    public void setPatientRace(Collection patientRace) {
        this.patientRace = patientRace;
    }

}
