package gov.nist.toolkit.server.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RegistryStatus implements Serializable, IsSerializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String fatalError = "";
	public boolean status = false;
	public String endpoint = "";
	public String name = "";
	public List<String> errors = new ArrayList<String>();
	
	public RegistryStatus() {}
	
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
		
		buf.append("[RegistryStatus ");
		buf.append("name=").append(name);
		buf.append(" status=").append(status);
        buf.append(" errors=").append(errors);
        if (fatalError != null && !fatalError.equals("")) buf.append(" fatalError=").append(fatalError);
		buf.append("]");
		
		return buf.toString();
	}


}
