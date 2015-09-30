package gov.nist.toolkit.adt;

/**
 *
 * @author mccaffrey
 */
public class HL7Address {

    private String parent = null;   //  uuid link to record... used in database but may not be used in actuality in java object

    private String streetAddress = null;
    private String otherDesignation = null;
    private String city = null;
    private String stateOrProvince = null;
    private String zipCode = null;
    private String country = null;
    private String countyOrParish = null;

    public HL7Address() {}


    public HL7Address(String parent) {
        this.setParent(parent);

    }

    /** Creates a new instance of HL7Address */
    public HL7Address(String parent, String streetAddress, String otherDesignation, String city, String stateOrProvince,
                      String zipCode, String country, String countyOrParish) {
        this.setParent(parent);
        this.setStreetAddress(streetAddress);
        this.setOtherDesignation(otherDesignation);
        this.setCity(city);
        this.setStateOrProvince(stateOrProvince);
        this.setZipCode(zipCode);
        this.setCountry(country);
        this.setCountyOrParish(countyOrParish);
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getOtherDesignation() {
        return otherDesignation;
    }

    public void setOtherDesignation(String otherDesignation) {
        this.otherDesignation = otherDesignation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountyOrParish() {
        return countyOrParish;
    }

    public void setCountyOrParish(String countyOrParish) {
        this.countyOrParish = countyOrParish;
    }

    // not complete
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getStreetAddress() + '\n');
        sb.append(this.getCity() + " " + this.getStateOrProvince() + this.getZipCode() + '\n');
        sb.append(this.getCountry() + '\n');
        return sb.toString();
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

}
