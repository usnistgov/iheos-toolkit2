package gov.nist.toolkit.http.httpclient;

public class HttpGet {

	
	static public void main(String[] args) {
		if ( args.length != 1) {
			System.err.println("httpget: Single argument required, URL of page to get");
			System.exit(1);
		}
		String url = args[0];
		try {
			HttpClient hc = new HttpClient();
			byte[] data = hc.httpGetBytes(url);
			System.out.println(new String(data));
			System.exit(0);
		} 
		catch (Exception e) {
			System.err.println("Failed to read document from URI: " + url + " - " + e.getMessage());
			System.exit(2);
		}
	}
}
