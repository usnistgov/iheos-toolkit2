package gov.nist.toolkit.valregmsg.xdm;

import gov.nist.toolkit.utilities.io.Sha1Bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArray {

	byte[] ba;
	int size;
	
	public ByteArray(byte[] ba) {
		this.ba = ba;
		this.size = ba.length;
	}
	
	public InputStream getInputStream() {
		return new ByteArrayInputStream(ba);
	}
	
	public int getSize() {
		return size;
	}
	
	public String getSizeAsString() {
		return Integer.toString(size);
	}
	
	public String getSha1() {
		Sha1Bean sb = new Sha1Bean();
		String sha1 = "";
		sb.setByteStream(ba);
		try {
			sha1 = sb.getSha1String();
		} catch (Exception e) {
		}
		return sha1;
	}

	public byte[] get() { return ba; }
	
}
