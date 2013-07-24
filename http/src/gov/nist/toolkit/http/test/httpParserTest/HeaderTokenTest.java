package gov.nist.toolkit.http.test.httpParserTest;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.http.HeaderToken;

import org.junit.Test;

public class HeaderTokenTest {

	@Test
	public void equalsTest() {
		assertTrue(HeaderToken.LT.equals(HeaderToken.LT));
	}
}
