package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

public class ConfigPull {
	static Logger logger = Logger.getLogger(ConfigPull.class);
	String url;
	File actorsDir;
	
	/**
	 * Pull all actor configs from Gazelle
	 * @param url - must include testingSession att. Example is 
	 * http://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=15
	 * @param actorsDir - Directory where toolkit stores actor config files
	 */
	public ConfigPull(String url, File actorsDir) {
		this.url = url;
		this.actorsDir = actorsDir;
		actorsDir.mkdirs();
	}
	
	/**
	 * Pull all configurations
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void pull() throws MalformedURLException, IOException, Exception {
		String u = url + 
		"&configurationType=WebServiceConfiguration";
		
		HttpClient hc = new HttpClient();
		byte[] data = hc.httpGetBytes(u);
		
		Io.bytesToFile(new File(actorsDir + File.separator + "all.csv"), data);
	}
	
	/**
	 * Pull a single system configuraton from Gazelle
	 * @param systemName
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void pull(String systemName) throws MalformedURLException, IOException, Exception {
		logger.debug("Pull from Gazelle(" + systemName + ")");
		
		String systemNameTr = systemName.replaceAll(" ", "%20");
		
		String u = url + 
		"&configurationType=WebServiceConfiguration&systemKeyword=" + systemNameTr;
		
		HttpClient hc = new HttpClient();
		byte[] data = hc.httpGetBytes(u);
		
		Io.bytesToFile(new File(actorsDir + File.separator + systemName + ".csv"), data);
	}
	
	public static void main(String[] args) {
		ConfigPull pa = new ConfigPull(
				"http://gazelle-orange.wustl.edu/NA2012/systemConfigurations.seam?testingSessionId=21&configurationType=WebServiceConfiguration", 
				new File("/Users/bill/tmp/toolkit/actors"));
		try {
			pa.pull();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] argv) {
//		try {
//			OMElement ele = Util.parse_xml(new File("/Users/bill/tmp/alberto/regresp.xml"));
//			System.out.println(new OMFormatter(ele).toString());
//		} catch (XdsInternalException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FactoryConfigurationError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
