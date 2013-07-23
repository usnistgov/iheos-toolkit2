/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: William Majurski
		 Frederic de Vaulx
		 Diane Azais
		 Julien Perugini
		 Antoine Gerardin
		
 */

package gov.nist.direct.test.java.messageProcessor.impl;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import gov.nist.direct.messageProcessor.direct.directImpl.DirectMimeMessageProcessor;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.direct.utils.Utils;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.ErrorRecorderAdapter;
import gov.nist.toolkit.errorrecording.client.ValidatorErrorItem;
import gov.nist.toolkit.errorrecording.client.VelocitySingleton;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorder;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Test;

public class DirectMimeMessageProcessorTest {

	@Test
	public void testProcessAndValidateDirectMessage() {
		ErrorRecorder er = new GwtErrorRecorder();

		String messageUnderTestPath = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/encrypted_boris.txt";
		byte[] messageUnderTest = null;
		
		String certificatePath = "direct/src/gov/nist/direct/test/java/messageProcessor/impl/hit-testing.nist.gov.p12";
		byte[] certificate = null;
		
		String certificatePassword = "";
		
		// Uses either a normal string parser or an http parser.
		messageUnderTest = Utils.getMessage(messageUnderTestPath);
		certificate = Utils.getMessage(certificatePath);
		
		DirectMimeMessageProcessor messageValidator = new DirectMimeMessageProcessor();
		messageValidator.processAndValidateDirectMessage(er, messageUnderTest, certificate, certificatePassword, new ValidationContext());
		
		//er.detail("\n#############################");
		System.out.println(er);
		//er.detail("#############################");
		

		ArrayList<ValidatorErrorItem> list = new ArrayList<ValidatorErrorItem>();
		try {
			for(int k=0;k<er.getErrMsgs().size();k++) {
					list.add(er.getErrMsgs().get(k));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		ErrorRecorderAdapter erAd = new ErrorRecorderAdapter(list);
		VelocityEngine ve = VelocitySingleton.getVelocityEngine();
		try {
			//ve.init();
			//  next, get the Template
			Template t = ve.getTemplate("gov/nist/toolkit/errorrecording/client/DirectValidationReport.vm");
			//  create a context and add data
			VelocityContext context = new VelocityContext();
			context.put("summary", erAd.getSummary());
			context.put("validationReport", erAd.getDetailed());
			// now render the template into a StringWriter
			StringWriter writer = new StringWriter();
			t.merge( context, writer );
			// show the World
			System.out.println( writer.toString() );
			FileWriter fileWriter = new FileWriter(new File("reportTest.html"));
			fileWriter.write(writer.toString());
			fileWriter.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testProcessPart() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessEnvelope() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessText() {
		fail("Not yet implemented");
	}

}
