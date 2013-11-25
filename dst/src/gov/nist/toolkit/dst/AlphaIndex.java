package gov.nist.toolkit.dst;

public class AlphaIndex {

	static public char charOf(int i) {
		return (char) ('a' + i);
	}
	
	static public int intOf(String s) {
		return intOf(s.charAt(0));
	}
	
	static public int intOf(char c) {
		return (int) (c - 'a');
	}
}
