package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Created by bill on 9/10/15.
 */
public enum ParamType implements IsSerializable, Serializable {
    OID,
    ENDPOINT,
    TEXT,
    BOOLEAN,
    TIME,
    SELECTION;

	ParamType() {
	} // for GWT
}
