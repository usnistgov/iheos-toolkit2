package gov.nist.registry.xdstools2.server.gazelle.actorConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class CSVEntryTest {

	@Test
	public void testSingle() {
		CSVEntry entry = new CSVEntry("a");
		assertTrue(entry.getItems().size() == 1);
		assertEquals(entry.get(0), "a");
	}

	@Test
	public void testTwo() {
		CSVEntry entry = new CSVEntry("a,b");
		assertTrue(entry.getItems().size() == 2);
		assertEquals(entry.get(0), "a");
		assertEquals(entry.get(1), "b");
	}

	@Test
	public void testSingleQuoted() {
		CSVEntry entry = new CSVEntry("\"a\"");
		assertTrue(entry.getItems().size() == 1);
		assertEquals(entry.get(0), "a");
	}

	@Test
	public void testTwoQuoted() {
		CSVEntry entry = new CSVEntry("\"a\",\"b\"");
		assertTrue(entry.getItems().size() == 2);
		assertEquals(entry.get(0), "a");
		assertEquals(entry.get(1), "b");
	}

	@Test
	public void testEmbeddedSpace() {
		CSVEntry entry = new CSVEntry("\"a n\",\"b\"");
		assertTrue(entry.getItems().size() == 2);
		assertEquals(entry.get(0), "a n");
		assertEquals(entry.get(1), "b");
	}

	@Test
	public void testEmpty() {
		CSVEntry entry = new CSVEntry("\"a\",\"\",\"b\"");
		assertTrue(entry.getItems().size() == 3);
		assertEquals(entry.get(0), "a");
		assertEquals(entry.get(1), "");
		assertEquals(entry.get(2), "b");
	}

	@Test
	public void testExtraComma() {
		CSVEntry entry = new CSVEntry("\"a\",\"b\", ");
		assertTrue(entry.getItems().size() == 2);
		assertEquals(entry.get(0), "a");
		assertEquals(entry.get(1), "b");
	}


}
