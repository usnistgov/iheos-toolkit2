package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class ConfigPull {
	static Logger logger = Logger.getLogger(ConfigPull.class.getName());
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
	

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[]{tm}, null);
		}


		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
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

		HttpClient client = getNewHttpClient();
		HttpGet request = new HttpGet(u);
		HttpResponse response = client.execute(request);
		int code = response.getStatusLine().getStatusCode();
		logger.info("Request status is " + code);


		InputStream x = response.getEntity().getContent();
		byte[] data = Io.getBytesFromInputStream(x);

		Io.bytesToFile(new File(actorsDir + File.separator + "all.csv"), data);
	}

	/**
	 * Pull a single system configuraton from Gazelle
	 * @param systemName
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws Exception
	 */
	public void pull(String systemName) throws Exception {
		try {
			logger.fine("Pull from Gazelle(" + systemName + ")");

			String systemNameTr = systemName.replaceAll(" ", "%20");

			String u = url +
					//"&configurationType=WebServiceConfiguration" +
					"&systemKeyword=" + systemNameTr;

			logger.info("Request configuration from Gazelle - " + u);


			HttpClient client = getNewHttpClient();
			HttpGet request = new HttpGet(u);
			HttpResponse response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			logger.info("Request status is " + code);

			InputStream x = response.getEntity().getContent();
			byte[] data = Io.getBytesFromInputStream(x);

			String dataString = new String(data);
			logger.info("config is " + dataString);

		String stuff = new String(data);
		logger.info("Config is " + stuff);
		
			Io.bytesToFile(new File(actorsDir + File.separator + systemName + ".csv"), data);
		} catch (Exception ex) {
			logger.severe("pull - exception + " + ExceptionUtil.exception_details(ex));
			throw ex;
		}
	}
	
	public static void main(String[] args) {
		ConfigPull pa = new ConfigPull(
//				"http://ihe.wustl.edu/gazelle-na/systemConfigurations.seam?testingSessionId=35&configurationType=WebServiceConfiguration",
				"https  ://gazelle.ihe.net/EU-CAT/systemConfigurations.seam?testingSessionId=35&configurationType=WebServiceConfiguration",
				new File("/Users/bill/tmp/toolkit2/actors"));
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
