package gov.nist.toolkit.valsupport;

import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.valsupport.client.MessageDirection;
import gov.nist.toolkit.valsupport.client.MessageTransaction;
import gov.nist.toolkit.valsupport.client.ValidationContext;


/**
 * Created by bill on 7/24/15.
 */
public class ValidationContextFactory {

    public static ValidationContext get(String wsaction) {
        ValidationContext vc = new ValidationContext();
        setValidationContextFromWSAction(vc, wsaction);
        return vc;
    }

    public static ValidationContext get(MessageTransaction trans, MessageDirection dir) {
        ValidationContext vc = new ValidationContext();

        if (trans == null || dir == null) return vc;

        vc.setDirection(dir);
        if      (MessageTransaction.R.equals(trans))            vc.isR   = true;
        else if (MessageTransaction.PNR.equals(trans))          vc.isPnR = true;
        else if (MessageTransaction.RET.equals(trans))          vc.isRet = true;
        else if (MessageTransaction.XDR.equals(trans)) {        vc.isPnR = true; vc.isXDR        = true; }
        else if (MessageTransaction.XDRLimited.equals(trans)) { vc.isPnR = true; vc.isXDRLimited = true; }
        else if (MessageTransaction.XDRMinimal.equals(trans)) { vc.isPnR = true; vc.isXDRMinimal = true; }
        else if (MessageTransaction.XDM.equals(trans))          vc.isXDM = true;
        else if (MessageTransaction.SQ.equals(trans))           vc.isSQ  = true;
        else if (MessageTransaction.MU.equals(trans))           vc.isMU  = true;

        return vc;
    }

    // is this fails to make a setting, it can be detected by the method
    // vc.isValid()
    static void setValidationContextFromWSAction(ValidationContext vc, String wsaction) {
        if (wsaction == null)
            return;
        TransactionType tt;
        boolean isRequest = true;
        tt = TransactionType.findByRequestAction(wsaction);
        if (tt == null) {
            tt = TransactionType.findByResponseAction(wsaction);
            isRequest = false;
        }
        if (tt == null)
            return;
        if (TransactionType.PROVIDE_AND_REGISTER.equals(tt)) {
            vc.isPnR = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.RETRIEVE.equals(tt)) {
            vc.isRet = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.REGISTER.equals(tt)) {
            vc.isR = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.UPDATE.equals(tt)) {
            vc.isMU = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.STORED_QUERY.equals(tt)) {
            vc.isSQ = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.XC_QUERY.equals(tt)) {
            vc.isSQ = true;
            vc.isXC = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.XC_RETRIEVE.equals(tt)) {
            vc.isRet = true;
            vc.isXC = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        } else if (TransactionType.MPQ.equals(tt)) {
            vc.isSQ = true;
            vc.isMultiPatient = true;
            vc.isRequest = isRequest;
            vc.isResponse = !isRequest;
        }
        vc.wsAction = wsaction;
    }

}
