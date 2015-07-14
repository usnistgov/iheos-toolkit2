package gov.nist.toolkit.testengine;

import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.XdsIOException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;

import sun.misc.BASE64Decoder;

public class Mtom {
	private OMElement document;
	private String content_type = null;
	private byte[] contents = null;
	private boolean xop;
//	String contents_string = "";
	
	public boolean isOptimized() { return xop; }

	public void decode(OMElement document) throws XdsIOException, IOException {
		this.document = document;
		OMText binaryNode = (OMText) document.getFirstOMChild();
		//System.out.println("isOptimized: " + binaryNode.isOptimized());

		xop = binaryNode.isOptimized(); 
		
		if (xop) {
			Object xhandler = binaryNode.getDataHandler();
			if ( !(xhandler instanceof javax.activation.DataHandler)) 
				throw new IOException("Expected instance of javax.activation.DataHandler, got instead " + xhandler.getClass().getName());
			javax.activation.DataHandler datahandler = (javax.activation.DataHandler) xhandler;
			InputStream is = null;
			try {
				is = datahandler.getInputStream();
				contents = Io.getBytesFromInputStream(is);
			}
			catch (IOException e) {
				throw new XdsIOException("Error accessing XOP encoded document content from message");
			}
			content_type = datahandler.getContentType();
		} else {
			String base64 = binaryNode.getText();
			BASE64Decoder d  = new BASE64Decoder();
			contents = d.decodeBuffer(base64);
			content_type = null;
//			contents_string = getContentsAsString();
		}
//		System.out.println("<Contents>" + contents_string + "</Contents>");
	}
	
	public String getContentsAsString() {
		if (contents == null )
			return "";
		return new String(contents);
	}

	public String getContent_type() {
		return content_type;
	}

	public byte[] getContents() {
		return contents;
	}
	
}
