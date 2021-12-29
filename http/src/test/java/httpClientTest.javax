package gov.nist.toolkit.http.httpclient;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

//      http://129.6.24.109:9080/Repository/129.6.58.92.9.xml
public class httpClientTest extends TestCase {
	
	public HttpURLConnection connect(URI uri) throws Exception {
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/html, text/xml, text/plain, */*");
		conn.connect();
		
		String contentType = conn.getHeaderField("Content-Type");
		System.out.println("Content type = " + contentType);

		String encoding = conn.getContentEncoding();
		System.out.println("Content Encoding = " + encoding);
		
		return conn;
	}
	
	public void testConnection() throws Exception {
		System.out.println( connect(new URI("http://129.6.24.109:9080/Repository/129.6.58.92.9.xml")).getClass().getName());
	}

}
