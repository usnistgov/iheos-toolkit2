package gov.nist.toolkit.registrymetadata.client;


import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ObjectRef extends MetadataObject implements IsSerializable, Serializable  {
	private static final long serialVersionUID = 1L;
	public String id;
	public String home;
	
	public String displayName() {
		return id;
	}
	
	public ObjectRef(String id) {
		this.id = id;
	}
	
	public ObjectRef(AnyId aid) {
		id = aid.id;
		home = aid.home;
	}
	
	public ObjectRef(String id, String home) {
		this.id = id;
		this.home = home;
	}
	
	public ObjectRef() {
		
	}

	public String getHome() {
		return home;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return "[" + id + " home=" + home + "]";
	}

}
