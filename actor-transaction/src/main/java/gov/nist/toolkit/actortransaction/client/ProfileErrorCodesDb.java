package gov.nist.toolkit.actortransaction.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * All error codes defined by profile
 */
public class ProfileErrorCodesDb implements Serializable {
    List<ErrorCode> errorCodes = new ArrayList<>();

    public ProfileErrorCodesDb() {}
    public ProfileErrorCodesDb(List<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public ProfileErrorCodesDb getByTransaction(TransactionType tt) {
        List<ErrorCode> codes = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (tt.equals(code.getTransaction()))
                codes.add(code);
        }

        return new ProfileErrorCodesDb(codes);
    }

    public ProfileErrorCodesDb getBySeverity(Severity s) {
        List<ErrorCode> codes = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (s.equals(code.getSeverity()))
                codes.add(code);
        }

        return new ProfileErrorCodesDb(codes);
    }

    public ProfileErrorCodesDb getByCode(String c) {
        List<ErrorCode> codes = new ArrayList<>();

        for(ErrorCode code : errorCodes) {
            if (c.equals(code.getCode()))
                codes.add(code);
        }

        return new ProfileErrorCodesDb(codes);
    }

}
