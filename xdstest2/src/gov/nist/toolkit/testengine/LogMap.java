package gov.nist.toolkit.testengine;


import gov.nist.toolkit.testenginelogging.LogFileContent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6977145786942237537L;
	List<LogMapItem> items = new ArrayList<LogMapItem>();
	transient Map<String, LogFileContent> logFileContent = new HashMap<String, LogFileContent>();
	
	public LogMap() {
	}
	
	public void add(String testName, LogFileContent log) {
		logFileContent.put(testName, log);
		items.add(new LogMapItem(testName, log));
	}
	
	public List<LogMapItem> getItems() { return items; }
	
	public Map<String, LogFileContent> getLogFileContentMap() { return logFileContent; }

	public class LogMapItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -290403612300468696L;
		public String testName;
		public LogFileContent log;
		
		public LogMapItem(String testName, LogFileContent log) {
			this.testName = testName;
			this.log = log;
		}
		
		public String toString() {
			return "[LogMapItem: testName=" + testName + " log=" + log.toString() + "]";
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("[LogMap: ");
		
		for (LogMapItem i : items) {
			buf.append(i.toString());
		}
		
		buf.append("]");
		
		return buf.toString();
	}
	
}
