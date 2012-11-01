/*
 * AdtJdbcConnection.java
 *
 * Created on October 4, 2004, 12:53 PM
 */

package gov.nist.toolkit.common.adt;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * For use in communicating with the ADT database.  At the moment, this is
 * hardcoded to talk to a PostgreSQL database, but this will become more
 * flexible in future releases.
 * @author Andrew McCaffrey
 */

public class AdtJdbcConnection {


	private String hostname = null;
	private static Connection con;
	private static Statement stmt;
	private boolean successfulConnection = false;

	/**
	 * Constant representing the name of the ADT database table.
	 */
	public static String ADT_MAIN_TABLE = "patient";
	public static String ADT_PATIENT_NAME_TABLE = "patientname";
	public static String ADT_PATIENT_ADDRESS_TABLE = "patientaddress";
	public static String ADT_PATIENT_RACE_TABLE = "patientrace";

	public static String ADT_MAIN_UUID = "uuid";
	/**
	 * Constant representing the patient ID column in the ADT database table.
	 */
	public static String ADT_MAIN_PATIENTID = "id";
	public static String ADT_MAIN_BIRTHDATETIME = "birthdatetime";
	public static String ADT_MAIN_ADMIN_SEX = "adminsex";
	public static String ADT_MAIN_ACCOUNT_NUMBER = "accountnumber";
	public static String ADT_MAIN_BED_ID = "bedid";


	public static String ADT_PATIENTNAME_PARENT = "parent";
	public static String ADT_PATIENTNAME_FAMILY_NAME = "familyname";
	public static String ADT_PATIENTNAME_GIVEN_NAME = "givenname";
	public static String ADT_PATIENTNAME_SECOND_AND_FURTHER_NAME = "secondandfurthername";
	public static String ADT_PATIENTNAME_SUFFIX = "suffix";
	public static String ADT_PATIENTNAME_PREFIX = "prefix";
	public static String ADT_PATIENTNAME_DEGREE = "degree";

	public static String ADT_PATIENTADDRESS_PARENT = "parent";
	public static String ADT_PATIENTADDRESS_STREET_ADDRESS = "streetaddress";
	public static String ADT_PATIENTADDRESS_OTHER_DESIGNATION = "otherdesignation";
	public static String ADT_PATIENTADDRESS_CITY = "city";
	public static String ADT_PATIENTADDRESS_STATE_OR_PROVINCE = "stateorprovince";
	public static String ADT_PATIENTADDRESS_ZIPCODE = "zipcode";
	public static String ADT_PATIENTADDRESS_COUNTRY = "country";
	public static String ADT_PATIENTADDRESS_COUNTY_OR_PARISH = "countyorparish";

	public static String ADT_PATIENTRACE_PARENT = "parent";
	public static String ADT_PATIENTRACE_RACE = "race";

	/**
	 * Creates a new instance of JdbcConnection
	 * @throws java.sql.SQLException Thrown if database access error.
	 */
	public AdtJdbcConnection() throws java.sql.SQLException, ClassNotFoundException {
		this.setHostname("localhost");
		this.initialize();
	}

	/**
	 * Creates a new instance of JdbcConnection
	 * @param hostname The hostname that houses the database.
	 * @throws java.sql.SQLException Thrown if there is a problem opening the database connection.
	 */
	public AdtJdbcConnection(String hostname) throws java.sql.SQLException, ClassNotFoundException {
		this.setHostname(hostname);
		this.initialize();
	}

	private void initialize() throws java.sql.SQLException, ClassNotFoundException {
		String user = "ebxmlrr";
		String pw = "";
		
		String suser = System.getenv("ADTUSER");
		String spw = System.getenv("ADTPW");
		
		if (suser != null && !suser.equals("")) {
			user = suser;
			pw = spw;
		}
		
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://" + this.getHostname() + "/adt";
		            System.out.println("Connecting to postres on url " + url + ", " + user + ", " + pw);
//		con = DriverManager.getConnection(url, "bill", "p1a2s3s4");
		con = DriverManager.getConnection(url, user, pw);
		stmt = con.createStatement();
		successfulConnection = true;

	}
	/**
	 * Close the connection.
	 * @throws java.sql.SQLException Thrown if database access error.
	 */
	public void close() throws SQLException {
		con.close();
	}


