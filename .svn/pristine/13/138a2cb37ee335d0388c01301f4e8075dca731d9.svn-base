package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;




public class CSVParser {
	String csv;
	String[] lines;
	CSVTable csvTable;
	IEntryFactory entryFactory;
//	ArrayList<CSVEntry> entries = new ArrayList<CSVEntry>();

	
//	public CSVParser(String csv) {
//		this.csv = csv;
//		parse();
//	}
	
	public CSVParser(File csvFile, CSVTable tab, IEntryFactory entryFactory) throws IOException {
		this(Io.stringFromFile(csvFile), tab, entryFactory);
//		this.csvTable = tab;
//		this.entryFactory = entryFactory;
//		csv = Io.stringFromFile(csvFile);
//		parse();
	}
	
	public CSVParser(String csv, CSVTable tab, IEntryFactory entryFactory) {
		this.csvTable = tab;
		this.entryFactory = entryFactory;
		this.csv = csv;
		parse();
	}
	
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
		lines = csv.split(splitOn);
	}
	
	
//	public static void main(String[] args) {
//		GazelleConfigs gConfigs = null; 
//		
//		try {
//			gConfigs = (GazelleConfigs) new CSVParser(new File("/Users/bill/tmp/toolkit/actors/all.csv")).getTable();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(-1);
//		}
//		
////		CSVEntry entry = parser.get(0);
////		
////		String system = entry.get(GazelleConfigSystem);
////		String type = entry.get(GazelleConfigDetail);
////		
////		System.out.println(system + " => " + type);
//		
//		OidConfigs oConfigs; 
//
//		try {
//			oConfigs = (OidConfigs) new CSVParser(new File("/Users/bill/tmp/toolkit/actors/oidSummary.csv")).getTable();
//			
//			new ConfigToXml(gConfigs, oConfigs, new File("/Users/bill/tmp/toolkit/actors")).run();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
}
