package gov.nist.toolkit.valregmsg.xdm;

import gov.nist.toolkit.utilities.io.Sha1Bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArray {

	byte[] ba;
	String sha1;
	int size;
	
	public ByteArray(byte[] ba) {
		this.ba = ba;
		this.size = ba.length;
		Sha1Bean sb = new Sha1Bean();
		sb.setByteStream(ba);
		try {
			this.sha1 = sb.getSha1String();
		} catch (Exception e) {
		}
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
		return sha1;
	}
	
}
