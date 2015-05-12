package gov.nist.toolkit.valregmsg.registry.storedquery.support;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.valregmsg.registry.And;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SqParams {
	Map<String, Object> params;
	String query_id;
	
	public SqParams(String queryid, Map<String, Object> params) {
		query_id = queryid;
		this.params = params;
	}
	
	public SqParams() {
		params = new HashMap<String, Object>();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext(); ) {
			String name = it.next();
			Object value = params.get(name);
			buf.append("\t");
			buf.append(name);
			buf.append(" => ");
			buf.append(value);
			buf.append("\n");
		}
		
		return buf.toString();
	}
	
	public int size() { return params.size(); }
	public void addParm(String name, Object value) { params.put(name, value); }
	public boolean hasParm(String parmName) { return params.containsKey(parmName); }
	public Object getParm(String parmName) { return params.get(parmName); }
	public String getQueryId() { return query_id; }
	
	public void addStringParm(String name, String value) {
		addParm(name, value);
	}

	public void addIntParm(String name, Integer value) {
		addParm(name, value);
	}

	public void addIntParm(String name, BigInteger value) {
		addParm(name, value);
	}
	
	public void addListParm(String name, List<String> values) {
		addParm(name, values);
	}

	public void addListParm(String name, String onlyValue) {
		List<String> values = new ArrayList<String>();
		values.add(onlyValue);
		addParm(name, values);
	}

	
	public String getStringParm(String name) {
		Object o = params.get(name);
		if ( o instanceof String) {
			return (String) o;
		}
		return null;
	}
	
	public String getIntParm(String name) throws MetadataException {
		Object o = params.get(name);
		if (o == null)
			return null;
		if ( o instanceof Integer) {
			Integer i = (Integer) o;
			return i.toString();
		} 
		if ( o instanceof BigInteger) {
			BigInteger i = (BigInteger) o;
			return i.toString();
		} 
		else
			throw new MetadataException("Parameter " + name + " - expecting a number but got " + o.getClass().getName() + " instead", SqDocRef.Request_parms);
	}

	public List<String> getListParm(String name) throws XdsInternalException, MetadataException {
		Object o = params.get(name);
		if (o == null)
			return null;
		if (o instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> a = (List<String>) o;
			if (a.size() == 0)
				throw new MetadataException("Parameter " + name + " is an empty list", SqDocRef.Request_parms);
			return a;
		}
		throw new XdsInternalException("get_arraylist_parm(): bad type = " + o.getClass().getName());
	}

	public List<Object> getAndorParm(String name) throws XdsInternalException, MetadataException {
		Object o = params.get(name);
		if (o == null)
			return null;
		if (o instanceof ArrayList) {
			@SuppressWarnings("unchecked")
			List<Object> a = (List<Object>) o;
			if (a.size() == 0)
				throw new MetadataException("Parameter " + name + " is an empty list", SqDocRef.Request_parms);
			return a;
		}
		throw new XdsInternalException("get_arraylist_parm(): bad type = " + o.getClass().getName());
	}
	
	public SQCodedTerm getCodedParm(String name) throws MetadataException, XdsInternalException {
		Object o = params.get(name);
		if (o == null)
			return null;
		if (o instanceof SQCodedTerm) {
			SQCodedTerm term = (SQCodedTerm) o;
			if (term.isEmpty())
				throw new MetadataException("Parameter " + name + " is empty", SqDocRef.Request_parms);
			return term;
		}
			
		throw new XdsInternalException("getCodedParm(): bad type = " + o.getClass().getName());
	}

	public boolean isAnd(Object values) {
		return (values instanceof And);
	}

	public int andSize(Object values) {
		if ( !isAnd(values)) return 0;
		And and = (And) values;
		return and.size();
	}

	// A simple OR query uses a single SQL variable to control
	// If AND logic is used then we need a separate SQL variable for each AND part
	// This method allocates the variable names
	public ArrayList<String> getAndorVarNames(Object andor, String varname) {
		ArrayList<String> names = new ArrayList<String>();
		if ( !isAnd(andor)) {
			names.add(varname);
			return names;
		}
		And and = (And) andor;
		for (int i=0; i<and.size(); i++) {
			names.add(varname + i);
		}
		return names;
	}

}
