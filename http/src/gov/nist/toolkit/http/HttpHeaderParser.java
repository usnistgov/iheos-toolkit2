package gov.nist.toolkit.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaderParser {
	String line;
	String name;
	String value;
	List<String> unnamedParams = new ArrayList<String>();
	Map<String, String> params = new HashMap<String, String>();
	boolean parsed = false;

	public HttpHeaderParser(String line) {
		this.line = line;
	}

	public String getName() throws ParseException { parse(); return name; }
	public String getValue() throws ParseException { parse(); return value; }
	public List<String> getUnnamedParams() throws ParseException { parse(); return unnamedParams; }
	public Map<String, String> getParams() throws ParseException { parse(); return params; }

	
	String untilGT(HeaderTokenizer tokenizer) throws ParseException {
		StringBuffer value = new StringBuffer();
		
		value.append(tokenizer.currentToken.getValue());
		Token token = tokenizer.getNextToken();
		while(true) {
			value.append(token.getValue());
			if (token.isToken() && token.getToken().equals(HeaderToken.GT))
				break;
			token = tokenizer.getNextToken();
		}
		
		return value.toString();
	}
	
	// will return value or <value> or null if something else found or end of input
	String getValueString(HeaderTokenizer tokenizer) throws ParseException {
		boolean debug = true;
		Token token = tokenizer.getNextToken(debug);
		if (token == null) return null;
		if (token.isToken()) {
			if (token.getToken().equals(HeaderToken.LT)) {  // < found
				return untilGT(tokenizer);
			} else {
				throw new ParseException("Expected < or name - found " + token.getValue() + " in " + tokenizer.getCursorStatus());
			}
		} else {  // input is string
			return token.getValue();
		}
	}

	public void parse() throws ParseException {
		if (parsed)
			return;
		parsed = true;
		HeaderTokenizer tokenizer = new HeaderTokenizer(line);
		Token token; 

		/**
		 * Header name
		 */
		token = tokenizer.getNextToken();    // header name
		if (token == null) {
			return;
		}
		// TODO: validate as name syntax
		if (token.isString())
			name = token.getValue();

		/**
		 * Colon separator
		 */
		token = tokenizer.getNextToken();   // colon
		if (!token.equals(HeaderToken.COLON))
			throw new ParseException("Expected pattern - HeaderName: Value - found " + name + " " + token + " in " + line);

		/**
		 * Header value
		 */
		value = getValueString(tokenizer);

		
		/**
		 * Parse parameters next
		 */
		token = tokenizer.getNextToken();
		while (token != null) {
			/**
			 * ; separator between value and first param or between param n and param n+1
			 */
			if (!(token.isToken() && token.getToken().equals(HeaderToken.SEMICOLON)))
				throw new ParseException("Expected ; - found " + token + " after " + tokenizer.previousToken + " in " + line);

			/**
			 * Get param name
			 */
			token = tokenizer.getNextToken();
			if (token == null)
				break;
			if (token.isToken()) 
				throw new ParseException("Expected Parameter name - found " + token + " in " + line);
			// TODO: needs to be validated as having name syntax
			String paramName = token.getValue();

			/**
			 * If end of line or ; - unnamed param
			 */
			token = tokenizer.getNextToken();
			if (token == null || (token.isToken() && token.getValue().equals(HeaderToken.SEMICOLON))) {
				unnamedParams.add(token.getValue());
				token = tokenizer.getNextToken();
				continue;
			}

			/**
			 * = separator between param name and param value
			 */
			if (!(token.isToken() && token.getToken().equals(HeaderToken.EQUALS)))
				throw new ParseException("Expected name=value - got  " + tokenizer.getPreviousToken() + " " + token + " in " + line);

			token = tokenizer.getNextToken();
			if (token == null) 
				throw new ParseException("Expected = value - got  " + tokenizer.getPreviousToken() + " " + token + " in " + line);
			if (token.isToken())
				throw new ParseException("Expected = value - got  " + tokenizer.getPreviousToken() + " " + token + " in " + line);
			String paramVal = token.getValue();
			params.put(paramName, paramVal);

			token = tokenizer.getNextToken();
		}
	}


}
