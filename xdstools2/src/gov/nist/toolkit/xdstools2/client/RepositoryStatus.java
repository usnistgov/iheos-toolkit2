package gov.nist.toolkit.xdstools2.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RepositoryStatus implements Serializable, IsSerializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String docId = null;
	public String fatalError = "";
	public boolean status = false;
	public String endpoint = "";
	public String name = "";
	public List<String> errors = new ArrayList<String>();
	public String registry = null;
	public String date = "";
	
	public RepositoryStatus() {} 
	
	public String getErrorsAsString() {
		if (errors == null || errors.size() == 0)
			return "";
		try {
			return errors.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("[");
		buf.append("name=").append(name);
		buf.append(" status=").append(status);
		buf.append("]");
		
		return buf.toString();
	}

}
