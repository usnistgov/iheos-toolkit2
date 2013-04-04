package gov.nist.toolkit.repository.api;

/**
 * The Type class captures the fundamental concept of categorizing an object.
 * Type are designed to be shared among various OSIDs and Managers.  The exact
 * meaning of a particular type is left to the developers who use a given Type
 * subclass.  The form of the Type class enables categorization. There are
 * four Strings that make up the Type class: authority, domain, keyword, and
 * description.  The first three of these Strings are used by the isEqual
 * method to determine if two instance of the Type class are equal.  The
 * fourth String, description, is used to clarify the semantic meaning of the
 * instance.
 * 
 * <p>
 * An example of a FunctionType instance:
 * </p>
 * 
 * <p>
 * <br/>  - authority is "higher ed"<br/
 * >  - domain is "authorization"<br/
 * >  - keyword is "writing checks"<br/
 * >  - description is "This is the FunctionType for writing checks"
 * </p>
 * 
 * <p>
 * This Type could be used with the authorization OSID.  It could also be used
 * with the dictionary OSID to determine the text to display for a given
 * locale (for example, CANADA_FRENCH).  The dictionary OSID could use the
 * FunctionType instance as a key to find the display text, but it could also
 * use just the keyword string from the FunctionType as a key.  By using the
 * keyword the same display text could then be used for other FunctionTypes
 * such as: <br/
 * >  - authority is "mit"<br/
 * >  - domain is "accounting"<br/
 * >  - keyword is "writing checks"<br/
 * >  - description is "A/P check writing type"<br/
 * >An instance of the Type class can be used in a variety of ways to
 * categorize information either as a complete object or as one of its parts
 * (ie authority, domain, keyword).
 * </p>
 * 
 * <p>
 * OSID Version: 2.0
 * </p>
 * 
 * <p>
 * Licensed under the {@link org.osid.SidLicense MIT
 * O.K.I&#46; OSID Definition License}.
 * </p>
 */
public abstract class Type implements java.io.Serializable {
    private String domain;
    private String authority;
    private String keyword;
    private String description;

    public Type(String authority, String domain, String keyword) {
        this.domain = domain;
        this.authority = authority;
        this.keyword = keyword;
        this.description = "";
    }

    public Type(String authority, String domain, String keyword,
        String description) {
        this.domain = domain;
        this.authority = authority;
        this.keyword = keyword;
        this.description = description;
    }

    public final boolean isEqual(Type type2) {
        if ((null != type2) && (null != type2.getDomain()) &&
                (null != type2.getAuthority()) && (null != type2.getKeyword()) &&
                (null != this.getDomain()) && (null != this.getAuthority()) &&
                (null != this.getKeyword())) {
            return (this.getDomain().equals(type2.getDomain()) &&
            this.getAuthority().equals(type2.getAuthority()) &&
            this.getKeyword().equals(type2.getKeyword()));
        }

        return false;
    }

    public final String getAuthority() {
        return this.authority;
    }

    public final String getDomain() {
        return this.domain;
    }

    public final String getKeyword() {
        return this.keyword;
    }

    public final String getDescription() {
        return this.description;
    }

    /**
     * <p>
     * MIT O.K.I&#46; SID Definition License.
     * </p>
     * 
     * <p>
     * <b>Copyright and license statement:</b>
     * </p>
     * 
     * <p>
     * Copyright &copy; 2003 Massachusetts Institute of     Technology &lt;or
     * copyright holder&gt;
     * </p>
     * 
     * <p>
     * This work is being provided by the copyright holder(s)     subject to
     * the terms of the O.K.I&#46; SID Definition     License. By obtaining,
     * using and/or copying this Work,     you agree that you have read,
     * understand, and will comply     with the O.K.I&#46; SID Definition
     * License.
     * </p>
     * 
     * <p>
     * THE WORK IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY     KIND, EXPRESS
     * OR IMPLIED, INCLUDING BUT NOT LIMITED TO     THE WARRANTIES OF
     * MERCHANTABILITY, FITNESS FOR A     PARTICULAR PURPOSE AND
     * NONINFRINGEMENT. IN NO EVENT SHALL     MASSACHUSETTS INSTITUTE OF
     * TECHNOLOGY, THE AUTHORS, OR     COPYRIGHT HOLDERS BE LIABLE FOR ANY
     * CLAIM, DAMAGES OR     OTHER LIABILITY, WHETHER IN AN ACTION OF
     * CONTRACT, TORT     OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
     * WITH     THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     * </p>
     * 
     * <p>
     * <b>O.K.I&#46; SID Definition License</b>
     * </p>
     * 
     * <p>
     * This work (the &ldquo;Work&rdquo;), including any     software,
     * documents, or other items related to O.K.I&#46;     SID definitions, is
     * being provided by the copyright     holder(s) subject to the terms of
     * the O.K.I&#46; SID     Definition License. By obtaining, using and/or
     * copying     this Work, you agree that you have read, understand, and
     * will comply with the following terms and conditions of     the
     * O.K.I&#46; SID Definition License:
     * </p>
     * 
     * <p>
     * You may use, copy, and distribute unmodified versions of     this Work
     * for any purpose, without fee or royalty,     provided that you include
     * the following on ALL copies of     the Work that you make or
     * distribute:
     * </p>
     * 
     * <ul>
     * <li>
     * The full text of the O.K.I&#46; SID Definition License in a location
     * viewable to users of the redistributed Work.
     * </li>
     * </ul>
     * 
     * 
     * <ul>
     * <li>
     * Any pre-existing intellectual property disclaimers, notices, or terms
     * and conditions. If none exist, a short notice similar to the following
     * should be used within the body of any redistributed Work:
     * &ldquo;Copyright &copy; 2003 Massachusetts Institute of Technology. All
     * Rights Reserved.&rdquo;
     * </li>
     * </ul>
     * 
     * <p>
     * You may modify or create Derivatives of this Work only     for your
     * internal purposes. You shall not distribute or     transfer any such
     * Derivative of this Work to any location     or any other third party.
     * For purposes of this license,     &ldquo;Derivative&rdquo; shall mean
     * any derivative of the     Work as defined in the United States
     * Copyright Act of     1976, such as a translation or modification.
     * </p>
     * 
     * <p>
     * THE WORK PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,     EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE     WARRANTIES OF
     * MERCHANTABILITY, FITNESS FOR A PARTICULAR     PURPOSE AND
     * NONINFRINGEMENT. IN NO EVENT SHALL     MASSACHUSETTS INSTITUTE OF
     * TECHNOLOGY, THE AUTHORS, OR     COPYRIGHT HOLDERS BE LIABLE FOR ANY
     * CLAIM, DAMAGES OR     OTHER LIABILITY, WHETHER IN AN ACTION OF
     * CONTRACT, TORT     OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
     * WITH     THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
     * </p>
     * 
     * <p>
     * The name and trademarks of copyright holder(s) and/or     O.K.I&#46; may
     * NOT be used in advertising or publicity     pertaining to the Work
     * without specific, written prior     permission. Title to copyright in
     * the Work and any     associated documentation will at all times remain
     * with     the copyright holders.
     * </p>
     * 
     * <p>
     * The export of software employing encryption technology     may require a
     * specific license from the United States     Government. It is the
     * responsibility of any person or     organization contemplating export
     * to obtain such a     license before exporting this Work.
     * </p>
     */
}
