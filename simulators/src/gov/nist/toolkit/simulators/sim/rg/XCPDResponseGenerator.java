package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;


public class XCPDResponseGenerator extends TransactionSimulator implements XCPDResponseGeneratingSim {

	OMElement xcpd_response = null;
	Exception startUpException = null;

	public XCPDResponseGenerator(SimCommon common) {
		super(common);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {	
       xcpd_response =  getXCPDResponse();	
	}

	public OMElement getXCPDResponse() {
			
		if (xcpd_response == null) {
			InputStream inputStream = null;
			String my_response = null;
		
			try {
				String warHome = System.getProperty("warHome");
				String path = warHome + File.separator + "toolkitx" + File.separator + "testkit" + File.separator + "xcpd" + File.separator + "FindPatientResponse" + File.separator + "FindPatientResponse.xml";
				System.out.println("[getXCPDResponse] Current directory: " + path);
				File response_message = new File(path);
				my_response = stringFromFile(response_message);
		    	
				if (my_response == null) { 
					System.out.println("Cannot load FindPatientResponse.xml or file is empty"); 
				//	return response; 
					return xcpd_response;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			try {			
				xcpd_response = AXIOMUtil.stringToOM(my_response);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return xcpd_response;
			
		}
		
	return xcpd_response;
	}

	
	
	// --------------------------------------------
	//  Read files methods 
	// --------------------------------------------
	static public String stringFromFile(File file) throws IOException {
		if ( !file.exists())
			throw new FileNotFoundException(file + " cannot be read");
		FileInputStream is = new FileInputStream(file);
		return getStringFromInputStream(is);

	}
	
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


}
