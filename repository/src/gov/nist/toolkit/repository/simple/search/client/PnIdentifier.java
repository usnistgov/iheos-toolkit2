package gov.nist.toolkit.repository.simple.search.client;

/**
 * 
 * This class is used for property name identifier conventions
 * A property name identifier is the one that is indexed in a repository database
 * Conventions are used to avoid conflicts between the storage provider (db) restrictions and asset property names
 * 
 * @author Sunil.Bhaskarla
 *
 */
public class PnIdentifier {

	
	public static final boolean uniquePropertyColumn = false; 	/* Use this to create unique property columns. Quoted identifiers must be used if FALSE to avoid SQL reserved name collisions. */
	
	static public String getQuotedIdentifer(String id) {	
		return "\"" + id + "\"";
	}

	static public String stripQuotes(String id) {
		return id.replaceAll("\"", "");
	}

}
