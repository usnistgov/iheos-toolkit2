package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class TestInstance implements IsSerializable, Serializable {
	String id = null;    //  id of the test
	String event = null;
	String eventDir;

	// params to re-create LogRepository
	String location = null;
	String user = null;
	LogIdIOFormat format = null;
	LogIdType idType = null;
	
	public TestInstance() {
		
	}

	public boolean linkedToLogRepository() { return location != null; }

	public void setEvent(String event) {
		this.event = event;
	}

	public void setLocation(String location) {
		this.location = location;
	}

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

	public String getUser() {
		return user;
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
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	// these should only be ued by TestIdBuilder which extends this class
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

	public String toString() { return event + ":" + id; }

}
