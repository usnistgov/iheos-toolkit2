package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.http.httpclient.HttpClient;
import gov.nist.toolkit.utilities.io.Io;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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
				"http://ihe.wustl.edu/gazelle-na/systemConfigurations.seam?testingSessionId=39&configurationType=WebServiceConfiguration",
				new File("/Users/bmajur/tmp/toolkit2/actors"));
		try {
			pa.pull();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
