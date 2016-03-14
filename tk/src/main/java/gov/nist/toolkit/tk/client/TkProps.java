package gov.nist.toolkit.tk.client;

import gov.nist.toolkit.tk.Ps;
import gov.nist.toolkit.tk.TkPropsServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TkProps implements IsSerializable {

	public List<Prop> p;

	public TkProps(List<Prop> p) {
		this.p = p;
	}
	
	public TkProps() {
		this.p = new ArrayList<Prop>();
	}
	
	public boolean isEmpty() {
		return p.size() == 0;
	}
	
	public void add(String name, String value) {
		p.add(new Prop(name, value));
	}
	
	public void parse(String line) {
		if (line == null) return;
		if (line.trim().equals("")) return;
		if (line.trim().startsWith("#")) return;
		String[] l = line.split(":");
		if (l.length == 2) {
			Prop x = new Prop(l[0].trim(), l[1].trim());
			p.add(x);
		}
	}
	

	TkProps startsWith(String prefix) {
		List<Prop> ps = new ArrayList<Prop>();
		
		for (Prop x : p) {
			if (x.name.startsWith(prefix))
				ps.add(x);
		}
		return new TkProps(ps);
	}
	
	public TkProps withPrefix(String prefix) {
		return startsWith(prefix);
	}
	
	public TkProps withPrefixRemoved(String prefix) {
		return withPrefix(prefix).rmPrefix(prefix);
	}
	
	public TkProps rmPrefix(String prefix) {
		List<Prop> ps = new ArrayList<Prop>();

		for (Prop x : p) {
			if (x.name.startsWith(prefix)) {
				String n = x.name.substring(prefix.length() + 1);
				Prop nx = new Prop(n, x.value);
				ps.add(nx);
			}
		}
		return new TkProps(ps);
	}
	
//	public String get(String name) {
//		for (Prop x : p) {
//			if (x.name.equals(name))
//				return x.value.trim();
//		}
//		return null;
//	}
	
	public String get(String name) throws PropertyNotFoundException {
		for (Prop x : p) {
			if (x.name.equals(name))
				return x.value.trim();
		}
		throw new PropertyNotFoundException("Property not found - name = " + name);
	}

	public String get(String name, String defaultValue)  {
		for (Prop x : p) {
			if (x.name.equals(name))
				return x.value.trim();
		}
		return defaultValue;
	}


	public String linesAsString(String name, Map<String, String> varSubstitutions) {
		String buf;
		
		TkProps p = startsWith(name);
		buf = p.toString();
		
		if (varSubstitutions == null)
			return buf;
		
		for (String n : varSubstitutions.keySet()) {
			String v = varSubstitutions.get(n);
			buf = buf.replaceAll(n, v);
		}
		return buf;
	}
	
	public String toString() {
		String buf = "";
		
		for (Prop x : p) {
			buf = buf + x.name + "= " + x.value + "\n";
		}
		
		return buf;
	}
}
