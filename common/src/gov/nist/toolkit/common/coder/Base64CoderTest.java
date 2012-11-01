package gov.nist.toolkit.common.coder;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;


public class Base64CoderTest  extends TestCase {
	
	String stringToBytesToString(String in) throws UnsupportedEncodingException {
		byte[] buffer = in.getBytes("UTF-8");
		return new String(buffer);
	}

	String run(String s) throws UnsupportedEncodingException {
		String sb64 = Base64Coder.encodeString(s);
		String s2 = Base64Coder.decodeString(sb64);
		
		assertTrue(s + " != " + s2, s2.equals(s));
		
		return s2;
	}
	
	public void testShortString() throws UnsupportedEncodingException {
		String s = "this is a test";

		run(s);
	}
	
	public void testGallow() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/gallowYounger.xml"));
		
        run(fin);
	}
	
	public void testSchnur() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur.xml"));
		
        run(fin);
	}
	
	public void testRunGallow() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/gallowYounger.xml"));
		
        String fout = run(fin);
		
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/gallowYounger2.xml"));
        fos.write(fout.getBytes());
        fos.close();
	}
	
	public void testRunSchnur() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur.xml"));
		
        String fout = run(fin);
		
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur2.xml"));
        fos.write(fout.getBytes());
        fos.close();
	}
	
	public void testRunCode() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/code.java"));
		
        String fout = run(fin);
		
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/code2.java"));
        fos.write(fout.getBytes());
        fos.close();
	}
	
	public void testS2B2S() throws Exception {
        String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/test1.xml"));
				
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/test12.xml"));
        fos.write(fin.getBytes("UTF-8"));
        fos.close();
		
	}
	
	public void testB2B() throws Exception {
		InputStream f_in = new FileInputStream("/Users/bill/ihe/testing/sha1/utf8ccds/test2.xml");
		byte[] in_bytes = Io.getBytesFromInputStream(f_in);
				
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/test22.xml"));
        fos.write(in_bytes);
        fos.close();
		
	}
	
	// This works for utf-8 inputs
	
	public void testB2BBase64() throws Exception {
		InputStream f_in = new FileInputStream("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur.xml");
		byte[] in_bytes = Io.getBytesFromInputStream(f_in);
		
		String base64 = Base64Coder.encodeToString(in_bytes);
		
		byte[] out_bytes = Base64Coder.decode(base64);
				
		assertTrue("out_bytes byte count does not match", in_bytes.length == out_bytes.length);
		
		for (int i=0; i<in_bytes.length; i++) {
			assertTrue("out_bytes byte " + i + " is different", in_bytes[i] == out_bytes[i]);
		}
		
		String str = new String(in_bytes);
		byte[] str_bytes = str.getBytes();  // don't know why this works, it doesn't in other cases

		assertTrue("str_bytes byte count does not match " + str_bytes.length + " != " + in_bytes.length, in_bytes.length == str_bytes.length);
		for (int i=0; i<in_bytes.length; i++) {
			assertTrue("str_bytes byte " + i + " is different", in_bytes[i] == str_bytes[i]);
		}
		
        FileOutputStream fos = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/test32.xml"));
        fos.write(out_bytes);
        fos.close();
		
        FileOutputStream fos2 = new FileOutputStream(new File("/Users/bill/ihe/testing/sha1/utf8ccds/test33.xml"));
        fos2.write(str_bytes);
        fos2.close();
		
	}
	
	public boolean isUsAscii(byte[] in) {
		return !bit8(in);
	}
	
	public boolean bit8(byte[] in) {
		for (int i=0; i<in.length; i++) {
			int a = in[i] & (byte)0x80;
			if (a != 0)
				return true;
		}
		return false;
	}
	
	public void testUTF8T1() throws Exception {
		InputStream f_in = new FileInputStream("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur.xml");
		byte[] in_bytes = Io.getBytesFromInputStream(f_in);
		assertFalse(isUsAscii(in_bytes));
	}
	
	public void testUTF8T2() throws Exception {
		InputStream f_in = new FileInputStream("/Users/bill/ihe/testing/sha1/utf8ccds/annaSchnur2.xml");
		byte[] in_bytes = Io.getBytesFromInputStream(f_in);
		assertTrue(isUsAscii(in_bytes));
	}
	
}
