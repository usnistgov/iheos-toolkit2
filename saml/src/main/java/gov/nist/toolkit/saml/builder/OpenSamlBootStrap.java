package gov.nist.toolkit.saml.builder;

import gov.nist.toolkit.saml.util.SAMLCallback;
import gov.nist.toolkit.saml.util.SamlTokenExtractor;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLException;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.Configuration;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Srinivasarao.Eadara
 *
 */
public class OpenSamlBootStrap {
	private static XMLObjectBuilderFactory builderFactory;
    public static MarshallerFactory marshallerFactory;
    public static UnmarshallerFactory unmarshallerFactory;
    public static boolean samlEngineInitialized = false;
   
    public static SAMLCallback samlCallBack = null ;
    private static Logger log = Logger.getLogger(SamlTokenExtractor.class.getName());
    
    
    /**
	 * @return the unmarshallerFactory
	 */
	public static UnmarshallerFactory getUnmarshallerFactory() {
		return unmarshallerFactory;
	}



	/**
	 * @param unmarshallerFactory the unmarshallerFactory to set
	 */
	public static void setUnmarshallerFactory(
			UnmarshallerFactory unmarshallerFactory) {
		OpenSamlBootStrap.unmarshallerFactory = unmarshallerFactory;
	}



	/**
	 * @return the builderFactory
	 */
	public static XMLObjectBuilderFactory getBuilderFactory() {
		return builderFactory;
	}
	
    /**
     * Initialise the SAML library
     */
    public synchronized static void initSamlEngine() {
        if (!samlEngineInitialized) {
            log.fine("Initilizing the opensaml2 library...");
            try {
            	DefaultBootstrap.bootstrap();
            	builderFactory = Configuration.getBuilderFactory();
                marshallerFactory = Configuration.getMarshallerFactory();
                unmarshallerFactory = Configuration.getUnmarshallerFactory();
                samlEngineInitialized = true;
                log.fine("opensaml2 library bootstrap complete");
            } catch (ConfigurationException e) {
                log.log(Level.SEVERE,
                    "Unable to bootstrap the opensaml2 library - all SAML operations will fail", 
                    e
                );
            }
        }
    }

}
