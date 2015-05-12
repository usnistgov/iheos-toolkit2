package gov.nist.toolkit.errorrecording.client;

import java.io.File;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocitySingleton {
	private static VelocityEngine velocityEngine;

	public static VelocityEngine getVelocityEngine() {
		if (velocityEngine == null)
			init();
		return velocityEngine;
	}

	private static void init() {
		velocityEngine = new VelocityEngine();
		Properties velocityProperties = new Properties();
		velocityProperties.setProperty("resource.loader", "file");
		velocityProperties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		velocityProperties.setProperty("file.resource.loader.cache", "true");
		velocityProperties.setProperty("file.resource.loader.modificationCheckInterval", "2");

		// Template directory
		String absolutePath = new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()).getParentFile().getParentFile().getPath();//this goes to webapps directory
		velocityProperties.put("file.resource.loader.path", absolutePath+"/WEB-INF/templates");

		try {
			velocityEngine.init(velocityProperties);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}