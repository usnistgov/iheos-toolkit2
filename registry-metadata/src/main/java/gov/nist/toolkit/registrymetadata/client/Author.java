package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
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

	@Override
	public String toString() {
		return person;
	}

	public static Author clone(Author src) {
		Author dest = new Author();

		dest.person = src.person;
		dest.personDoc = src.personDoc;

		if (src.institutions != null) {
			dest.institutions = new ArrayList<>(src.institutions);
			if (src.institutionsDoc!=null) {dest.institutionsDoc = new ArrayList<>(src.institutionsDoc);}
		}

		if (src.roles != null) {
			dest.roles = new ArrayList<>(src.roles);
			if (src.rolesDoc!=null) {dest.rolesDoc = new ArrayList<>(src.rolesDoc);}
		}

		if (src.specialties != null) {
			dest.specialties = new ArrayList<>(src.specialties);
			if (src.specialtiesDoc!=null) {dest.specialtiesDoc = new ArrayList<>(src.specialtiesDoc);}
		}

		if (src.telecom != null) {
			dest.telecom = new ArrayList<>(src.telecom);
			if (src.telecomDoc!=null) {dest.telecomDoc = new ArrayList<>(src.telecomDoc);}
		}

		return dest;
	}
}