	private ResultSet executeQuery(String sql) throws SQLException {
		ResultSet result = null;
		result = stmt.executeQuery(sql);

		return result;
	}

	/**
	 * Executes the SQL update to the database.
	 * @param sql The SQL of the update.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return An int representing the number of rows affected by update.  (If zero,
	 * then no update occured.)
	 */
	public int executeUpdate(String sql) throws SQLException {
		int i = 0;
		i = stmt.executeUpdate(sql);

		return i;
	}
	
	public String getDate() throws SQLException {
		ResultSet result = executeQuery("SELECT DATE(CURRENT_TIMESTAMP)");
		result.next();
		return result.getString("date");
	}
	/**
	 * Check connection.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return
	 */
	//  public boolean isAlive() throws SQLException {

	//    return !con.isClosed();

	//  }

	/**
	 * Getter for property hostname.
	 * @return Value of property hostname.
	 */
	public java.lang.String getHostname() {
		return hostname;
	}

	/**
	 * Setter for property hostname.
	 * @param hostname New value of property hostname.
	 */
	public void setHostname(java.lang.String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Turn all single quote characters into escaped characters.  Needed because the
	 * single quote in SQL is what surrounds Strings.
	 * @param input The String to process.
	 * @return The safe version of the input.
	 */
	public static String makeSafe(String input) {
		String output = input.replaceAll("'", "\"");
		return output;
	}
	/**
	 * Adds the patient ID/patient name pair to the database.
	 * @param id The patient ID to add.
	 * @param name The patient name to add.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return Boolean.  True if update was successful.  False if update was not
	 * successful (patient ID already exists?).
	 */
	/*
    public boolean addIdName(String id, String name) throws SQLException {

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ");
        sb.append(this.ADT_MAIN_TABLE);
        sb.append(" (" + this.adtId + "," + this.adtName + ")");
        sb.append(" VALUES ");
        sb.append("('" + id + "','" + name + "');");
        int i = 0;
        i = this.executeUpdate(sb.toString());
        if (i > 0)
            return true;
        return false;

    }
	 */


	public boolean addAdtRecord(AdtRecord record) throws SQLException {

		Collection races = record.getPatientRace();
		Iterator itRace = races.iterator();
		while(itRace.hasNext()) {            
			Hl7Race race = (Hl7Race) itRace.next();
			this.addHl7Race(race);            
		}

		Collection names = record.getPatientNames();
		Iterator itName = names.iterator();
		while(itName.hasNext()) {
			Hl7Name name = (Hl7Name) itName.next();
			this.addHl7Name(name);
		}

		Collection addresses = record.getPatientAddresses();
		Iterator itAddress = addresses.iterator();
		while(itAddress.hasNext()) {
			Hl7Address address = (Hl7Address) itAddress.next();
			this.addHl7Address(address);
		}

		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(this.ADT_MAIN_TABLE);
		sb.append(" (" + this.ADT_MAIN_ACCOUNT_NUMBER + "," + this.ADT_MAIN_ADMIN_SEX + ",");
		sb.append(this.ADT_MAIN_BED_ID + "," + this.ADT_MAIN_BIRTHDATETIME + ",");
		sb.append(this.ADT_MAIN_PATIENTID + "," + this.ADT_MAIN_UUID + "," + "timestamp" + ")");
		sb.append(" VALUES ");
		sb.append("('" + record.getPatientAccountNumber() + "','" + record.getPatientAdminSex() + "','");
		sb.append(record.getPatientBedId() + "','" + record.getPatientBirthDateTime() + "','");
		sb.append(record.getPatientId() + "','" + record.getUuid() + "','" + getDate() + "');");

		int i = 0;
		i = this.executeUpdate(sb.toString());
		if(i > 0)
			return true;
		return false;                
	}

	public boolean addHl7Name(Hl7Name name) throws java.sql.SQLException {
		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(this.ADT_PATIENT_NAME_TABLE);
		sb.append(" (" + this.ADT_PATIENTNAME_DEGREE + "," + this.ADT_PATIENTNAME_FAMILY_NAME + ",");
		sb.append(this.ADT_PATIENTNAME_GIVEN_NAME + "," + this.ADT_PATIENTNAME_PARENT + ",");
		sb.append(this.ADT_PATIENTNAME_PREFIX + "," + this.ADT_PATIENTNAME_SECOND_AND_FURTHER_NAME + ",");
		sb.append(this.ADT_PATIENTNAME_SUFFIX + ")");
		sb.append(" VALUES ");
		sb.append("('" + name.getDegree() + "','" + name.getFamilyName() + "','");
		sb.append(name.getGivenName() + "','" + name.getParent() + "','");
		sb.append(name.getPrefix() + "','" + name.getSecondAndFurtherName() + "','");
		sb.append(name.getSuffix() + "');");
		int i = 0;
		i = this.executeUpdate(sb.toString());
		if(i > 0)
			return true;
		return false;

	}

	public boolean addHl7Address(Hl7Address address) throws java.sql.SQLException {
		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(this.ADT_PATIENT_ADDRESS_TABLE);
		sb.append(" (" + this.ADT_PATIENTADDRESS_CITY + "," + this.ADT_PATIENTADDRESS_COUNTRY + ",");
		sb.append(this.ADT_PATIENTADDRESS_COUNTY_OR_PARISH + "," + this.ADT_PATIENTADDRESS_OTHER_DESIGNATION + ",");
		sb.append(this.ADT_PATIENTADDRESS_PARENT + "," + this.ADT_PATIENTADDRESS_STATE_OR_PROVINCE + ",");
		sb.append(this.ADT_PATIENTADDRESS_STREET_ADDRESS + "," + this.ADT_PATIENTADDRESS_ZIPCODE + ")");
		sb.append(" VALUES ");        
		sb.append("('" + address.getCity() + "','" + address.getCountry() + "','");
		sb.append(address.getCountyOrParish() + "','" + address.getOtherDesignation() + "','");
		sb.append(address.getParent() + "','" + address.getStateOrProvince() + "','");
		sb.append(address.getStreetAddress() + "','" + address.getZipCode() + "');");

		int i = 0;
		i = this.executeUpdate(sb.toString());
		if(i > 0)
			return true;
		return false;        
	}

	public boolean addHl7Race(Hl7Race race) throws java.sql.SQLException {
		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(this.ADT_PATIENT_RACE_TABLE);
		sb.append(" (" + this.ADT_PATIENTRACE_PARENT + "," + this.ADT_PATIENTRACE_RACE + ")");
		sb.append(" VALUES ");
		sb.append("('" + race.getParent() + "','" + race.getRace() + "');");
		int i = 0;
		i = this.executeUpdate(sb.toString());
		if(i > 0)
			return true;
		return false;                
	}

	/**
	 * Queries the database to see if the given ID already exists in the database.
	 * This method does an exist, case-sensitive search only.  Use
	 * getInexactMatch(String) for wild-cards.
	 * @param id The ID to query on.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return Boolean.  True if ID does exist.  False if ID does not exist.
	 */
	public boolean doesIdExist(String id) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_MAIN_TABLE + " ");
		sb.append("WHERE " + this.ADT_MAIN_PATIENTID + " = '" + id + "';");

