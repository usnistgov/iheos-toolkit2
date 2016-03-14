package gov.nist.toolkit.saml.bean;

import java.io.Serializable;



public class SamlDataContext implements Serializable {
	
	private String orgId ;
	private String hcId ;
	private String npId ;
	private String role ;
	private String ipAddres ;
	private String userName ;
	private String portNumber ;
	private String endPoint ;
	private String jksFileName;
	private String xmlFileName;

	
	public String getJksFileName() {
		return jksFileName;
	}
	public void setJksFileName(String jksFileName) {
		this.jksFileName = jksFileName;
	}
	public String getXmlFileName() {
		return xmlFileName;
	}
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}
	
    public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getHcId() {
		return hcId;
	}
	public void setHcId(String hcId) {
		this.hcId = hcId;
	}
	public String getNpId() {
		return npId;
	}
	public void setNpId(String npId) {
		this.npId = npId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getIpAddres() {
		return ipAddres;
	}
	public void setIpAddres(String ipAddres) {
		this.ipAddres = ipAddres;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	

}
