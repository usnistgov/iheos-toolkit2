package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.sitemanagement.CombinedSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;

import javax.xml.parsers.FactoryConfigurationError;

import org.junit.Test;



public class SitesTest {
	Sites sites = null;

//	@Test
//	public void emptyConfig() {
//		String conf = "<sites/>";
//		compile(conf);
//
//		assert sites.getSiteNames().isEmpty();
//		try {
//			sites.getDefaultSite();
//		} catch (Exception e) {
//			assert e.getMessage().startsWith("No Default");
//			return;
//		}
//		assert false;
//	}
//
//	@Test
//	public void emptySiteName()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site name=\"\">" +
//		"<transaction name=\"foo\">http://foo</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		try {
////			sites = new Sites(conf);
//			sites = new CombinedSiteLoader().load(conf, null);
//		} catch (FactoryConfigurationError e) {
//			assert false;
//		} catch (Exception e) {
//			assert e.getMessage().startsWith("Cannot parse Site with empty");
//			return;
//		}
//		assert false;
//	}
//
//	@Test
//	public void missingSiteName()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site>" +
//		"<transaction name=\"foo\">http://foo</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		try {
////			sites = new Sites(conf);
//			sites = new CombinedSiteLoader().load(conf, null);
//		} catch (FactoryConfigurationError e) {
//			assert false;
//		} catch (Exception e) {
//			assert e.getMessage().startsWith("Cannot parse Site with empty");
//			return;
//		}
//		assert false;
//	}
//
//	@Test
//	public void singleConfig()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site name=\"pub\">" +
//		"<transaction name=\"foo\">http://foo</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		compile(conf);
//		
//		System.out.println(sites);
//
//		assert sites.getSiteNames().size() == 1;
//			Site site = null;
//			try {
//				site = sites.getDefaultSite();
//			} catch (Exception e1) {
//				assert false;
//			}
//			assert site != null;
//			try {
//				assert site.getEndpoint("foo", false, false) != null;
//			} catch (Exception e) {
//				assert false;
//			}
//			try {
//				assert site.getEndpoint("foo", false, false).equals("http://foo");
//			} catch (Exception e) {
//				assert false;
//			}
//			try {
//				assert site.getEndpoint("foo", true /*isSecure*/, false) == null;
//				assert false;
//			} catch (Exception e) {
//				// should throw exception
//			}
//	}
//
//	@Test
//	public void singleSecureConfig()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site name=\"pub\">" +
//		"<transaction name=\"foo\" secure=\"1\">http://foo</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		compile(conf);
//		
//		System.out.println(sites);
//
//		assert sites.getSiteNames().size() == 1;
//		try {
//			Site site = sites.getDefaultSite();
//			assert site != null;
//			assert site.getRawEndpoint("foo", true /*isSecure*/, false) != null;
//			assert site.getRawEndpoint("foo", true /*isSecure*/, false).equals("http://foo");
//			assert site.getRawEndpoint("foo", false, false) == null;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//
//	@Test
//	public void multiTransactionConfig()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site name=\"pub\">" +
//		"<transaction name=\"foo\" >http://foo</transaction>" + 
//		"<transaction name=\"bar\" >http://bar</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		compile(conf);
//		
//		System.out.println(sites);
//
//		assert sites.getSiteNames().size() == 1;
//		try {
//			Site site = sites.getDefaultSite();
//			assert site != null;
//			assert site.getEndpoint("foo", false, false) != null;
//			assert site.getEndpoint("foo", false, false).equals("http://foo");
//			assert site.getEndpoint("bar", false, false) != null;
//			assert site.getEndpoint("bar", false, false).equals("http://bar");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//
//	@Test
//	public void multiSiteConfig()  {
//		String conf = "<sites>" +
//		"<defaultsite>pub</defaultsite>" + 
//		"<site name=\"pub\">" +
//		"<transaction name=\"foo\" >http://foo</transaction>" + 
//		"</site>" + 
//		"<site name=\"dev\">" +
//		"<transaction name=\"foo\" >http://baz</transaction>" + 
//		"</site>" + 
//		"</sites>";
//		compile(conf);
//		
//		System.out.println(sites);
//
//		assert sites.getSiteNames().size() == 2;
//		try {
//			Site site = sites.getDefaultSite();
//			assert site != null;
//			assert site.getRawEndpoint("foo", false, false) != null;
//			assert site.getRawEndpoint("foo", false, false).equals("http://foo");
//			assert site.getRawEndpoint("bar", false, false) == null;
//			
//			site = sites.getSite("dev");
//			assert site != null;
//			assert site.getRawEndpoint("foo", false, false) != null;
//			assert site.getRawEndpoint("foo", false, false).equals("http://baz");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//
//	private void compile(String conf) {
//		try {
////			sites = new Sites(conf);
//			sites = new CombinedSiteLoader().load(conf, null);
//		} catch (FactoryConfigurationError e) {
//			assert false;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			assert false;
//		}
//	}
//
}