		ResultSet result = this.executeQuery(sb.toString());
		return result.next();

	}

	/**
	 * Queries on part of a patient ID to see if that partial String exists in the
	 * patient ID column.  Returns a Hashtable of id/names that match the search.
	 * @param search The partial String representing part of a patient ID to search for.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return A Hashtable of all matches or an empty Hashtable if none are found.
	 */
	public Hashtable getInexactMatch(String search) throws SQLException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_MAIN_TABLE + " ");
		sb.append("WHERE " + this.ADT_MAIN_PATIENTID + " Like '" + search + "';");

		ResultSet result = this.executeQuery(sb.toString());
		Hashtable table = new Hashtable();
		while(result.next()) {
			table.put(result.getString(this.ADT_MAIN_PATIENTID), "not completely implemented yet");
		}
		return table;
	}
	/**
	 * Returns the number of patients in the database.
	 * @throws java.sql.SQLException Thrown if database access error.
	 * @return An int representing the number of patients in the ADT database.
	 */
	public int getNumberPatients() throws SQLException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(\"" + this.ADT_MAIN_PATIENTID + "\") ");
		sb.append("FROM " + this.ADT_MAIN_TABLE);
		ResultSet result = this.executeQuery(sb.toString());

		return result.getInt("count");

	}

	public Collection getPatientNames(String uuid) throws SQLException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_PATIENT_NAME_TABLE + " ");
		sb.append("WHERE " + this.ADT_PATIENTNAME_PARENT + " = '" + uuid + "';");

		ResultSet result = this.executeQuery(sb.toString());
		Collection names = new ArrayList();

		while(result.next()) {
			Hl7Name name = new Hl7Name();

			name.setParent(result.getString(this.ADT_PATIENTNAME_PARENT));
			name.setDegree(result.getString(this.ADT_PATIENTNAME_DEGREE));
			name.setFamilyName(result.getString(this.ADT_PATIENTNAME_FAMILY_NAME));
			name.setGivenName(result.getString(this.ADT_PATIENTNAME_GIVEN_NAME));
			name.setPrefix(result.getString(this.ADT_PATIENTNAME_PREFIX));
			name.setSecondAndFurtherName(result.getString(this.ADT_PATIENTNAME_SECOND_AND_FURTHER_NAME));
			name.setSuffix(result.getString(this.ADT_PATIENTNAME_SUFFIX));

			names.add(name);

		}
		return names;
	}

	public Collection getPatientRaces(String uuid) throws SQLException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_PATIENT_RACE_TABLE + " ");
		sb.append("WHERE " + this.ADT_PATIENTRACE_PARENT + " = '" + uuid + "';");

		ResultSet result = this.executeQuery(sb.toString());
		Collection races = new ArrayList();

		while(result.next()) {
			Hl7Race race = new Hl7Race();

			race.setParent(result.getString(this.ADT_PATIENTRACE_PARENT));
			race.setRace(result.getString(this.ADT_PATIENTRACE_RACE));

			races.add(race);
		}
		return races;
	}

	public Collection getPatientAddresses(String uuid) throws SQLException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_PATIENT_ADDRESS_TABLE + " ");
		sb.append("WHERE " + this.ADT_PATIENTADDRESS_PARENT + " = '" + uuid + "';");

		ResultSet result = this.executeQuery(sb.toString());
		Collection addresses = new ArrayList();

		while(result.next()) {
			Hl7Address address = new Hl7Address();

			address.setParent(result.getString(this.ADT_PATIENTADDRESS_PARENT));
			address.setCity(result.getString(this.ADT_PATIENTADDRESS_CITY));
			address.setCountry(result.getString(this.ADT_PATIENTADDRESS_COUNTRY));
			address.setCountyOrParish(result.getString(this.ADT_PATIENTADDRESS_COUNTY_OR_PARISH));
			address.setOtherDesignation(result.getString(this.ADT_PATIENTADDRESS_OTHER_DESIGNATION));
			address.setStateOrProvince(result.getString(this.ADT_PATIENTADDRESS_STATE_OR_PROVINCE));
			address.setStreetAddress(result.getString(this.ADT_PATIENTADDRESS_STREET_ADDRESS));
			address.setZipCode(result.getString(this.ADT_PATIENTADDRESS_ZIPCODE));

			addresses.add(address);

		}
		return addresses;
	}

	public AdtRecord getAdtRecord(String uuid) throws SQLException, ClassNotFoundException {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * ");
		sb.append("FROM " + this.ADT_MAIN_TABLE + " ");
		sb.append("WHERE " + this.ADT_MAIN_UUID + " = '" + uuid + "';");

		ResultSet result = this.executeQuery(sb.toString());
		try {
			result.next();
		} catch (SQLException e) {
			// not found.
			return null;
		}

		AdtRecord record = new AdtRecord();

		record.setUuid(uuid);
		record.setPatientAccountNumber(result.getString(this.ADT_MAIN_ACCOUNT_NUMBER));
		record.setPatientAdminSex(result.getString(this.ADT_MAIN_ADMIN_SEX));
		record.setPatientBedId(result.getString(this.ADT_MAIN_BED_ID));
		record.setPatientBirthDateTime(result.getString(this.ADT_MAIN_BIRTHDATETIME));
		record.setPatientId(result.getString(this.ADT_MAIN_PATIENTID));

		record.setPatientAddresses(this.getPatientAddresses(uuid));
		record.setPatientNames(this.getPatientNames(uuid));
		record.setPatientRace(this.getPatientRaces(uuid));

		return record;

	}




	public static String getCurrentTimestamp() {
		return new Date().toString();
	}

}
