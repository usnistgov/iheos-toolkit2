package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import java.util.ArrayList;
import java.util.List;

public class CSVEntry {
	List<String> items = new ArrayList<String>();
	String line;
	
	public List<String> getItems() {
		return items;
	}
		
	public CSVEntry(String line) {
		this.line = line;
	}
	
	public void build() {
		int stringStart = -1;

		for (int cursor=0; cursor < line.length(); cursor++) {
			char c = line.charAt(cursor);

			if (isWhite(c)) {
				
			} else if (c == ',') {
				if (stringStart == -1) {
					add("");
				} else {
					add(line.substring(stringStart, cursor).trim());
					stringStart = -1;
				}
			} else if (c == '"') {
				if (stringStart == -1)
					stringStart = cursor + 1;
				else {
					String contents = line.substring(stringStart, cursor).trim(); 
					add(contents);
					stringStart = -1;
				}
			} else {
				if (stringStart == -1)
					stringStart = cursor;
			}
		}
		
		if (stringStart != -1) {
			add(line.substring(stringStart, line.length()).trim());
		}
	}
	
	public String rmParenthetical(String in) {
		int openI = in.indexOf('(');
		if (openI == -1) return in;
		int closeI = in.lastIndexOf(')');
		if (closeI == -1) return in;
		return in.substring(0, openI) + in.substring(closeI+1);
	}
	
	boolean isWhite(char c) { return c == ' ' || c == '\t'; }
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (String item : items)
			buf.append(item).append(", ");
		
		return buf.toString();
	}
	
	public String get(int i) {
		if (i >= items.size())
			return "";
		return items.get(i);
	}

	void add(String item) {
		items.add(rmParenthetical(item).trim());
	}
	
}
