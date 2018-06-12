package gov.nist.toolkit.testenginelogging.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.*;

public class LogMapDTO implements Serializable, IsSerializable {
	/**
	 * Each LogFileContentDTO represents the log for one section of the test
	 */
	private static final long serialVersionUID = 6977145786942237537L;
	private List<LogMapItemDTO> items = new ArrayList<LogMapItemDTO>();
	private transient Map<String, LogFileContentDTO> logFileContent = new HashMap<String, LogFileContentDTO>();
	private boolean tls = false;
	
	public LogMapDTO() {
	}

	public boolean isTls() {
		return tls;
	}
	
	public void add(String sectionName, LogFileContentDTO log) {
		logFileContent.put(sectionName, log);
		items.add(new LogMapItemDTO(sectionName, log));
		if (log.isTls())
			tls = true;
	}
	
	public List<LogMapItemDTO> getItems() { return items; }
	
	public Map<String, LogFileContentDTO> getLogFileContentMap() { return logFileContent; }
	
	public List<String> getKeys() {
		List<String> keys = new ArrayList<String>();
		
		for (LogMapItemDTO item : items) {
			keys.add(item.getTestName());
		}
		
		return keys;
	}

}
