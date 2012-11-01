package gov.nist.toolkit.simulators.sim.reg.store;


public class PatternMatchTest {

	DocEntryCollection c = new DocEntryCollection();
	String pattern;
	String ret;
	int startAt;

	String value;
	boolean good;
	
	void run2() throws Exception {
		value = "Auth";
		pattern = "%th";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Smi";
		pattern = "S%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Smi";
		pattern = "S%i";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Smbi";
		pattern = "S%i";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Author Jack Smith";
		pattern = "Author%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Author Jack Smith";
		pattern = "%Jack%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Author Jack Smith";
		pattern = "Author Jack%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Author Jack Smith";
		pattern = "%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = " th";
		pattern = "%th%";
		good = c.matchAuthor(value, pattern);
		expect(true);
		
		value = "Author Jack Smith";
		pattern = "%Jane%";
		good = c.matchAuthor(value, pattern);
		expect(false);
		
		
	}
	
	void run() throws Exception {

		pattern = "%And%";
		startAt = 0;
		ret = c.getAfterText(pattern, startAt);
		expect("And");

		pattern = "%And";
		startAt = 0;
		ret = c.getAfterText(pattern, startAt);
		expect("And");
		
		pattern = "%And%Or";
		startAt = 0;
		ret = c.getAfterText(pattern, startAt);
		expect("And");
		
		pattern = "%And%Or";
		startAt = 4;
		ret = c.getAfterText(pattern, startAt);
		expect("Or");
		
	}
	
	void expect(String expect) throws Exception {
		System.out.println("expect = " + expect);
		if (!expect.equals(ret))
			throw new Exception("Expected [" + expect + "] got [" + ret + "]");
	}
	

	void expect(boolean val) throws Exception {
		if (good != val)
			throw new Exception("fail");
	}
	
	static public void main(String[] args) {
		try {
//			new PatternMatchTest().run();
			new PatternMatchTest().run2();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
