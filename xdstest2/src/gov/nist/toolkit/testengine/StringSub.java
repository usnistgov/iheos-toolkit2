package gov.nist.toolkit.testengine;

import java.util.ArrayList;

public class StringSub {
	ArrayList<String> from;
	ArrayList<String> to;
	StringBuffer buf;

	public StringSub(String content) {
		setString(content);
		from = new ArrayList<String>();
		to = new ArrayList<String>();
	}

	public StringSub() {
		from = new ArrayList<String>();
		to = new ArrayList<String>();
	}

	public void setString(String content) {
		buf = new StringBuffer(content);
	}

	public void addSub(String from, String to) {
		this.from.add(from);
		this.to.add(to);
	}

	private void doSub() {
		for (int i=0; i<from.size(); i++) {
			String from = this.from.get(i);
			String to = this.to.get(i);

			int cnt=10000;
			while(true) {

				int idx = buf.indexOf(from);
				
				if (idx == -1) 
					break;
				
				int len = from.length();

				buf.replace(idx, idx+len, to);
				
				cnt--;
				if (cnt<=0) 
					break;   // safety valve
			}

		}
	}

	public String toString() {
		doSub();
		return buf.toString();
	}
}
