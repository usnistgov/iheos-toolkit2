package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.results.client.SiteSpec;

public class QueryState {
	String patientId;
	SiteSpec siteSpec = new SiteSpec();

	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public SiteSpec getSiteSpec() {
		return siteSpec;
	}
	public void setSiteSpec(SiteSpec siteSpec) {
		this.siteSpec = siteSpec;
	}
}
