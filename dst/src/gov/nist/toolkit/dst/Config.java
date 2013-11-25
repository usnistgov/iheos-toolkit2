package gov.nist.toolkit.dst;

import gov.nist.toolkit.dst.cmd.selectables.SiteLoader;
import gov.nist.toolkit.sitemanagement.client.Site;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	public Dst dst;
	public SiteLoader siteLoader;
	static Config it = null;
	List<String> currentSelect = null;
	Map<String, Object> props = new HashMap<String, Object>();

	public Config(Dst dst) throws Exception {
		if (it != null)
			return;
		this.dst = dst;
		siteLoader = new SiteLoader(new File("/Users/bmajur/tmp/toolkit/actors.xml"));
		it = this;
	}
	
	static public Config get() { return it; }
	public void put(String name, Object value) { props.put(name, value); }
	public Object get(String name) { return props.get(name); }
	
	public String forDisplay() {
		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		for (String name : props.keySet()) {
			if (!first) buf.append("\n");
			first = false;
			buf.append(name).append(": ").append(get(name));
		}
		
		return buf.toString();
	}
	
	public void setCurrentList(List<String> lst) {
		currentSelect = lst;
		listCurrent();
	}
	
	public List<String> getCurrentList() { return currentSelect; }
	
	/**
	 * Display in the 'list' area the list in currentSelect.
	 */
	public void listCurrent() {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (String item : currentSelect) {
			buf.append(AlphaIndex.charOf(i)).append("   ").append(item).append("\n");
			i++;
		}
		dst.displaySelection(buf.toString());
	}
	
	public Site getCurrentSite() throws Exception {
		String siteName = (String) get("Site");
		return siteLoader.getSite(siteName);
	}
}
