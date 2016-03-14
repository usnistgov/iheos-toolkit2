package gov.nist.toolkit.xdstools2.server.gazelle.actorConfig;

public class CSVParser {
//	String csv;

	CSVTable csvTable;
	IEntryFactory entryFactory;

//	public CSVParser(File csvFile, IEntryFactory entryFactory) throws IOException {
//		this(Io.stringFromFile(csvFile), entryFactory);
//	}
	
	public CSVParser(IEntryFactory entryFactory) {
		this.entryFactory = entryFactory;
	}

//    public CSVParser(String csv, IEntryFactory entryFactory) {
//        this.entryFactory = entryFactory;
//        this.csv = csv;
//    }

    public CSVTable getTable() { return csvTable; }

    // update CSVTable with content of csv string
	public CSVTable parse(CSVTable tab, String csv) {
		this.csvTable = tab;
        String[] lines = mkLines(csv);
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
	
	String[] mkLines(String csv) {
		String splitOn = "\n";
		if (csv.indexOf("\r") > 0)
			splitOn = "\r";
		return csv.split(splitOn);
	}
	
}
