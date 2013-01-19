package gov.nist.toolkit.tk;

import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class TkPropsServer {
	static Logger logger = Logger.getLogger(TkPropsServer.class);

	public List<Ps> p;
	public String prefix = "";

	public TkPropsServer(List<Ps> p) {
		this.p = p;
		this.prefix = "";
	}

	public TkPropsServer(List<Ps> p, String prefx) {
		this.p = p;
		this.prefix = prefx + ".";
	}

	public TkPropsServer() {
		this.p = new ArrayList<Ps>();
		this.prefix = "";
	}

	public TkProps toTkProps() {
		TkProps tp = new TkProps();

		for (Ps a : p) {
			tp.add(a.name, a.value);
		} 

		return tp;
	}

	public TkPropsServer(TkProps t) {
		p = new ArrayList<Ps>();
		prefix = "";

		for (gov.nist.toolkit.tk.client.Prop tp : t.p) {
			Ps pr = new Ps(tp.name, tp.value);
			p.add(pr);
		}
	}

	public void parse(String line) {
		if (line == null) return;
		if (line.trim().equals("")) return;
		if (line.trim().startsWith("#")) return;
		if (parse(line, ":"))
			return;
		parse(line, "=");
	}

	boolean parse(String line, String seperator) {
		String[] l = line.split(seperator);
		if (l.length >= 2) {
			String v = l[1];
			for (int i=2; i<l.length; i++)
				v = v + ":" +l[i];
			Ps x = new Ps(l[0].trim(), v.trim());
			p.add(x);
			return true;
		} 
		return false;
	}

	public void set(String name, String value) {
		Ps s = getEntry(name);
		if (s == null) {
			Ps x = new Ps(name, value);
			p.add(x);
		} else {
			s.value = value;
		}
	}

	TkPropsServer startsWith(String prefix) {
		List<Ps> ps = new ArrayList<Ps>();

		for (Ps x : p) {
			if (x.name.startsWith(prefix))
				ps.add(x);
		}
		return new TkPropsServer(ps);
	}

	public List<String> values() {
		List<String> l = new ArrayList<String>();

		for (Ps x : p) {
			l.add(x.value);
		}

		return l;
	}

	public TkPropsServer withPrefix(String prefix) {
		return startsWith(prefix);
	}

	public TkPropsServer rmPrefix(String prefix) {
		List<Ps> ps = new ArrayList<Ps>();

		logger.debug("TkPropsServer#rmPrefix(" + prefix + ")");
		logger.debug(toString());
		logger.debug("\n");

		for (Ps x : p) {
			logger.debug("name is " + x.name);
			if (x.name.startsWith(prefix)) {
				String n = x.name.substring(prefix.length() + 1);
				logger.debug("n is " + n);
				Ps nx = new Ps(n, x.value);
				ps.add(nx);
			}
		}
		return new TkPropsServer(ps, prefix);
	}

	public TkPropsServer withPrefixRemoved(String prefix) {
		return withPrefix(prefix).rmPrefix(prefix);
	}

	Ps getEntry(String name) {
		for (Ps x : p) {
			if (x.name.equals(name))
				return x;
		}
		return null;
	}

	public String get(String name) throws PropertyNotFoundException {
		for (Ps x : p) {
			if (x.name.equals(name))
				return x.value;
		}
		throw new PropertyNotFoundException("Property not found - name = " + prefix + name);
	}

	public String get(String name, String defaultValue)  {
		for (Ps x : p) {
			if (x.name.equals(name))
				return x.value;
		}
		return defaultValue;
	}

	String asString(List<String> x) {
		StringBuffer buf = new StringBuffer();

		for (String s : x) {
			buf.append(s).append("\n");
		}

		return buf.toString();
	}

	public String linesAsString(String name, Map<String, String> varSubstitutions) {
		StringBuffer buf = new StringBuffer();

		TkPropsServer p = startsWith(name);
		List<String> values = p.values();

		System.out.println("Substutions are " + varSubstitutions);

		if (varSubstitutions == null)
			return asString(values);

		for (String st : values) {
			for (String n : varSubstitutions.keySet()) {
				String v = varSubstitutions.get(n);

				try {
					System.out.println("Replacing: TEMPLATE=" + st + " PATTERN=" + n + " VALUE=" + v);
					st = st.replaceAll(n, v);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					break;
				}
			}
			System.out.println("Result is " + st);
			buf.append(st).append("\n");
		}
		return buf.toString();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (Ps x : p) {
			buf.append(x.name).append(": ").append(x.value).append("\n");
		}

		return buf.toString();
	}
}
