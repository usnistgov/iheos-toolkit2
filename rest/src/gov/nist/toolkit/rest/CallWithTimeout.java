package gov.nist.toolkit.rest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

/*
 * This service is being built on top of the Apache Commons project 
 * Some key web pages are:
 * 
 * 
 * http://stackoverflow.com/questions/3000214/java-http-client-request-with-defined-timeout
 *     the important example is described as a "small, self-contained example" submitted to this blog
 *     on June 17, 2011 by Jeff Kirby.  In the example the class name is Example. 
 * 
 * For now this class has a public static main method to simplify testing in Eclipse.  Later
 * this will change to make it callable from the class gov.nist.toolkit.valccda.CcdaMessageValidator
 * 
 */

public class CallWithTimeout {
	   String site = "http://some.website.com/upload";
	   int timeoutSecs = 5;
	   
	   public CallWithTimeout(String URL, int timeoutInSeconds) {
		   this.site = URL;
		   this.timeoutSecs = timeoutInSeconds;
	   }

	   // upload a file and return the response as a string
	   public String post(File file, Map<String, String> parms) throws IOException, InterruptedException {
	      final Part[] multiPart = { new FilePart("file", file.getName(), file) };
	      final EntityEnclosingMethod post = new PostMethod(site);
	      HttpMethodParams hparms = new HttpMethodParams();
	      for (String parmName : parms.keySet()) {
	    	  String parmValue = parms.get(parmName);
		      hparms.setParameter(parmName, parmValue);
	      }
	      post.setRequestEntity(new MultipartRequestEntity(multiPart, post.getParams()));
	      final ExecutorService executor = Executors.newSingleThreadExecutor();
	      final List<Future<Integer>> futures = executor.invokeAll(Arrays.asList(new KillableHttpClient(post)), timeoutSecs, TimeUnit.SECONDS);
	      executor.shutdown();
	      if(futures.get(0).isCancelled()) {
	         throw new IOException(site + " has timed out. It has taken more than " + timeoutSecs + " seconds to respond");
	      }
	      return post.getResponseBodyAsString();
	   }

	   private static class KillableHttpClient implements Callable<Integer> {

	      private final EntityEnclosingMethod post;

	      private KillableHttpClient(EntityEnclosingMethod post) {
	         this.post = post;
	      }

	      public Integer call() throws Exception {
	         return new HttpClient().executeMethod(post);
	      }
	   }
	   
	   public static void main(String[] args) {
		   CallWithTimeout cwt = new CallWithTimeout("", 5);
		   String result = "";
		   Map<String, String> parms = new HashMap<String, String>();
		   parms.put("CCDA_Validation_Type", "Clinical Office Visit Summary");
		try {
			result = cwt.post(new File("CCDA_CCD_b1_Ambulatory.xml"), parms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   System.out.println(result);
	   }
}
