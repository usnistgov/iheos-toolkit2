package gov.nist.toolkit.common.epres;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import gov.nist.toolkit.utilities.io.Io;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;

/**
 *  Converts EDI input to XML output using the
 *  default XSLT transformer.
 *
 * If an input-file is not specified, System.in is used;
 * if an output-file is not specified, System.out is used.
 *
 * @author  Linan
 */

public class EdiToXml {
	
	String generatedOutput;
	String inputMessage;
	ContentHandler handler;
	EDIReader parser;
	InputSource inputSource;


	public EdiToXml() {}

	/**
	 *Constructor for the EDItoXML object
	 *
	 * @param  input   file containing EDI-structured data
	 * @param  output  XML file
	 */
	public EdiToXml(String input) {
		
		inputSource = new InputSource(Io.bytesToInputStream(input.getBytes()));
		generatedOutput = new String();

//		// Establish inputSource, a SAX InputSource
//		if (inputMessage != null) {
//			inputSource = new InputSource(inputMessage.trim());
//		} else {
//			inputSource = new InputSource("");
//		}
	}
	
	/**
	  *  Main processing method for the EDItoXML object
	  */
	public void run() {
		try {
			// Establish an XMLReader which is actually an EDIReader.
			System.setProperty("javax.xml.parsers.SAXParserFactory","com.berryworks.edireader.EDIParserFactory");
			SAXParserFactory sFactory = SAXParserFactory.newInstance();
			SAXParser sParser = sFactory.newSAXParser();
			XMLReader ediReader = sParser.getXMLReader();

			// Establish the SAXSource
			SAXSource source = new SAXSource(ediReader, inputSource);

			// Establish an XSL Transformer to generate the XML output.
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			
			Writer outWriter = new StringWriter();  
			StreamResult result = new StreamResult( outWriter );  
			
			// The StreamResult to capture the generated XML output.
//			StreamResult result = new StreamResult(generatedOutput);
				
			// Call the XSL Transformer with no stylesheet to generate
			// XML output from the parsed input.
			transformer.transform(source, result);
			this.generatedOutput = outWriter.toString();
			System.out.print("\nTransformation complete\n");
		} catch (SAXException e) {
			System.out.println("\nUnable to create EDIReader: " + e);
		} catch (ParserConfigurationException e) {
			System.out.println("\nUnable to create EDIReader: " + e);
		} catch (TransformerConfigurationException e) {
			System.out.println("\nUnable to create Transformer: " + e);
		} catch (TransformerException e) {
			System.out.println("\nFailure to transform: " + e);
		}
	}
		
		public String getGeneratedOutput() {
			if(generatedOutput == null)
			{
				return "";
			}
			return generatedOutput;
		}

//		public void setGeneratedOutput(String generatedOutput) {
//			this.generatedOutput = generatedOutput.toString();
//		}

		public ContentHandler getHandler() {
			return handler;
		}

		public void setHandler(ContentHandler handler) {
			this.handler = handler;
		}

		public String getInputMessage() {
			return inputMessage;
		}

		public void setInputMessage(String inputMessage) {
			this.inputMessage = inputMessage;
		}
		static public Boolean isEDI(String input){
			if (input.trim().charAt(0)== '<'){
				return false;
			}
			return true;
		}
}
