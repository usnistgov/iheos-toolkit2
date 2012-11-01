package gov.nist.toolkit.testengine;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Allocate new uniqueIds.  This is done in several stages. On disk are two files, a base file and and increment file.
 * The base file is initialized once on a system. The increment file is incremented once every time this class
 * is loaded. Each allocation increments a counter kept in memory.  So a uniqueId looks like base.file_increment.counter
 * where base is itself an OID and file_increment and counter are simple integers represented as strings. Some methods
 * are synchronized for use in the WEB version so that multiple threads can use this service.
 * @author bill
 *
 */
public class UniqueIdAllocator extends IdAllocator {
	
	private UniqueIdAllocator(TestConfig config) {
		super(config);
	}
	
	/**
	 * This factory method creates a single instance each time this class is loaded.
	 * That instance is used by all threads.
	 * @param config
	 * @return
	 */
	static public synchronized UniqueIdAllocator getInstance(TestConfig config) {
		UniqueIdAllocator a = null;
		if (a == null)
			a = new UniqueIdAllocator(config);
		return a;
	}

	private String getUniqueIdBase() throws IOException  {
		String base = null;

		try {
			base = Io.stringFromFile(uniqueIdBaseFile).trim();
		 
		} catch (FileNotFoundException e) {
			base = "1.42." + new Hl7Date().now();
			putUniqueIdBase(base);
		}
		if ( ! base.endsWith("."))
			base = base + ".";
		return base;
	}

	private void putUniqueIdBase(String base) throws IOException {
		PrintStream ps = new PrintStream(uniqueIdBaseFile);
		ps.print(base);
		ps.close();
	}

	private String getUniqueIdIndex() throws IOException {
		String id = null;

		try {
			id = Io.stringFromFile(uniqueIdIncrFile).trim();
			if (id == null || id.equals(""))
				id = "305";
		} catch (FileNotFoundException e) {
			id = "1";
			putUniqueIdIndex(id);
		}
		return id;
	}

	private void putUniqueIdIndex(String index) throws IOException {
		PrintStream ps = new PrintStream(uniqueIdIncrFile);
		ps.print(index);
		ps.close();
	}

	private String allocateUniqueId() throws IOException {
		String uniqueid_base = getUniqueIdBase();
		int uniqueid_index = Integer.parseInt(getUniqueIdIndex()) + 1;
		putUniqueIdIndex(String.valueOf(uniqueid_index));
		return uniqueid_base + uniqueid_index;
	}

	private String allocateBasePlusIncrement() throws XdsInternalException {
		try {
			return allocateUniqueId();
		} catch (Exception e) {
			throw new XdsInternalException("Error allocating Unique ID", e);
		}
	}

	int counter = 0;
	String basePlusIncrement = null;
	/**
	 * Allocate a new uniqueID.
	 */
//	public synchronized String allocate() throws XdsInternalException {
//		if (basePlusIncrement == null)
//			basePlusIncrement = allocateBasePlusIncrement();
//		counter++;
//		return basePlusIncrement + "." + String.valueOf(counter);
//	}
	
	UniqueIdAllocator() {
		super(new TestConfig());
	}

	String base;
	int cnt;
	static UniqueIdAllocator uida;
	
	static {
		uida = new UniqueIdAllocator();
		uida.base = "1.42." + new Hl7Date().now();
		uida.cnt = 1;
	}
	
	synchronized String alloc() {
		cnt++;
		return base + "." + String.valueOf(cnt);
	}
	
	public  String allocate() {
		return uida.alloc();
	}
}
