package gov.nist.toolkit.registrymetadata.client;

import java.util.ArrayList;
import java.util.List;

public class MetadataDiffBase {

	protected static List<String> dup(List<String> x) {
		List<String> y = new ArrayList<String>();
		for (String r : x) {
			y.add(new String(r));
		}
		return y;
	}

	protected static List<Author> dupa(List<Author> as) {
		List<Author> al = new ArrayList<Author>();
		for (Author a : as) {
			al.add(dup(a));
		}
		return al;
	}

	static Author dup(Author a) {
		Author b = new Author();
		b.person = a.person;
		b.institutions = dup(a.institutions);
		b.roles = dup(a.roles);
		b.specialties = dup(a.specialties);
		b.telecom = dup(a.telecom);
		return b;
	}

	static boolean dif(Author a, Author b) {
		if (dif(a.person, b.person)) return true;
		if (dif(a.institutions, b.institutions)) return true;
		if (dif(a.roles, b.roles)) return true;
		if (dif(a.specialties, b.specialties)) return true;
		if (dif(a.telecom, b.telecom)) return true;
		return false;
	}

	protected static boolean difa(List<Author> x, List<Author> y) {
		if (x.size() == 0 && y.size() == 0) return false;
		if (x.size() == 1 && y.size() == 1) return dif(x.get(0), y.get(0));
		if (x.size() != y.size()) return true;
		
		for (int i=0; i<x.size(); i++) // This method is order dependent
			if (dif(x.get(i), y.get(i))) return true;
		return false;
	}

	protected static boolean dif(List<String> x, List<String> y) {
		if (x.size() == 0 && y.size() == 0) return false;
		if (x.size() == 1 && y.size() == 1) return dif(x.get(0), y.get(0));
		if (x.size() != y.size()) return true;
		
		for (int i=0; i<x.size(); i++)
			if (dif(x.get(i), y.get(i))) return true;
		return false;
	}

	protected static boolean dif(String x, String y) {
		if (x == null && y == null) return false;
		if (x == null && y != null) return true;
		if (x.equals(y)) return false;
		return true;
	}

	public MetadataDiffBase() {
		super();
	}

}