package gov.nist.toolkit.utilities.io;

import gov.nist.toolkit.xdsexception.XdsInternalException;

public class Hash {
	public String compute_hash(ByteBuffer buffer) throws XdsInternalException {
		Sha1Bean sha = new Sha1Bean();
		sha.setByteStream(buffer.get());
		String hash = null;
		try {
			hash = sha.getSha1String();
		}
		catch (Exception e) {
			XdsInternalException ne = new XdsInternalException(e.getMessage());
			ne.setStackTrace(e.getStackTrace());
			throw ne;
		}
		return hash;
	}
	
	public String compute_hash(String doc)  throws XdsInternalException {
		return compute_hash(doc.getBytes());
	}

	public String compute_hash(byte[] bytes)  throws XdsInternalException {
		ByteBuffer b = new ByteBuffer();
		b.append(bytes, 0, bytes.length);
		return compute_hash(b);
	}
}
