package gov.nist.toolkit.soap;

import static org.junit.Assert.*;

import gov.nist.toolkit.xdstools2.scripts.DashboardDaemon;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.TestConfig;
import gov.nist.toolkit.testengine.transactions.BasicTransaction;
import gov.nist.toolkit.testengine.transactions.StoredQueryTransaction;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;
import gov.nist.toolkit.xdsexception.XdsFormatException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.sitemanagement.Sites;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicArrowButton;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;




public class SOAPmessageBuilder_FromTransactionObject_Test {

	private final String NO_ENDPOINT="no_endpoint";
	private final String DUMMY_ENDPOINT = "http://localhost:1/";
	private final String AEGIS_ENDPOINT = "https://dilhn000.dil.aegis.net:443/CONNECTNhinServicesWeb/NhinService/NhinPatientDiscovery";
	private final String UBUNTU_ENDPOINT = "https://192.168.188.128:9892/Gateway/PatientDiscovery/1_0/NhinService/NhinPatientDiscovery";
	//https://192.168.203.128:8181/Gateway/PatientDiscovery/1_0/NhinService/NhinPatientDiscovery?wsdl
	

	//skb "http://localhost:8080";
	private final String NO_ACTION="no_action";
	private final String NO_EXPECTED_RETURN_ACTION="no_expected_return_action";
	private final boolean NO_MTOM = false;
	private final boolean NO_ADDRESSING = false;
	private final boolean NO_SOAP12 = false;
	
	private DashboardDaemon dd = null; 
	
	/* skb
	 * Internet code plug 
	 * Due to variances in environment, this code may NOT work on all O/S.
	 * However, it was tested on WIN7.
	 * <>
	 * */
	protected static void setEnv(Map<String, String> newenv)
	{
	  try
	    {
	        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
	        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
	        theEnvironmentField.setAccessible(true);
	        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
	        env.putAll(newenv);
	        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
	        theCaseInsensitiveEnvironmentField.setAccessible(true);
	        Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
	        cienv.putAll(newenv);
	    }
	    catch (NoSuchFieldException e)
	    {
	      try {
	        Class[] classes = Collections.class.getDeclaredClasses();
	        Map<String, String> env = System.getenv();
	        for(Class cl : classes) {
	            if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
	                Field field = cl.getDeclaredField("m");
	                field.setAccessible(true);
	                Object obj = field.get(env);
	                Map<String, String> map = (Map<String, String>) obj;
	                map.clear();
	                map.putAll(newenv);
	            }
	        }
	      } catch (Exception e2) {
	        e2.printStackTrace();
	      }
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    } 
	}

    private static Map<String, String> createMap() {
        Map<String, String> result = new HashMap<String, String>();
        result.put("XDSHTTP10", "TRUE");
        return Collections.unmodifiableMap(result);
    }
	/* </> */

    
