package gov.nist.toolkit.testengine.transactions;

/**
 * Call type from test client to Sim/Impl.  DIRECT_CALL is introduced to allow
 * unit testing and maybe future simulator integration. DIRECT here means method call
 * instead of SOAP call.
 *
 * Created by bill on 8/17/15.
 */

public enum CallType { DIRECT_CALL, SOAP }
