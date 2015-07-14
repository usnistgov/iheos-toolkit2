package gov.nist.toolkit.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.emory.mathcs.backport.java.util.Arrays;

public class HeaderTokenizer {
	String inputString;
	List<Token> tokens = null;
	int currentTokenIndex = -1;
	int cursor = 0;
	Token currentToken = null;
	Token previousToken = null;
	static final Logger logger = Logger.getLogger(HeaderTokenizer.class);
	boolean debug = false;
	
	public HeaderTokenizer(String inputString) {
		this.inputString = inputString.trim();
	}

	// Return next token.  Do not return comments
	// if ( (start of comment) then ignore everything until end of comment
	// comments may be nested
	public Token getNextToken() throws ParseException {
		if (tokens == null) {
			tokens = new ArrayList<Token>();
			tokenize();
		}
		previousToken = currentToken;
		currentToken = getNextNonCommentToken();
		return currentToken;
	}
	
	public Token getNextToken(boolean debug) throws ParseException {
		Token t = getNextToken();
		if (debug) {
			if (t.isToken()) System.out.println("T(" + t.getValue() + ")");
			else System.out.println("S(" + t.getValue() + ")");
		}
		return t;
	}
	
	public Token getPreviousToken() { return previousToken; }
	
	// if ( (start of comment) then ignore everything until end of comment
	// comments may be nested
	Token getNextNonCommentToken() {
		Token t = nextToken();
		if (t == null) return t;
		if (t.isToken() && t.equals(HeaderToken.OPEN_PAREN)) {
			readToCommentClose();
			t = nextToken();
		}
		return t;
	}
	
	// leaves pointer at comment close
	void readToCommentClose() {
		Token t = nextToken();
		
		if (t == null) return;
		if (t.isToken() && t.equals(HeaderToken.OPEN_PAREN)) {
			readToCommentClose();
			nextToken();  // pass close
		}
		while (t != null && !(t.isToken() && t.equals(HeaderToken.CLOSE_PAREN))) {
			t = nextToken();
			if (t == null) return;
			if (t.isToken() && t.equals(HeaderToken.OPEN_PAREN)) {
				readToCommentClose();
				nextToken(); // pass close
			}
		}
	}
	
	public List<Token> getTokens() throws ParseException {
		if (tokens == null) {
			tokens = new ArrayList<Token>();
			tokenize();
		}
		return tokens;
	}
	
	Token nextToken() {
		currentTokenIndex++;
		return currentToken();
	}
	
	Token currentToken() {
		if (currentTokenIndex < tokens.size()) return tokens.get(currentTokenIndex);
		return null;
	}
	
	public String getCursorStatus() {
		return "Cursor at item " + currentTokenIndex + " in " + tokens;
	}
	
	void tokenize() throws ParseException {
		while(ok()) {
			if ('"' == current()) { 
				addToken(parseQuotedString()); 
				continue; 
			}
			if ( '"' != current() && '=' == previous()) {
				addToken(parseUnquotedValue());
				continue;
			}
			if (isSpecial(current())) { 
				addToken(current()); 
				next(); 
				continue; 
			}
			if (isLWSP(current())) { 
				next(); 
				continue; 
			}
			addToken(parseString());
		}
		
	}
	
	void addToken(Token token) {
		if (token.isString()) {
			logger.debug("Add S(" + token + ") ");
			if (debug) System.out.println("Add S(" + token + ") ");
		}
		else {
			logger.debug("Add T(" + token + ") ");
			if (debug) System.out.println("Add T(" + token + ") ");
		}
		tokens.add(token);
	}
	
	void addToken(String string) {
		if(HeaderToken.isSpecial(string)) {
			Token token = new Token(HeaderToken.toToken(string)); 
			if (debug) System.out.println("Add T(" + token + ") ");
			tokens.add(token);
		}
		else {
			Token token = new Token(string); 
			if (debug) System.out.println("Add S(" + token + ") ");
			tokens.add(token);
		}
	}
	
	void addToken(char c) {
		addToken(Character.toString(c));
	}
	
	// start references open double-quote
	String parseQuotedString() throws ParseException {
		next();  // past open double-quote
		int start = cursor;
		while(ok()) {
			if (current() == '"' && previous() != '\\') {
				String val = inputString.substring(start, cursor);
				next();  // past close double-quote
				skipWhiteSpace();
				return val;
			}
			next();
		}
		throw new ParseException("Invalid quoted value - no terminating double-quote found");
	}
	
	Character[] xseparators = { '(', ')', '<', '>', '@' , ',', ';', ':', '\\', '"', '/', '[',
			']', '?', '=', '{', '}'};
	@SuppressWarnings("unchecked")
	List<Character> separators = Arrays.asList(xseparators);
	
	String parseUnquotedValue() {
		StringBuffer value = new StringBuffer();
		while(ok()) {
			if (separators.contains(current())) {
				return value.toString();
			} else {
				value.append(current());
			}
			next();
		}
		return value.toString();
	}
	
	// start is beginning of string
	String parseString() {
		int start = cursor;
		next();
		while(ok()) {
			if (isLWSP(current()) || isSpecial(current())) return inputString.substring(start,  cursor);
			next();
		}
		String value = inputString.substring(start,  cursor);   // end of string
		return value;
	}
	
	char current() { 
		if (ok()) 
			return inputString.charAt(cursor);
		return '\0'; 
	}
	
	char previous() {
		if ((cursor - 1) < 0)
			return '\0';
		return inputString.charAt(cursor - 1); 
	}
	
	void next() { 
		cursor++;
	}
	
	void skipWhiteSpace() {
		while(ok() && isLWSP(current()))
			next();
	}
		
	String lwsp = " \t ";
	boolean isLWSP(char a) { return lwsp.indexOf(a) > -1; }
	
	boolean isSpecial(char a) { return HeaderToken.isSpecial(a); }
	
	boolean ok() {
		if (cursor >= inputString.length())
			return false;
		return true;
	}
	
}
