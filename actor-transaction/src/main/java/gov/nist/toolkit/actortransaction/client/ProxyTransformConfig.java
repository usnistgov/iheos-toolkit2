package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.configDatatypes.client.FhirVerb;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.io.Serializable;

public class ProxyTransformConfig implements IsSerializable, Serializable {
    private TransactionType transactionType;
    private TransactionDirection transactionDirection;
    private String transformClassName;
    private FhirVerb fhirVerb;

    public ProxyTransformConfig(TransactionType transactionType, TransactionDirection transactionDirection, FhirVerb fhirVerb, String transformClassName) {
        this.transactionType = transactionType;
        this.transactionDirection = transactionDirection;
        this.fhirVerb = fhirVerb;
        this.transformClassName = transformClassName;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public FhirVerb getFhirVerb() {
        return fhirVerb;
    }

    public TransactionDirection getTransactionDirection() {
        return transactionDirection;
    }

    public String getTransformClassName() {
        return transformClassName;
    }

    public static ProxyTransformConfig parse(String encoded) throws Exception {
        String[] parts = encoded.split("\\^", 4);
        if (parts.length != 4)
            throw new Exception("ProxyTransformConfig: bad configuration: " + encoded);
        TransactionType ttype = TransactionType.find(parts[0]);
        TransactionDirection dir;
        if (parts[1].equals(TransactionDirection.REQUEST.name()))
            dir = TransactionDirection.REQUEST;
        else if (parts[1].equals(TransactionDirection.RESPONSE.name()))
            dir = TransactionDirection.RESPONSE;
        else
            throw new Exception("ProxyTransformConfig: bad TransactionDirection: " + parts[1]);
        return new ProxyTransformConfig(ttype, dir, FhirVerb.valueOf(parts[2]), parts[3]);
    }

    public String toString() {
        return transactionType.getShortName() + "^" + transactionDirection.name() + "^" + fhirVerb.name() + "^" + transformClassName;
    }
}
