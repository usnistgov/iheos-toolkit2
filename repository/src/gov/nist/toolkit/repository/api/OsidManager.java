package gov.nist.toolkit.repository.api;

/**
 * OsidManager is the key to binding an application to an OSID implementation.
 * An OSID is required to define an interface that extends OsidManager.  The
 * OSID implementation is required to have a class that implements its
 * OsidManager. The application loads an OSID implementation by using the
 * OsidLoader to get an instance of the OSID's OsidManager.  The application
 * accesses all OSID objects directly or indirectly through the OSID's
 * OsidManager.
 * 
 * <p>
 * OsidManager defines three methods: getOsidContext, assignOsidContext,
 * assignConfiguration. The assign methods are called by the
 * OsidLoader.getManager method. An application can use the assign methods,
 * but this would only be for overriding the default behavior of the
 * OsidLoader.getManager method.
 * </p>
 * 
 * <p>
 * The OsidLoader.getManager method checks its OsidContext argument to make
 * sure it is not null, and then calls the Osidmanager implementation class
 * assignOsidContext method.
 * </p>
 * 
 * <p>
 * The OsidLoader.getManager method loads a properties file that contains the
 * configuration information if one exists. The configuration information is
 * set by the system integrator who has installed an implementation. The
 * configuration properties file is loaded by by the OsidLoader.getManager
 * method using the getResourceAsStream method of the OSID's OsidManager class
 * to search for the configuration properties file. The properties from the
 * loaded configuration file are overlaid with any configuration properties
 * supplied by the application in the call to the OsidLoader.getManager call
 * in the additionalConfiguration argument. The assignConfiguration method is
 * then called.
 * </p>
 * 
 * <p>
 * Typically, the application calls the getOsidContext method only.  It is
 * unusual for the application to override the OsidLoader.getManager and call
 * assignOsidContext or assignConfiguration.
 * </p>
 * 
 * <p>
 * The implementation of OsidManager can use both the OsidContext and the
 * Configuration properties as needed.
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
public interface OsidManager extends java.io.Serializable {
    /**
     * Return context of this OsidManager.
     *
     * @return gov.nist.toolkit.repository.api.OsidContext
     */
    gov.nist.toolkit.repository.api.OsidContext getOsidContext() throws gov.nist.toolkit.repository.api.OsidException;

    /**
     * Assign the context of this OsidManager.
     *
     * @param context
     *
     * @throws gov.nist.toolkit.repository.api.OsidException An exception with one of the following
     *         messages defined in gov.nist.toolkit.repository.api.OsidException:  {@link
     *         gov.nist.toolkit.repository.api.OsidException#NULL_ARGUMENT NULL_ARGUMENT}
     */
    void assignOsidContext(gov.nist.toolkit.repository.api.OsidContext context)
        throws gov.nist.toolkit.repository.api.OsidException;

    /**
     * Assign the configuration of this OsidManager.
     *
     * @param configuration
     *
     * @throws gov.nist.toolkit.repository.api.OsidException An exception with one of the following
     *         messages defined in gov.nist.toolkit.repository.api.OsidException:  {@link
     *         gov.nist.toolkit.repository.api.OsidException#OPERATION_FAILED OPERATION_FAILED},
     *         {@link gov.nist.toolkit.repository.api.OsidException#PERMISSION_DENIED
     *         PERMISSION_DENIED}, {@link
     *         gov.nist.toolkit.repository.api.OsidException#CONFIGURATION_ERROR
     *         CONFIGURATION_ERROR}, {@link
     *         gov.nist.toolkit.repository.api.OsidException#UNIMPLEMENTED UNIMPLEMENTED}, {@link
     *         gov.nist.toolkit.repository.api.OsidException#NULL_ARGUMENT NULL_ARGUMENT}
     */
    void assignConfiguration(java.util.Properties configuration)
        throws gov.nist.toolkit.repository.api.OsidException;

    /**
     * Verify to OsidLoader that it is loading
     * 
     * <p>
     * OSID Version: 2.0
     * </p>
     * .
     */
    void osidVersion_2_0() throws gov.nist.toolkit.repository.api.OsidException;

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
