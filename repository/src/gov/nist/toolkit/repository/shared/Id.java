package gov.nist.toolkit.repository.shared;

public class Id {
	String id;
	
	public Id() {}
	
	public Id(String idValue) {
		id = idValue;
	}
	
	public String getId() { return id; }
	public void setId(String idValue) { id = idValue; }
	public String toString() { return "Id:" + id; }
}
