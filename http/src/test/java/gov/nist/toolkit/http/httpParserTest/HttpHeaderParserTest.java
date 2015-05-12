package gov.nist.toolkit.http.test.httpParserTest;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.http.HttpHeaderParser;
import gov.nist.toolkit.http.ParseException;

import org.junit.Assert;
import org.junit.Test;

public class HttpHeaderParserTest {

	@Test
	public void nameValueTest() {
		String input = "name: value\r\n";
		HttpHeaderParser hp = new HttpHeaderParser(input);
		
		try {
			assertTrue(hp.getName().equals("name"));
			assertTrue(hp.getValue().equals("value"));
			assertTrue(hp.getUnnamedParams().size() == 0);
			assertTrue(hp.getParams().size() == 0);
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
}
