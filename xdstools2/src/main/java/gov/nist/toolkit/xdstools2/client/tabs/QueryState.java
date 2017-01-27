package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.Cookies;
import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CookieManager;

public class QueryState {
	String patientId = null;
	SiteSpec siteSpec = new SiteSpec();

	public String getPatientId() {
		if (patientId == null) {
			patientId = Cookies.getCookie(CookieManager.PATIENTIDCOOKIENAME);
		}
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
		Cookies.setCookie(CookieManager.PATIENTIDCOOKIENAME, patientId);
	}
	public SiteSpec getSiteSpec() {
		String cookieSite = Cookies.getCookie(CookieManager.LASTSITECOOKIENAME);
		if (isEmpty(siteSpec.getName()) && !isEmpty(cookieSite))
			siteSpec.setName(cookieSite);
		return siteSpec;
	}
	public void setSiteSpec(SiteSpec siteSpec) {
		if (siteSpec == null) return;
		Cookies.setCookie(CookieManager.LASTSITECOOKIENAME, siteSpec.getName());
		this.siteSpec = siteSpec;
	}

	static boolean isEmpty(String x) { return x == null || x.equals(""); }
}
