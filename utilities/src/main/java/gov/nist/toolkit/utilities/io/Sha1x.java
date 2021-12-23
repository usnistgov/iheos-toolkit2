package gov.nist.toolkit.utilities.io;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1x {
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while(two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return mdAlg("SHA-1", text);
	}

	public static String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return mdAlg("SHA-256", text);
	}


	private static String mdAlg(String alg, String text)
	throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance(alg);
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] hashByteArray = md.digest();
		return convertToHex(hashByteArray);
	}

	public static void main(String[] argv) throws Exception {

//		String in = "Jelani Nelson";
//		byte[] inb = in.getBytes();
//		Sha1Bean sb = new Sha1Bean();
//		sb.setByteStream(inb);
//		System.out.println("sha1 of 'Jelani Nelson' is " + sb.getSha1String());

		String fin = Io.stringFromFile(new File("/Users/bill/ihe/testing/sha1/utf8ccds/gallowYounger.xml"));
		String sha1 = Sha1x.SHA1(fin);
		System.out.println(sha1);

//		fin = Io.stringFromFile(new File("/Users/bill/tmp/hash/rep.txt"));
//		inb = fin.getBytes();
//		sb = new Sha1Bean();
//		sb.setByteStream(inb);
//		System.out.println("sha1 of 'rep.txt' is " + sb.getSha1String());
	}


}

