package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ActorType;

import java.io.Serializable;

/**
 * Identify a test instance, the combination of a test definition and the state
 * of the running test.
 */
public class TestInstance implements IsSerializable, Serializable {
	private String id = null;    //  id of the test
   private String section = null;  // this is optional
   private String event = null;
   private String eventDir;
   private boolean sutInitiated;  // part of this test must be initiated by the SUT
   private boolean runStatus;
   private ActorType actorType = null;
   private String option = null;

   // params to re-create LogRepository
   String location = null;
   String user = null;
   LogIdIOFormat format = null;
   LogIdType idType = null;

   public TestInstance() {}

   public TestInstance(String id) {
      this.id = id;
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
         .append("...user = ").append(user).append("\n")
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
        x.user = user;
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
   public String getUser() {
      return user;
   }

   /**
    * Set the test user, also known as the testSession in the UI
    *
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

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (event != null) buf.append(event);
      buf.append(':');
      buf.append(id);
      buf.append(':');
      if (section != null) buf.append(section);
      return buf.toString();
   }

	public boolean getSutInitiated() {
		return sutInitiated;
	}

	public void setSutInitiated(boolean sutInitiated) {
		this.sutInitiated = sutInitiated;
	}

   public boolean isRunStatus() {
      return runStatus;
   }

   public void setRunStatus(boolean runStatus) {
      this.runStatus = runStatus;
   }

   public ActorType getActorType() {
      return actorType;
   }

   public void setActorType(ActorType actorType) {
      this.actorType = actorType;
   }

   public String getOption() {
      return option;
   }

   public void setOption(String option) {
      this.option = option;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TestInstance that = (TestInstance) o;

      if (sutInitiated != that.sutInitiated) return false;
      if (runStatus != that.runStatus) return false;
      if (id != null ? !id.equals(that.id) : that.id != null) return false;
      if (section != null ? !section.equals(that.section) : that.section != null) return false;
      if (event != null ? !event.equals(that.event) : that.event != null) return false;
      if (eventDir != null ? !eventDir.equals(that.eventDir) : that.eventDir != null) return false;
      if (actorType != that.actorType) return false;
      if (option != null ? !option.equals(that.option) : that.option != null) return false;
      if (location != null ? !location.equals(that.location) : that.location != null) return false;
      if (user != null ? !user.equals(that.user) : that.user != null) return false;
      if (format != that.format) return false;
      return idType == that.idType;
   }

   @Override
   public int hashCode() {
      int result = id != null ? id.hashCode() : 0;
      result = 31 * result + (section != null ? section.hashCode() : 0);
      result = 31 * result + (event != null ? event.hashCode() : 0);
      result = 31 * result + (eventDir != null ? eventDir.hashCode() : 0);
      result = 31 * result + (sutInitiated ? 1 : 0);
      result = 31 * result + (runStatus ? 1 : 0);
      result = 31 * result + (actorType != null ? actorType.hashCode() : 0);
      result = 31 * result + (option != null ? option.hashCode() : 0);
      result = 31 * result + (location != null ? location.hashCode() : 0);
      result = 31 * result + (user != null ? user.hashCode() : 0);
      result = 31 * result + (format != null ? format.hashCode() : 0);
      result = 31 * result + (idType != null ? idType.hashCode() : 0);
      return result;
   }
}
