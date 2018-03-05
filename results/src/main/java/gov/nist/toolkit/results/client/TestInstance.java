package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestSession;

import java.io.Serializable;

/**
 * Identify a test instance, the combination of a test definition and the state
 * of the running test.
 */
public class TestInstance implements IsSerializable, Serializable {
	String id = null;    //  id of the test
	String section = null;  // this is optional
	String event = null;
	String eventDir;
	boolean sutInitiated;  // part of this test must be initiated by the SUT

   // params to re-create LogRepository
   String location = null;
   TestSession testSession = null;
   LogIdIOFormat format = null;
   LogIdType idType = null;

   public TestInstance() {}

   public TestInstance(String id) {
      this.id = id;
   }

   public TestInstance(String id, TestSession testSession) {
      this.id = id;
      this.testSession = testSession;
   }

   public TestInstance(String id, String section) {
      this.id = id;
      this.section = section;
   }

   public String describe() {
      StringBuilder buf = new StringBuilder();
      buf.append("TestInstance...\n")
         .append("...id = ").append(id).append("\n")
         .append("...event = ").append(event).append("\n")
         .append("...eventDir = ").append(eventDir).append("\n")
         .append("...location = ").append(location).append("\n")
         .append("...user = ").append(testSession).append("\n")
         .append("...type = ").append(idType).append("\n").
              append("...sutInitiated = ").append(sutInitiated).append("\n");
      return buf.toString();
   }

    public TestInstance copy() {
        TestInstance x = new TestInstance();
        x.id = id;
		x.section = section;
        x.event = event;
        x.eventDir = eventDir;
        x.location = location;
        x.testSession = testSession;
        x.format = format;
        x.idType = idType;
		x.sutInitiated = sutInitiated;
        return x;
    }

   public boolean linkedToLogRepository() {
      return location != null;
   }

   public boolean isTestCollection() {
      return id.startsWith("tc:");
   }

   public void setEvent(String event) {
      this.event = event;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   /**
    * Get the test user, also known as testSession in the UI
    *
    * @return testSession as a string
    */
   public TestSession getTestSession() {
      return testSession;
   }

   /**
    * Set the test user, also known as the testSession in the UI
    *
    * @param testSession testSession
    */
   public void setTestSession(TestSession testSession) {
      this.testSession = testSession;
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

   public boolean isEmpty() {
      return id == null || id.equals("");
   }

   public String getSection() {
      return section;
   }

   public void setSection(String section) {
      this.section = section;
   }

   /**
    * Set the test identifier (11901 for example)
    *
    * @param id the identifier
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * Get the test identifier (11901 for example).
    *
    * @return identifier as a string
    */
   public String getId() {
      return id;
   }

   /*
    *  these should only be used by TestIdBuilder which extends this class with
    *  stuff that cannot be part of GWT
    */
   public String getInternalEvent() {
      return event;
   }

   public void setInternalEvent(String e) {
      event = e;
   }

   public String getEventDir() {
      return eventDir;
   }

   public void setEventDir(String eventDir) {
      this.eventDir = eventDir;
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
      return ((id == null) ? 42 : id.hashCode()) + ((event == null) ? 43 : event.hashCode());
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (event != null) buf.append(event);
      buf.append(':');
      buf.append(id);
      buf.append(':');
      if (section != null) buf.append(section);
      buf.append("  in ").append(testSession);
      return buf.toString();
   }

	public boolean getSutInitiated() {
		return sutInitiated;
	}

	public void setSutInitiated(boolean sutInitiated) {
		this.sutInitiated = sutInitiated;
	}
}
