package gov.nist.toolkit.utilities.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinesOfFile  {
	File file;
	DataInputStream in;
	BufferedReader br;
	FileInputStream fstream;
	String strLine;

	public LinesOfFile(File file) {
		this.file = file;
		strLine = null;
		try{
			// Open the file that is the first 
			// command line parameter
			fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}	
	}

	public boolean hasNext() throws IOException {
		if (strLine == null)
			strLine = br.readLine();
		if (strLine == null)
			return false;
		return true;
	}

	public String next() throws IOException {
		hasNext();
		if (strLine == null)
			return null;
		String buf = strLine;
		strLine = null;
		return buf;
	}

	public void remove() {
	}

	public void close() throws IOException {
		in.close();
		in = null;
	}

}
