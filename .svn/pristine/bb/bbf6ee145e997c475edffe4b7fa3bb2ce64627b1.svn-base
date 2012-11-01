package gov.nist.toolkit.testengine.errormgr;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class ErrorManager {
	
	abstract class Event {
		String msg;
		String code;
		String location;
		String url;
		String urltext;
		
		Event(String msg) {
			this.msg = msg;
			this.code = null;
			this.location = null;
			this.url = null;
			this.urltext = null;
		}
	}
	
	class Error extends Event {
		Error(String msg) {
			super(msg);
		}
	}
	
	class Info extends Event {
		Info(String msg) {
			super(msg);
		}
	}
	
	class Warning extends Event {
		Warning(String msg) {
			super(msg);
		}
	}
	

	List<Event> events = new ArrayList<Event>();
	List <AssertionResult> assertions;
	boolean fatal = false;

	public ErrorManager() {
	}
	
	public void setFatal () {
		fatal = true;
	}
	
	public boolean isFatal() {
		return fatal;
	}
	
	public ErrorManager(String err) {
		add(err);
	}
	
	Event add(Event event) {
		events.add(event);
		return event;
	}

	public ErrorManager add(String msg) {
		add(new Error(msg));
		return this;
	}

	public ErrorManager add(Exception e) {
		return add(ExceptionUtil.exception_details(e));
	}
	
	public ErrorManager addURL(String url, String urltext) {
		if (events.size() == 0 )
			return this;
		events.get(events.size()-1).url = url;
		events.get(events.size()-1).urltext = urltext;
		return this;
	}

	public ErrorManager addInfo(String s) {
		add(new Info(s));
		return this;
	}

	public ErrorManager addWarning(String s) {
		add(new Warning(s));
		return this;
	}

	public String getInfoString() {
		StringBuffer buf = new StringBuffer();

		for (Event e : events) {
			if (e instanceof Info)
				buf.append(e.msg).append("\n");
		}
		return buf.toString();
	}

	void addElement(OMElement root, String name, String value) {
		OMElement ele = MetadataSupport.om_factory.createOMElement(name, null);
		ele.setText(value.replace("<", "[").replace(">", "]"));
		root.addChild(ele);
	}

	public void asXml(OMElement root, String errorElementName, String infoElementName) {
		for (Event e : events) {
			if (e instanceof Error) 
				addElement(root, errorElementName, e.msg);
			else if (e instanceof Warning) 
				addElement(root, infoElementName, "Warning: " + e.msg);
			else if (e instanceof Info) 
				addElement(root, infoElementName, e.msg);
		}
	}

	public List<String> getErrors() {
		List<String> es = new ArrayList<String>();
		for (Event e : events) {
			if (e instanceof Error)
				es.add(e.msg);
		}
		return es;
	}

	public boolean hasErrors() {
		for (Event e : events) {
			if (e instanceof Error)
				return true;
		}
		return false;
	}

	public String getErrorString() {
		StringBuffer buf = new StringBuffer();

		for (Event e : events) {
			if (e instanceof Error)
				buf.append(e.msg).append("\n");
		}

		return buf.toString();
	}

	public String toString() {
		return getErrorString();
	}
}
