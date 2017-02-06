package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

public class Author implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;
	public String person;
	public String personDoc;
	
	public List<String> institutions;
	public List<String> institutionsDoc;
	
	public List<String> roles;
	public List<String> rolesDoc;
	
	public List<String> specialties;
	public List<String> specialtiesDoc;

	public List<String> telecom;
	public List<String> telecomDoc;

}
