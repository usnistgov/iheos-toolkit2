package edu.wustl.mir.erl.ihe.xdsi.util;

public class KOSInstanceBean {
	
	private String instanceUID = null;
	private String classUID = null;
	public KOSInstanceBean(String instanceUID, String classUID) {
		super();
		this.instanceUID = instanceUID;
		this.classUID = classUID;
	}
	public String getInstanceUID() {
		return instanceUID;
	}
	public void setInstanceUID(String instanceUID) {
		this.instanceUID = instanceUID;
	}
	public String getClassUID() {
		return classUID;
	}
	public void setClassUID(String classUID) {
		this.classUID = classUID;
	}

}
