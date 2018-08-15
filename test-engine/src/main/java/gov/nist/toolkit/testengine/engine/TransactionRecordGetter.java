package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.server.SimDbEvent;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.List;

public interface TransactionRecordGetter<T> {

    List<T> getAll() throws XdsInternalException;

    SimDbEvent getSimDbEvent();

    TransactionType getTransactionType();

    String getUrl();
    String getPlaceToken();
}
