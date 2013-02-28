/*
 * Io.java
 *
 * Created on January 17, 2004, 2:39 PM
 */

package gov.nist.toolkit.utilities.io;

import gov.nist.toolkit.utilities.xml.OMFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.om.OMElement;


/**
 * A collection of various IO utility methods.
 * @author Bill Majurski
 */
public class Io {

	/** Creates a new instance of Io */
	public Io() {
	}

	/**
	 * Given an InputStream, converts it to a String.
	 * @param in The InputStream to read.
	 * @throws java.io.IOException Thrown if there is an error accessing this InputStream.
	 * @return The String containing the contents of the InputStream.
	 */
	static public String getStringFromInputStream(InputStream in) throws java.io.IOException {
		int count;
		byte[] by = new byte[256];

		StringBuffer buf = new StringBuffer();
		while ( (count=in.read(by)) > 0 ) {
			for (int i=0; i<count; i++) {
				by[i] &= 0x7f;
			}
			buf.append(new String(by,0,count));
		}
		return new String(buf);
	}

	static byte[] resize(byte[] in, int size, int new_size) {
		byte[] out = new byte[new_size];

		for (int i =0; i<size && i<new_size; i++) {
			out[i] = in[i];
		}

		return out;
	}


	static public byte[] getBytesFromInputStream(InputStream in) throws java.io.IOException {
		int count=0;
		int allocation = 4096;
		byte[] by = new byte[allocation];
		int read_size = allocation;

		int current_size = allocation;
		int offset = 0;

		count = in.read(by, offset, read_size);
		while ( true ) {
			if (count <= 0)
				return resize(by, offset, offset);
			if (count == read_size) {
				by = resize(by, offset+count, offset+count+allocation);
				offset = offset+count;
				read_size = by.length - offset; 
				current_size = by.length;
			} else {
				offset = offset+count;
				read_size = by.length - offset; 
				current_size = by.length;
			}
			count = in.read(by, offset, read_size);
		}
	}

	//	static public byte[] getBytesFromInputStream(InputStream in) throws java.io.IOException {
	//	int count;
	//	int allocation = 4096;
	//	byte[] by = new byte[allocation];
	//	int read_size = allocation;

	//	int current_size = allocation;
	//	int offset = 0;

	//	count = in.read(by, offset, read_size);
	//	while ( true ) {
	//	if (count < read_size)
	//	return resize(by, offset + count, offset+count);
	//	by = resize(by, count, current_size * 2);

	//	offset = current_size;
	//	read_size = by.length - offset; 
	//	current_size = by.length;
	//	count = in.read(by, offset, read_size);
	//	}
	//	}

	/**
	 * Given a File, return the contents as a String.
	 * @param file The File to be read.
	 * @throws java.io.IOException Results if there is an IO error accessing the file.
	 * @return The contents of the file as a String.
	 */
	static public String stringFromFile(File file) throws IOException {
		if ( !file.exists())
			throw new FileNotFoundException(file + " cannot be read");
		FileInputStream is = new FileInputStream(file);
		return getStringFromInputStream(is);

	}
	
	static public InputStream getInputStreamFromFile(File file) throws FileNotFoundException {
		if ( !file.exists())
			throw new FileNotFoundException(file + " cannot be read");
		FileInputStream is = new FileInputStream(file);
		return is;
	}

	static public byte[] bytesFromFile(File file) throws IOException {
		if ( !file.exists())
			throw new FileNotFoundException(file + " cannot be read");
		FileInputStream is = new FileInputStream(file);
		return getBytesFromInputStream(is);

	}

	/**
	 * Copies the bytes of an InputStream to an OutputStream.
	 * @param is Input as an InputStream.
	 * @param os Output as an OutputStream.
	 * @throws java.io.IOException If there is an IO error accessing the InputStream or the OutputStream.
	 */
	static public void copyBytes(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[256];
		int cnt;
		while ( (cnt=is.read(buf)) > 0) {
			os.write(buf, 0, cnt);
		}
	}

	static public String stringFromBufferedReader(BufferedReader in) throws IOException {
		StringBuffer buf = new StringBuffer();

		char[] chars = new char[2048];

		int count;

		count = in.read(chars, 0, chars.length);
		while(count > -1) {
			if (count > 0)
				buf.append(chars, 0, count);
			count = in.read(chars, 0, chars.length);
		}

		return buf.toString();
	}

	static public void stringToFile(File file, String string) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(string);
		out.close();	
	}
	
	static public void xmlToFile(File file, OMElement xml) throws IOException {
		stringToFile(file, new OMFormatter(xml).toString());
	}

	static public void bytesToFile(File file, byte[] content) throws IOException {
		OutputStream out = new FileOutputStream(file);
		out.write(content);
		out.close();	
	}
	
	static public InputStream bytesToInputStream(byte[] in) {
		return new ByteArrayInputStream(in);
	}

	static public InputStream stringToInputStream(String in) {
		return new ByteArrayInputStream(in.getBytes()); 
	}
	
	/**
	 * Delete file, recursively if file represents a directory.
	 * @param f
	 */
	static public void delete(File f) {
		if (!f.exists())
			return;
		if (f.isDirectory()) {
			String[] contents = f.list();
			for (int i=0; i<contents.length; i++) 
				delete(new File(f + File.separator + contents[i]));
			f.delete();
		} else if (f.isFile()){
			f.delete();
		}
	}

}
