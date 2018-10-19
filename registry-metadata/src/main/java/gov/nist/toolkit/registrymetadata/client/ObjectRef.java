package gov.nist.toolkit.registrymetadata.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;


public class ObjectRef extends MetadataObject implements IsSerializable, Serializable  {
	private static final long serialVersionUID = 1L;
//	public String id;

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

	@Override
	public boolean equals(Object o) {
	    if (this==o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ObjectRef objectRef = (ObjectRef)o;
		if (id!=null && objectRef.id!=null)
			return (id.equals(objectRef.id));
		return false;
	}

	@Override
	public int hashCode() {
	    if (id!=null)
			return id.hashCode();
	    else
	    	return 0;
	}
}
