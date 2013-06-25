package gov.nist.toolkit.http.test.httpParserTest;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParser;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class HttpParserTest {

	@Test
	public void existsTest() {
		File f = new File("http/src/gov/nist/toolkit/http/resources/boundryTest");
		assertTrue(f.exists());
		assertTrue(f.isDirectory());
	}

	void assemble() {
		try {
			File f = new File("http/src/gov/nist/toolkit/http/resources/boundryTest");
			File hdrFile = new File(f, "request_hdr.txt");
			assertTrue(hdrFile.exists());
			byte[] hdr = Io.stringFromFile(hdrFile).getBytes();
			File bodyFile = new File(f, "request_body.bin");
			assertTrue(bodyFile.exists());
			byte[] body = Io.bytesFromFile(bodyFile);

			byte[] msg = new byte[hdr.length + body.length];
			System.arraycopy(hdr, 0, msg, 0, hdr.length);
			System.arraycopy(body, 0, msg, hdr.length, body.length);

			ErrorRecorder er = new TextErrorRecorder();
			new HttpParser(msg, er);
			System.out.println(er.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (HttpParseException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (HttpHeaderParseException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void assembleTest() {
		assemble();
	}
}
