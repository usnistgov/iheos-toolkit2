package gov.nist.toolkit.tk;

import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.LinesOfFile;

import java.io.File;
import java.io.IOException;

public class TkLoader {

	static public TkPropsServer LOAD(File file) throws IOException {
		LinesOfFile lof;
		TkPropsServer p = new TkPropsServer();
		
		if (!file.exists()) 
			throw new IOException("Property file " + file + " does not exist");
		
		lof = new LinesOfFile(file);
		while(lof.hasNext()) {
			String l = lof.next();
			p.parse(l);
		}
		
		return p;
	}
	
	static public void SAVE(TkPropsServer p, File file) throws IOException {
		String content = p.toString();
		Io.stringToFile(file, content);
	}
	
	static public TkProps tkProps(File configFile) throws Exception {
		File installedTkProps = configFile;
//		File installedTkProps = new File(Installation.installation().externalCache() + File.separator + "tk_props.txt");
		if (installedTkProps.exists())
			try {
				return TkLoader.LOAD(installedTkProps).toTkProps();
			} catch (IOException e) {
				throw new Exception("Cannot load tk_props", e);
			}
		throw new Exception("Cannot load tk_props");
//		File defaultTkProps = new File(Installation.installation().warHome() + File.separator + "WEB-INF" + File.separator +
//				"tk_props_default.txt");
//		TkProps p = null;
//		try {
//			p = TkLoader.LOAD(defaultTkProps).toTkProps();
//		} catch (IOException e) {
//			throw new Exception("Cannot load tk_props", e);
//		}
//		try {
//			TkLoader.SAVE(new TkPropsServer(p), installedTkProps);
//		} catch (IOException e) {
//			// ignore
//		}
//		return p;
	}
	

}
