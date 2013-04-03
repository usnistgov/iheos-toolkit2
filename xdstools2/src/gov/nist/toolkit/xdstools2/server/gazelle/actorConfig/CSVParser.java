package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;




public class CSVParser {
	String csv;   // raw input for parsing
	String[] lines;  // input parsed into lines
	CSVTable csvTable;  // output of parsing
	IEntryFactory entryFactory;
	
	public CSVParser(File csvFile, CSVTable tab, IEntryFactory entryFactory) throws IOException {
		this(Io.stringFromFile(csvFile), tab, entryFactory);
	}
	
	public CSVParser(String csv, CSVTable tab, IEntryFactory entryFactory) {
		this.csvTable = tab;
		this.entryFactory = entryFactory;
		this.csv = csv;
	}
	
	public void run() { parse(); }
	
	public CSVTable getTable() { return csvTable; }
	
	void parse() {
		mkLines();
		// first line is header - skip
		for (int i=1; i<lines.length; i++) {
			csvTable.entries().add(entryFactory.mkEntry(lines[i]));
		}
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
		if (csv.indexOf(splitOn) == -1) {
			lines = new String[1];
			lines[0] = csv;
		} else {
			lines = csv.split(splitOn);
		}
	}
	
}
