package gov.nist.toolkit.repository.api;

/**
 * OsidException or one of its subclasses is thrown by all methods of all
 * interfaces of an Open Service Interface Definition (OSID). This requires
 * the caller of an OSID package method handle the OsidException. Since the
 * application using an OSID can not determine where an implementation method
 * will ultimately execute, it must assume a worst case scenerio and protect
 * itself. OSID Implementations should throw their own subclasses of
 * OsidException and limit exception messages to those predefined by their own
 * OsidException or its superclasses. This approach to exception messages
 * allows Applications and OSID implementations using an OsidException's
 * predefined messages to handle exceptions in an interoperable way.
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
public class RepositoryException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3287075237460316637L;

	/** Operation failed */
    public static final String OPERATION_FAILED = "Operation failed ";

    /** Null argument */
    public static final String NULL_ARGUMENT = "Null argument";

    /** IO Error */
    public static final String IO_ERROR = "IO Error";
    
    /** Index Error */
    public static final String INDEX_ERROR = "Index Error";
    
    /** Search Error */
    public static final String MALFORMED_SEARCHCRITERIA = "Malformed search criteria";

    /** Unimplemented method */
    public static final String UNIMPLEMENTED = "Unimplemented method ";

    /** OSID Version mismatch */
    public static final String VERSION_ERROR = "OSID Version mismatch error ";

    /** Transaction already marked */
    public static final String ALREADY_MARKED = "Transaction already marked ";

    /** No transaction marked */
    public static final String NOTHING_MARKED = "No transaction marked ";

    /** Interface not found */
    public static final String INTERFACE_NOT_FOUND = "Interface not found ";

    /** Manager not OSID implementation */
    public static final String MANAGER_NOT_OSID_IMPLEMENTATION = "Manager not OSID implementation ";

    /** Manager instantiation error */
    public static final String MANAGER_INSTANTIATION_ERROR = "Manager instantiation error ";

    /** Error assigning context */
    public static final String ERROR_ASSIGNING_CONTEXT = "Error assigning context ";

    /** Error assigning configuration */
    public static final String ERROR_ASSIGNING_CONFIGURATION = "Error assigning configuration ";

    /** Permission denied */
    public static final String PERMISSION_DENIED = "Permission denied";

    /** Configuration error */
    public static final String CONFIGURATION_ERROR = "Configuration error";

    /** Unknown Id */
    public static final String UNKNOWN_ID = "Unknown Id ";

    /** Unknown or unsupported Type */
    public static final String UNKNOWN_TYPE = "Unknown Type ";

    /** Iterator has no more elements */
    public static final String NO_MORE_ITERATOR_ELEMENTS = "Iterator has no more elements ";

    /** Object already added */
    public static final String ALREADY_ADDED = "Object already added ";

    /** Circular operation */
    public static final String CIRCULAR_OPERATION = "Circular operation not allowed ";

    /** Unknown key */
    public static final String UNKNOWN_KEY = "Unknown key ";

    /** Unknown Repository */
    public static final String UNKNOWN_REPOSITORY = "Unknown Repository ";

    /** Manager not found */
    public static final String MANAGER_NOT_FOUND = "Manager not found ";

    /** No object has this date */
    public static final String NO_OBJECT_WITH_THIS_DATE = "No object has this date ";

    /** Cannot copy or inherit RecordStructure from itself */
    public static final String CANNOT_COPY_OR_INHERIT_SELF = "Cannot copy or inherit RecordStructure from itself ";

    /** Already inheriting this RecordStructure */
    public static final String ALREADY_INHERITING_STRUCTURE = "Already inheriting this RecordStructure ";

    /** Effective date must precede expiration date */
    public static final String EFFECTIVE_PRECEDE_EXPIRATION = "Effective date must precede expiration date ";

    /** Repository already exists */
    public static final String REPOSITORY_ALREADY_EXISTS = "Repository cannot be created, it already exists";
    public RepositoryException(String message) {
        super(message);
    }
    
    public RepositoryException(String message, Exception e) {
    	super(message, e);
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
