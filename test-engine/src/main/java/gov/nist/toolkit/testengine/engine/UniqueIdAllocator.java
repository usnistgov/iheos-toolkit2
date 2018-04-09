package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.common.datatypes.Hl7Date;

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

	static public synchronized UniqueIdAllocator getInstance() {
		return new UniqueIdAllocator();
	}

	private UniqueIdAllocator() {
		super();
	}

	private String base;
	private int cnt;
	private static UniqueIdAllocator uida;
	
	static {
		uida = new UniqueIdAllocator();
		uida.base = "1.2.42." + new Hl7Date().now();
		uida.cnt = 1;
	}
	
	private synchronized String alloc() {
		cnt++;
		return base + "." + String.valueOf(cnt);
	}
	
	public  String allocate() {
		return uida.alloc();
	}
}
