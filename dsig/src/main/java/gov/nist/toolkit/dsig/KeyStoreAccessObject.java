package gov.nist.toolkit.dsig;

import gov.nist.toolkit.securityCommon.SecurityParams;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.LoadKeystoreException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyStoreAccessObject {

	private File keystore = null;
	private String type = "JKS";
	private String keystorePassword = null;
	private KeyStore ks = null;
	private String alias = null;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	// This is a fix before we can review the configuration mechanism thoroughly.
	// securityParams are found in the session stored in Soap.class
	// We pass the instance around to used it here in the keystore configuration.
	private SecurityParams securityParams;

	private static KeyStoreAccessObject singleton = null;
	private static Log logger = LogFactory.getLog(KeyStoreAccessObject.class);
	
	private KeyStoreAccessObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public synchronized static KeyStoreAccessObject getInstance(SecurityParams securityParams) throws Exception
	{
		if (singleton == null) {
			singleton = new KeyStoreAccessObject ();
			singleton.setSecurityParams(securityParams);
	    	singleton.loadKeyStore("JKS");
		}
		
		return singleton;
	}

	private void loadKeyStore(String type)
	throws  NoSuchAlgorithmException, LoadKeystoreException
	{
//		try { 
			//  ****************************************************************************
			//** Retrieve KeyStore  URLs and passwords from WAS Env Provider
			//Resource environment entries > MedVA ICServices configuration properties > Custom properties > KeystoreURL    
			//----------------------------------------------------------------------------
//				InitialContext ctx = new InitialContext();
//				ResourceEnvironmentProviderConfig icSvcConfig = 
//					(ResourceEnvironmentProviderConfig)ctx.lookup("rep/ICServiceConfig");
//				String keystoreURL = (String)icSvcConfig.getAttribute("KeystoreURL");
//				this.keystorePassword = (String)icSvcConfig.getAttribute("KeystorePassword");

//			String keystoreURL = "/Users/bill/dev/xdstoolkit/xdstest/keystores/NA2010/keystore";
			//String keystoreURL = System.getProperty("DSIG_keystore_url");
			// read the properties file in the environment directory.
			//
			
			Properties props = new Properties();

			//String root = System.getProperty("warHome");
			String cache_root = System.getProperty("External_Cache");
			String environmentName = System.getProperty("Environment_Name");
			
			File keystoreDir = null;
			try {
				keystoreDir = securityParams.getKeystoreDir();
			} catch (EnvironmentNotSelectedException e2) {
				throw new LoadKeystoreException(e2.getMessage(), null ,e2 );
			}
			
			String pathToKeystoreProperties = keystoreDir + File.separator + "keystore.properties";
			// String path = cache_root + File.separator + "environment" + File.separator + environmentName + File.separator + "keystore" + File.separator + "keystore.properties";
			
			try {
				
				props.load(new FileInputStream(new File(pathToKeystoreProperties))); 
			} catch (IOException e) {
				throw new LoadKeystoreException("Cannot load DSIG property file from " + keystoreDir, null, e);
				//throw new LoadKeystoreException("Cannot load DSIG property file from " + pathToKeystoreProperties, null, e);
			}

			String keystoreURL = keystoreDir + File.separator + props.getProperty("keystore_url");
			this.keystorePassword = props.getProperty("DSIG_keystore_password");
			this.alias = props.getProperty("DSIG_keystore_alias");
			System.out.println("DSIG: keystore is " + keystoreURL);
			System.out.println("DSIG: alias is " + alias);
			logger.info("HttpClientAdapter.createHttpAdapter Keystore URL: " + keystoreURL);

			this.keystore =  new File (keystoreURL);
			if (this.keystore == null)
				throw new LoadKeystoreException("The Keystore path " + keystoreURL + " as taken from the DSIG property file " + pathToKeystoreProperties + " was invalid", null);

			if (type!=null && type.length()>0){
				this.type = type;
			} 

			try {
				this.ks = KeyStore.getInstance(this.type);
			} catch (KeyStoreException e1) {
				throw new LoadKeystoreException(e1.getMessage(), null, e1);
			}
			char[] passwordAsChar = keystorePassword.toCharArray();
			if (this.ks == null){
				throw new LoadKeystoreException("The Keystore cannot be instantiated.", null);
			} else {
				logger.info(this.ks.getType());
			}

			FileInputStream fis;
			try {
				fis = new FileInputStream(this.keystore);
			} catch (FileNotFoundException e) {
				throw new LoadKeystoreException(e.getMessage(),null, e);
			}
			try {
				this.ks.load(fis, passwordAsChar);
			} catch (CertificateException e) {
				throw new LoadKeystoreException(e.getMessage(),null, e);
			} catch (IOException e) {
				throw new LoadKeystoreException(e.getMessage(),null, e);
			}
			//setAlias();

			//get the Private Key
//			KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(
//					  this.alias, new KeyStore.PasswordProtection(this.keystorePassword.toCharArray()));
//			privateKey = keyEntry.getPrivateKey();
			try {
				privateKey = ((PrivateKey)this.ks.getKey(this.alias, this.keystorePassword.toCharArray()));
			} catch (UnrecoverableKeyException e) {
				throw new LoadKeystoreException(e.getMessage(),null, e);
			} catch (KeyStoreException e) {
				throw new LoadKeystoreException(e.getMessage(),null, e);
			}
			
			if (privateKey==null){
				throw new LoadKeystoreException("The Private key is null" /*; Generating key pair from key store"*/, null);
			} else {
				//get the Public Key
				Certificate cert;
				try {
					cert = this.ks.getCertificate(this.alias);
				} catch (KeyStoreException e) {
					throw new LoadKeystoreException(e.getMessage(),null, e);
				}
				logger.info("Cert type:: " +  cert.getType());
				publicKey = cert.getPublicKey();

			}
			
			
			
//		} catch (Exception e) {
//			logger.info("Generating key pair");
//			this.generateKeys("RSA", 512);
//			
//			e.printStackTrace();
//			//throw new AdapterException(e, 1);
//		}


	}

//	private void setAlias()
//	throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
//	{
//		if (this.ks == null)
//		{
//			return;
//		}
//
//		Enumeration en = this.ks.aliases();
//
//		ArrayList vectaliases = new ArrayList();
//
//		while (en.hasMoreElements())
//			vectaliases.add(en.nextElement());
//		String[] aliases = (String[])vectaliases.toArray(new String[0]);
//
//		for (int i = 0; i < aliases.length; ++i){
//			System.out.println("alias = " + i + " " + aliases[i]);
//			if (this.ks.isCertificateEntry(aliases[i])){
//				//this.alias = aliases[i];
//				System.out.println("alias = " + aliases[i]);
//				//return;
//				//get the Private Key
//				Key pKey = this.ks.getKey(aliases[i], this.keystorePassword.toCharArray());
//				if (pKey==null){
//					System.out.println("The Private key is null for alias: " + aliases[i]);
//				}
//			}
//		} 
//	}

//	public static void writeKeyToKeystore(Key key, File keystoreFile) throws Exception
//	{
//		//keystoreFile.delete();
//
//		// Take your pick of keystore types
//		String keystoreType = KeyStore.getDefaultType(); // or "JCEKS" or ...
//		KeyStore ks = KeyStore.getInstance(keystoreType);
//		char[] storePassword = "store-password".toCharArray();
//		char[] keyPassword = "key-password".toCharArray();
//
//		// Create an empty keystore
//		ks.load(null, storePassword);
//
//		ks.setKeyEntry("desede_key", key, keyPassword, null);
//
//		FileOutputStream fos = new FileOutputStream(keystoreFile);
//		ks.store(fos, storePassword);
//		fos.close();
//	}
//

	private String formatKey(Key key){
		StringBuffer sb = new StringBuffer();
		String algo = key.getAlgorithm();
		String fmt = key.getFormat();
		byte[] encoded = key.getEncoded();
		sb.append("Key[algorithm=" + algo + ", format=" + fmt +
				", bytes=" + encoded.length + "]\n");
		if (fmt.equalsIgnoreCase("RAW")){
			sb.append("Key Material (in hex):: ");
			// sb.append(Util.byteArray2Hex(key.getEncoded()));
		}
		return sb.toString();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public X509Certificate getX509Certificate() throws KeyStoreException {
		return ((X509Certificate)this.ks.getCertificate(this.alias));
	}

	public void setKeystoreUrl (File keystore, String password)
	{
		this.keystore = keystore;
		this.keystorePassword= password;
	}

	private void generateKeys(String type, int bitsize) throws NoSuchAlgorithmException {
		System.out.println("Generating key pair for " + type);
		// Create a KeyPair
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(type);
		kpg.initialize(bitsize);
		KeyPair kp = kpg.generateKeyPair();
		this.publicKey = kp.getPublic();
		this.privateKey = kp.getPrivate();
	}

	//this won't work with no security params -Antoine
	public static void main(String[] args){
		try {
			SecurityParams NO_SECURITY_PARAMS = null;
			KeyStoreAccessObject ksl = getInstance(NO_SECURITY_PARAMS);
			
			X509Certificate cert = ksl.getX509Certificate();
			System.out.println("Cert version:: " +  cert.getVersion());
			System.out.println("Cert version:: " +  cert.getSubjectX500Principal().getName());
			
			PublicKey pubk = ksl.getPublicKey();
			PrivateKey prvk = ksl.getPrivateKey();
			System.out.println("Public Key:: " + ksl.formatKey(pubk));
			//

			System.out.println("Private Key:: " + ksl.formatKey(prvk));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void setSecurityParams(SecurityParams securityParams) {
		this.securityParams = securityParams;
	}


}
