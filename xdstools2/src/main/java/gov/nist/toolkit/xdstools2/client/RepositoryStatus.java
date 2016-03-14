package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public void setFatalError(String e) {
        fatalError = e;
    }
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("[RepositoryStatus ");
		buf.append("name=").append(name);
		buf.append(" status=").append(status);
        if (fatalError != null && !fatalError.equals("")) buf.append(" fatalError=").append(fatalError);
        buf.append(" errors=").append(errors);
		buf.append("]");
		
		return buf.toString();
	}

}
