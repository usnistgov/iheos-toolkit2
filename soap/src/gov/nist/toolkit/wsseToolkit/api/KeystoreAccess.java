package gov.nist.toolkit.wsseToolkit.api;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * KeystoreAccess provides easy access to a keystore info.
 * It expects the keystore to be configured with a unique private key
 * 
 * @author gerardin
 * 
 */
public class KeystoreAccess {

	private KeyStore keystore;
	public KeyPair keyPair;
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public Certificate certificate;

	public KeystoreAccess(String storePath, String storePass, String privateKeyAlias, String privateKeyPass)
			throws KeyStoreException {

		try {
			keystore = loadKeyStore(storePath, storePass);
			loadKeyStoreInfo(privateKeyAlias, privateKeyPass);
		} catch (KeyStoreException e) {
			throw new KeyStoreException("cannot properly access keystore located at : " + storePath, e);
		}
	}

	private void loadKeyStoreInfo(String privateKeyAlias, String privateKeyPass) throws KeyStoreException {
		if( ! keystore.containsAlias(privateKeyAlias)){
			throw new KeyStoreException("alias not found : " + privateKeyAlias);
		}
		
		try {
			privateKey = (PrivateKey) keystore.getKey(privateKeyAlias, privateKeyPass.toCharArray());
			certificate = keystore.getCertificate(privateKeyAlias);
			publicKey = certificate.getPublicKey();
			keyPair = new KeyPair(publicKey, privateKey);
		} catch (Exception e) {
			throw new KeyStoreException("cannot retrieve info from keystore for alias : " + privateKeyAlias, e);
		}
	}

	private KeyStore loadKeyStore(String store, String sPass) throws KeyStoreException {
		try {
			KeyStore mykeystore = KeyStore.getInstance("JKS");
			FileInputStream fis = new FileInputStream(store);
			mykeystore.load(fis, sPass.toCharArray());
			fis.close();
			return mykeystore;
		} catch (Exception e) {
			throw new KeyStoreException("cannot load keystore with pass : " + sPass, e);
		}
	}
}
