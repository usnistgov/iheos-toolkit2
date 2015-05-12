package gov.nist.toolkit.results.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class XdstestLogId implements IsSerializable, Serializable {
	String id = null; 
	
	
	public XdstestLogId() {
		
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public XdstestLogId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean equals(XdstestLogId xid) {
		if (id == null)
			return false;
		return id.equals(xid.id);
	}

}
