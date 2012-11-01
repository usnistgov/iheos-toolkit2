package gov.nist.toolkit.registrymetadata.client;


import com.google.gwt.user.client.rpc.IsSerializable;


public class AnyId implements IsSerializable {
	public String id;
	public String home;
	public String repositoryUniqueId;
	public boolean isLid = false;
	
	public boolean isUUID() {
		return id != null && id.startsWith("urn:uuid:") && !isLid;
	}
	
	public boolean isLid() {
		return id != null && isLid;
	}
	
	public String toString() {
		return "[AnyId: id=" + id + 
		((home == null) ? "" : " home=" + home ) + 
		((repositoryUniqueId == null) ? "" :" repositoryUniqueId=" + repositoryUniqueId) + 
		" " +
		typeName() +
		"]";
	}
	
	public String typeName() {
		if (isLid())
			return "isLid";
		else if (isUUID())
			return "isUUID";
		else
			return "isUid";

	}
	
	public AnyId() {
		id = null;
	}
	
	public AnyId(String id) {
		this.id = id;
	}
	
	public AnyId(ObjectRef or) {
		id = or.id;
		home = or.home;
	}
	
	public AnyId(Uid uid) {
		id = uid.uid;
		home = uid.home;
		repositoryUniqueId = uid.repositoryUniqueId;
	}
}
