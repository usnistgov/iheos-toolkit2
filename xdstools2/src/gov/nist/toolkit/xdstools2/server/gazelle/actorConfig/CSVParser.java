package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;




public class CSVParser {
	String csv;
	String[] lines;
	CSVTable csvTable;
	IEntryFactory entryFactory;

	public CSVParser(File csvFile, IEntryFactory entryFactory) throws IOException {
		this(Io.stringFromFile(csvFile), entryFactory);
	}
	
	private CSVParser(String csv, IEntryFactory entryFactory) {
		this.entryFactory = entryFactory;
		this.csv = csv;
	}
	
	public CSVTable getTable() { return csvTable; }
	
	public CSVTable parse(CSVTable tab) {
		this.csvTable = tab;
		mkLines();
		// first line is header - skip
		for (int i=1; i<lines.length; i++) {
			csvTable.entries().add(entryFactory.mkEntry(lines[i]));
		}
		return csvTable;
	}
	
	public CSVEntry get(int i) {
		return csvTable.entries().get(i);
	}
	
	public int size() {
		return csvTable.size();
	}
	
	void mkLines() {
		String splitOn = "\n";
		if (csv.indexOf("\r") > 0)
			splitOn = "\r";
		lines = csv.split(splitOn);
	}
	
}
