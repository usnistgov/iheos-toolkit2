package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public enum QuickScanLogAttribute implements Serializable, IsSerializable {
    IS_RUN,
    IS_PASS,
    IS_TLS,
    HL7TIME,
    SITE,
    TEST_DEPENDENCIES,
    CONFTEST_PROPERTIES
};

