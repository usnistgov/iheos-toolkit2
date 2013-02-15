package gov.nist.direct.logger;

import java.io.File;
import java.util.ArrayList;

public class LoggerUtils {
	
	public static ArrayList<String> listFilesForFolder(final String folder) {
		File f = new File(folder);
		ArrayList<String> list = new ArrayList<String>();
	    for (final File fileEntry : f.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            list.add(fileEntry.getName());
	           // if the element is not a folder, ignore it.
	        	}
	    }
		return list;
}

	
}