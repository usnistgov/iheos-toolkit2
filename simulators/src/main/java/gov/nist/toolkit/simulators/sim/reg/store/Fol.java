package gov.nist.toolkit.simulators.sim.reg.store;


import java.io.Serializable;

public class Fol extends Ro implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String pid;
	public int version;
	public String lid;
	public String lastUpdateTime;
	public String[] codeList;

	public String getType() {
		return "Folder";
	}
	
	public Fol clone() {
		Fol f = new Fol();
		
		f.pid = pid;
		f.lastUpdateTime = lastUpdateTime;
		f.version = version;
		f.lid = lid;
		
		int codeListSize = codeList.length;
		f.codeList = new String[codeListSize];
		for (int i=0; i<codeListSize; i++)
			f.codeList[i] = codeList[i];
		
		f.id = id;
		f.uid = uid;
		f.pathToMetadata = pathToMetadata;
		f.setAvailabilityStatus(getAvailabilityStatus());
		
		return f;
	}
	
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	public void setLastUpdateTime(String time) {
		lastUpdateTime = time;
	}
	
}