//    @Before
    public void setupEnv() {
		try {
			setEnv(createMap());	
		} catch (Exception ex) {			
			System.out.print(ex.toString());
		} finally {
			System.out.println("system----env:"+ System.getenv("XDSHTTP10"));
			//assertNotNull(System.getenv("USERNAME"));
			assertNotNull(System.getenv("XDSHTTP10"));
		}
    }
    
	@Before
	public void setupTest() {


		System.setProperty("javax.net.ssl.keyStore","C:/Projects/xdstoolkit_0613/environment/sunil/keystore/gateway.jks");
		System.setProperty("javax.net.ssl.keyStorePassword","d23rq4m1");
		System.setProperty("javax.net.ssl.trustStore", "C:/Projects/xdstoolkit_0613/environment/sunil/keystore/cacerts.jks");
		System.setProperty("javax.net.ssl.trustStorePassword","changeit");
		

		
		
		String pid = "a1e4655b50754a2";
		String warhom = "/Users/gerardin/Documents/workspace/mergeFeb4/xdstools2/war";
		String outdir = "/Users/gerardin/IHE-Testing/outputDirTTT/";
		String env = "AEGIS_env";
		String externalCache = "/Users/gerardin/IHE-Testing/xdstools2_environment/"; 

		try {
			
			dd = new DashboardDaemon(warhom, outdir, env, externalCache);
			
			//dd.run(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/*
	 * $Antoine -
	 * 
	 * NOTES:
	 * - Configuration is an ordeal to set up. This indicates we should review and test the configuration mechanism.
	 * - Soap is trying to send a message after building it. Separation of concerns principles indicates we should refactor this.
	 */
	@Test
	public void BuildFindPatientEnvelope() throws XdsInternalException, FactoryConfigurationError, IOException, XdsFormatException, LoadKeystoreException, EnvironmentNotSelectedException {
		// path configuration to rampart 
		String testmgmt_dir= "/Users/gerardin/Documents/workspace/toolkit/xdstools2/war/toolkitx/xdstest";
		System.out.println(testmgmt_dir);
		TestConfig testConfig = new TestConfig();
		testConfig.testmgmt_dir = testmgmt_dir;	
		System.out.println("\tAxis2 client Repository: " + testConfig.testmgmt_dir + File.separator + "rampart" + File.separator + "client_repositories");
		
		Soap soap = new Soap();
		
		/*
		 * skb
		<>
		*/
		
		soap.setSecurityParams((SecurityParams)(dd.getSession()));
		System.out.println("**" +dd.getSession().getKeystoreDir());
		System.out.println("**" + dd.getSession().getKeystorePassword());
		
		
		//doesn't seem to be used
		System.out.println("**" + System.getProperty("javax.net.ssl.keyStore"));
		System.out.println("**" + System.getProperty("javax.net.ssl.keyStorePassword"));
		
		/*</>*/
		soap.setUseSaml(true);
		soap.setRepositoryLocation(testConfig.testmgmt_dir + File.separator + "rampart" + File.separator + "client_repositories" );
		
		// path configuration to the environment
		System.setProperty("External_Cache","C:\\Projects\\xdstoolkit_0613\\");
		//skb "/Users/gerardin/IHE-Testing/xdstools2_environment"
		
		System.setProperty("Environment_Name", "EURO2012");
		
		//parse the input
		URL url = getClass().getClassLoader().getResource("findpatient.xml");
		assertNotNull(url);
		
		System.out.println( "pass 2.");
		
		System.out.println( url.getFile() );
		OMElement body = Util.parse_xml(url.openStream());
		
		//build the soap message
		OMElement output = null;
		try {
			output = soap.soapCall(body, AEGIS_ENDPOINT, NO_MTOM , NO_ADDRESSING , NO_SOAP12 , NO_ACTION , NO_EXPECTED_RETURN_ACTION );
		} catch (AxisFault e) {
			e.printStackTrace();
			System.out.println("Axis complains because endpoint is not available");
		}
		
		assertNotNull(soap);
		// $Antoine - Have introduced public method to bypass soap axis exception
		System.out.println("soap.getSoapHeader is :" + soap.getSoapHeader());
		
	}
	

	public void BuildFindPatientEnvelopeViaTransaction() throws XdsInternalException, FactoryConfigurationError, IOException {
		URL url = getClass().getClassLoader().getResource("findpatient.xml");
		assertNotNull(url);
		System.out.println( url.getFile() );
		
		BasicTransaction transaction = new StoredQueryTransaction(null, null, null);
		Soap soap = new Soap();
		soap.setUseSaml(true);
	
		
		OMElement body = Util.parse_xml(url.openStream());
		
		try {
			soap.soapCall(body, NO_ENDPOINT, NO_MTOM , NO_ADDRESSING , NO_SOAP12 , NO_ACTION , NO_EXPECTED_RETURN_ACTION );
		} catch (XdsFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoadKeystoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EnvironmentNotSelectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(soap.getOutHeader());
	}

}
