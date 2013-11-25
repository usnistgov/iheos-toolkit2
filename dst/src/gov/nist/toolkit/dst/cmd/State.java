package gov.nist.toolkit.dst.cmd;

import java.util.HashMap;
import java.util.Map;

public class State {
	static Map<String, String> state = new HashMap<String, String>();
	
	public String get(String name) { return state.get(name); }
	public String put(String name, String value) { state.put(name, value); return value; }
	public void reset() { state.clear(); }
	
	public String forDisplay() {
		StringBuffer buf = new StringBuffer();
		for (String name : state.keySet()) {
			buf.append(name).append(": ").append(state.get(name)).append("\n");
		}
		return buf.toString();
	}
}
