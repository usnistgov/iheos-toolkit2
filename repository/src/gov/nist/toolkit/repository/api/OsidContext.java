package gov.nist.toolkit.repository.api;

/**
 * <p>
 * OsidContext holds contextual information that is shared by the application
 * and the OSID implementations it uses. The osid package has some design
 * constraints that create the need for OsidContext.  They are:
 * 
 * <ul>
 * <li>
 * OSIDs must work with all frameworks.
 * </li>
 * <li>
 * OSID implementations are independent of each other.
 * </li>
 * </ul>
 * 
 * These design constraints mean that there this no obvious place to put global
 * information.  The OsidContext argument of the OsidLoader.getManager method
 * is intended to provide access to information that is global to an
 * application and the OSID implementations that it loads.  OsidContext can
 * hold and retrieve context. The only requirement is that the information is
 * serializable. There are OsidContext methods to get and assign context.
 * </p>
 * 
 * <p>
 * With few exceptions OSID objects are interfaces and not classes. The use of
 * interfaces in the definition of OSID objects has some important
 * characteristics:
 * 
 * <ul>
 * <li>
 * There is no OSID framework for storing contextual (global) information.
 * </li>
 * <li>
 * The OSID implementation developer can define OSID objects by implementing
 * the OSID interface and extending a framework class.
 * </li>
 * <li>
 * Contextual (global) information can only be communicated when the
 * application loads an implementation.
 * </li>
 * </ul>
 * 
 * These characteristics of OSIDs and the need to provide sharable contextual
 * (global) information led to the definition of OsidContext. An application
 * is responsible for supplying a valid OsidContext instance when it loads an
 * OSID implementation using the OsidLoader.getManager method. This approach
 * provides all the benefits and limitations of any system of global data.
 * </p>
 * 
 * <p>
 * OsidContext uses an unambiguous String as a key to assign the serializable
 * context information. To retrieve the contextual information from the
 * OsidContext the getContext method is called with the key.
 * </p>
 * 
 * <p>
 * OSID Version: 2.0
 * </p>
 * 
 * <p>
 * Licensed under the {@link gov.nist.toolkit.repository.api.SidLicense MIT
 * O.K.I&#46; OSID Definition License}.
 * </p>
 */
public class OsidContext implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1753251509301748730L;
	private final java.util.Hashtable contextInfo = new java.util.Hashtable();

    /**
     * Assign the context of the OsidContext. Context is associated with an
     * unambiguous key, for example the context's fully qualified class name.
     * There is only one context asscociated with a particular key.  If a
     * context already exists for this key, that context is overwritten.
     *
     * @param key key is an unambiguous String identifier associated with a
     *        particular context. Any application or implementation knowing
     *        the key can assign or get the context.  The key may not be null.
     * @param context context is any serializable information that either an
     *        application or an implementation needs to retrieve or share. For
     *        context to be sharable both parties must know the key.  Setting
     *        a null context removes the context.
     *
     * @throws gov.nist.toolkit.repository.api.OsidException An exception with one of the following
     *         messages defined in gov.nist.toolkit.repository.api.OsidException:  {@link
     *         gov.nist.toolkit.repository.api.OsidException#NULL_ARGUMENT NULL_ARGUMENT}
     */
    public final void assignContext(String key, java.io.Serializable context)
        throws gov.nist.toolkit.repository.api.RepositoryException {
        if ((null != key) && (null != context)) {
            contextInfo.put(key, context);
        } else if ((null != key) && (null == context)) {
            if (contextInfo.get(key).equals(context)) {
                contextInfo.remove(key);
            }
        } else {
            throw new gov.nist.toolkit.repository.api.RepositoryException(gov.nist.toolkit.repository.api.RepositoryException.NULL_ARGUMENT);
        }
    }

    /**
     * Get the context associated with this key.  If the key is unknown, null
     * is returned.
     *
     * @param key key is an unambiguous String identifier associated with a
     *        particular context. Any application or implementation knowing
     *        the key can assign or get the context.  The key may not be null.
     *
     * @return java.io.Serializable context
     *
     * @throws gov.nist.toolkit.repository.api.OsidException An exception with one of the following
     *         messages defined in gov.nist.toolkit.repository.api.OsidException:  {@link
     *         gov.nist.toolkit.repository.api.OsidException#NULL_ARGUMENT NULL_ARGUMENT}
     */
    public final java.io.Serializable getContext(String key)
        throws gov.nist.toolkit.repository.api.RepositoryException {
        if (null != key) {
            if (contextInfo.containsKey(key)) {
                return (java.io.Serializable) contextInfo.get(key);
            } else {
                return null;
            }
        }

        throw new gov.nist.toolkit.repository.api.RepositoryException(gov.nist.toolkit.repository.api.RepositoryException.NULL_ARGUMENT);
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
