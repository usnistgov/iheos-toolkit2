package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Identify a test instance, the combination of a test definition and the state of the running test.
 */
public class TestInstance implements IsSerializable, Serializable {
	String id = null;    //  id of the test
	String section = null;  // this is optional
	String event = null;
	String eventDir;

	// params to re-create LogRepository
	String location = null;
	String user = null;
	LogIdIOFormat format = null;
	LogIdType idType = null;
	
	public TestInstance() {
	}

    public String describe() {
        StringBuilder buf = new StringBuilder();
        buf
                .append("TestInstance...\n")
                .append("...id = ").append(id).append("\n")
                .append("...event = ").append(event).append("\n")
                .append("...eventDir = ").append(eventDir).append("\n")
                .append("...location = ").append(location).append("\n")
                .append("...user = ").append(user).append("\n")
                .append("...type = ").append(idType).append("\n");
        return buf.toString();
    }

    public TestInstance copy() {
        TestInstance x = new TestInstance();
        x.id = id;
		x.section = section;
        x.event = event;
        x.eventDir = eventDir;
        x.location = location;
        x.user = user;
        x.format = format;
        x.idType = idType;
        return x;
    }

	public boolean linkedToLogRepository() { return location != null; }

    public boolean isTestCollection() { return id.startsWith("tc:"); }

	public void setEvent(String event) {
		this.event = event;
	}

	public void setLocation(String location) {
		this.location = location;
	}

    /**
     * Get the test user, also known as testSession in the UI
     * @return testSession as a string
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the test user, also known as the testSession in the UI
     * @param user testSession as a string
     */
    public void setUser(String user) {
		this.user = user;
	}

	public void setFormat(LogIdIOFormat format) {
		this.format = format;
	}

	public void setIdType(LogIdType idType) {
		this.idType = idType;
	}

	public String getLocation() {
		return location;
	}

	public LogIdIOFormat getFormat() {
		return format;
	}

	public LogIdType getIdType() {
		return idType;
	}

	public String getEvent() {
		return event;
	}

	public boolean isEmpty() { return id == null || id.equals(""); }

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	/**
     * Set the test identifier (11901 for example)
     * @param id the identifier
     */
	public void setId(String id) {
		this.id = id;
	}

    /**
     * Get the test identifier (11901 for example).
     * @return identifier as a string
     */
    public String getId() {
		return id;
	}

	// these should only be used by TestIdBuilder which extends this class
	// with stuff that cannot be part of GWT
	public String getInternalEvent() { return event; }
	public void setInternalEvent(String e) { event = e; }
	public String getEventDir() { return eventDir; }
	public void setEventDir(String eventDir) { this.eventDir = eventDir; }

	public TestInstance(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof TestInstance)) return false;
		TestInstance x = (TestInstance) o;
		if (id == null && x.id != null) return false;
		if (id != null && !id.equals(x.id)) return false;
		if (section == null && x.section != null) return false;
		if (section != null && !section.equals(x.section)) return false;
		if (event == null && x.event != null) return false;
		if (event != null && !event.equals(x.event)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return
				((id == null) ? 42 : id.hashCode()) +
						((event == null) ? 43 : event.hashCode());
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		if (event != null) buf.append(event);
		buf.append(':');
		if (section != null) buf.append(section);
		buf.append(':');
		buf.append(id);
		return buf.toString();
	}

}
