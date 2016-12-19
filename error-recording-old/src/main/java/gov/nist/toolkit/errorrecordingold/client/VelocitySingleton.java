package gov.nist.toolkit.errorrecordingold.client;

import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.util.Properties;

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
			e.printStackTrace();
		}
	}
}