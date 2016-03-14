package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.java.test;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.xdstools2.server.gazelle.actorConfig.CSVEntry;

import org.junit.Test;

public class Entry {

	@Test
	public void simple() {
		String data="1,2,3,4,5";
		CSVEntry entry = new CSVEntry(data);
		assertTrue("1".equals(entry.get(0)));
		assertTrue("2".equals(entry.get(1)));
		assertTrue("3".equals(entry.get(2)));
		assertTrue("4".equals(entry.get(3)));
		assertTrue("5".equals(entry.get(4)));
		assertTrue("".equals(entry.get(5)));
	}

	@Test
	public void spaced() {
		String data="1 ,2 ,3,  4, 5";
		CSVEntry entry = new CSVEntry(data);
		assertTrue("1".equals(entry.get(0)));
		assertTrue("2".equals(entry.get(1)));
		assertTrue("3".equals(entry.get(2)));
		assertTrue("4".equals(entry.get(3)));
		assertTrue("5".equals(entry.get(4)));
		assertTrue("".equals(entry.get(5)));
	}

	@Test
	public void empty() {
		String data="1 ,,3,  4, 5";
		CSVEntry entry = new CSVEntry(data);
		assertTrue("1".equals(entry.get(0)));
		assertTrue("".equals(entry.get(1)));
		assertTrue("3".equals(entry.get(2)));
		assertTrue("4".equals(entry.get(3)));
		assertTrue("5".equals(entry.get(4)));
		assertTrue("".equals(entry.get(5)));
	}

	@Test
	public void begin() {
		String data=", ,,3,  4, 5";
		CSVEntry entry = new CSVEntry(data);
		assertTrue("".equals(entry.get(0)));
		assertTrue("".equals(entry.get(1)));
		assertTrue("".equals(entry.get(2)));
		assertTrue("3".equals(entry.get(3)));
		assertTrue("4".equals(entry.get(4)));
		assertTrue("5".equals(entry.get(5)));
		assertTrue("".equals(entry.get(6)));
	}
}
