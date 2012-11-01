package gov.nist.toolkit.testengine.test;

import gov.nist.toolkit.testengine.StringSub;
import junit.framework.TestCase;

public class StringSubTest extends TestCase {

	public void test_rplc_1() {
		StringSub ss = new StringSub("xxxabcyyy");
		
		ss.addSub("abc", "mno");
		
		String result = ss.toString();
		
		assertTrue(result.equals("xxxmnoyyy"));
	}

	public void test_rplc_2() {
		StringSub ss = new StringSub("xxxjjabcjjyyy");
		
		ss.addSub("jj", "zz");
		
		String result = ss.toString();
		
		assertTrue(result.equals("xxxzzabczzyyy"));
	}

	public void test_rplc_3() {
		StringSub ss = new StringSub("xxxjjabcjjyyy");
		
		ss.addSub("jj", "zz");
		
		String result = ss.toString();

		assertFalse(result.equals("xxxzzabczzyyy1"));
	}

	public void test_rplc_4() {
		StringSub ss = new StringSub("xxxjjabcjjyyy");
		
		ss.addSub("foo", "zz");
		
		String result = ss.toString();

		assertTrue(result.equals("xxxjjabcjjyyy"));
	}

	public void test_rplc_5() {
		StringSub ss = new StringSub("xxxjjabcjjyyy");
				
		String result = ss.toString();

		assertTrue(result.equals("xxxjjabcjjyyy"));
	}
}
