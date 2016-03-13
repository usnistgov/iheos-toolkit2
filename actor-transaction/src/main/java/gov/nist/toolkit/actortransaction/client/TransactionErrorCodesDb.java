package gov.nist.toolkit.actortransaction.client;

import gov.nist.toolkit.configDatatypes.client.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * All error codes defined by transactions
 */
public class TransactionErrorCodesDb implements Serializable {
    List<ErrorCode> errorCodes = new ArrayList<>();

    public TransactionErrorCodesDb() {}

    public void add(ErrorCode code) { errorCodes.add(code);}

    public TransactionErrorCodesDb(List<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<String> getRefsByTransaction(TransactionType tt, Severity severity) {
        List<String> refs = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (tt.equals(code.getTransaction()) && severity.equals(code.getSeverity()))
                refs.add(code.getCode());
        }

        return refs;
    }

    public TransactionErrorCodesDb getBySeverity(Severity s) {
        List<ErrorCode> codes = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (s.equals(code.getSeverity()))
                codes.add(code);
        }

        return new TransactionErrorCodesDb(codes);
    }

    public TransactionErrorCodesDb getByCode(String c) {
        List<ErrorCode> codes = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (c.equals(code.getCode()))
                codes.add(code);
        }

        return new TransactionErrorCodesDb(codes);
    }

}
