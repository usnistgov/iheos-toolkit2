package gov.nist.toolkit.saml.builder;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe, idempotent OpenSAML 5 bootstrap.
 * Call ensureInitialized() once before any OpenSAML operation.
 * The class uses double-checked locking so subsequent calls are free.
 * Why this is needed
 * OpenSAML 5 moves away from the old {@code DefaultBootstrap.bootstrap()} pattern.
 * Initialization is now driven by {@link InitializationService#initialize()}, which
 * uses {@link java.util.ServiceLoader} to discover {@code InitializerFunction}
 * implementations shipped inside the {@code *-impl} JARs
 * ({@code opensaml-core-impl}, {@code opensaml-saml-impl},
 * {@code opensaml-xmlsec-impl}, …).
 * Until that call is made the {@link XMLObjectProviderRegistrySupport} registry
 * is empty, every factory returns {@code null}, and you will see:
 *  ServiceLoader discovery: Not finding providers
 *  Registry from support: Returns null
 *  Factory instances: All NULL (BuilderFactory, MarshallerFactory, UnmarshallerFactory)
 * Required Maven dependencies
 * Make sure ALL of the following {@code *-impl} artifacts are on the runtime
 * classpath – they carry the {@code META-INF/services/} files that ServiceLoader
 * needs:
 * {@code
 * <dependency>
 *     <groupId>org.opensaml</groupId>
 *     <artifactId>opensaml-core-impl</artifactId>
 *     <version>5.1.4</version>
 * </dependency>
 * <dependency>
 *     <groupId>org.opensaml</groupId>
 *     <artifactId>opensaml-saml-impl</artifactId>
 *     <version>5.1.4</version>
 * </dependency>
 * <dependency>
 *     <groupId>org.opensaml</groupId>
 *     <artifactId>opensaml-xmlsec-impl</artifactId>
 *     <version>5.1.4</version>
 * </dependency>
 * <dependency>
 *     <groupId>org.opensaml</groupId>
 *     <artifactId>opensaml-security-impl</artifactId>
 *     <version>5.1.4</version>
 * </dependency>
 * }
 * Java 17 module-path note
 * If you run on the module-path (not the class-path), ServiceLoader may still
 * fail to discover providers unless each impl module {@code provides} the SPI
 * in its {@code module-info.java}. The safest option for a Jakarta EE / OSGi-free
 * project is to keep OpenSAML on the class-path.
 */
public final class OpenSAMLInitializer {

    private static final Logger log = LoggerFactory.getLogger(OpenSAMLInitializer.class);

    /**
     * Tracks initialization state. {@code volatile} ensures visibility across threads
     * without requiring a synchronized read on the fast path.
     */
    private static volatile boolean initialized = false;

    /** Mutex for the one-time initialization block. */
    private static final Object LOCK = new Object();

    private OpenSAMLInitializer() { /* utility class */ }

    /**
     * Initialize OpenSAML 5 exactly once, no matter how many threads call this.
     *
     * @throws InitializationException if the OpenSAML bootstrap fails
     */
    public static void ensureInitialized() throws InitializationException {
        // Fast path — already done, no lock needed.
        if (initialized) {
            return;
        }

        synchronized (LOCK) {
            // Re-check inside the lock in case another thread just finished.
            if (initialized) {
                return;
            }

            log.info("Bootstrapping OpenSAML 5 via InitializationService …");
            InitializationService.initialize();

            // Verify the registry is actually populated after init.
            validateRegistry();

            initialized = true;
            log.info("OpenSAML 5 initialized successfully.");
        }
    }

    /**
     * Convenience wrapper that rethrows as {@link ExceptionInInitializerError}
     * so it can be used safely inside a {@code static} block.
     */
    public static void ensureInitializedUnchecked() {
        try {
            ensureInitialized();
        } catch (InitializationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Emit a clear diagnostic log after init rather than letting callers
     * discover null factories at an unpredictable point later.
     */
    private static void validateRegistry() {
        boolean builderOk     = XMLObjectProviderRegistrySupport.getBuilderFactory()     != null;
        boolean marshallerOk  = XMLObjectProviderRegistrySupport.getMarshallerFactory()  != null;
        boolean unmarshallerOk= XMLObjectProviderRegistrySupport.getUnmarshallerFactory()!= null;

        if (builderOk && marshallerOk && unmarshallerOk) {
            log.debug("Registry validation OK — all three factories are non-null.");
        } else {
            log.error(
                "OpenSAML registry validation FAILED after init! "
                + "BuilderFactory={} MarshallerFactory={} UnmarshallerFactory={}. "
                + "Check that opensaml-core-impl, opensaml-saml-impl, "
                + "opensaml-xmlsec-impl and opensaml-security-impl are ALL "
                + "on the runtime class-path, not just the compile scope.",
                builderOk, marshallerOk, unmarshallerOk
            );
            // Throw here so the failure is surfaced at startup, not at first use.
            throw new IllegalStateException(
                "OpenSAML 5 registry is incomplete after InitializationService.initialize(). "
                + "Ensure all *-impl JARs are present on the class-path and their "
                + "META-INF/services/ entries are not excluded by a shade/uber-jar plugin."
            );
        }
    }
}
