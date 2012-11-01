/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Author: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.direct.utils;

import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

	protected static String defaultLogFile = "output\result";
	protected static String extension = ".txt";

	public static void write(String s) throws IOException {
		String path = defaultLogFile + "001" + extension;
		write(path, s);
	}

	public static void write(String f, String s) throws IOException {

		FileWriter aWriter = new FileWriter(f, true);
		aWriter.write(s + "\n");
		aWriter.flush();
		aWriter.close();
	}
	
	
}
