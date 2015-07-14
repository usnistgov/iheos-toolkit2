package gov.nist.toolkit.commondatatypes.client;


public class SchematronMetadataTypes {
    final public static int METADATA_TYPE_UNKNOWN =  0;
    final public static int IHE_XCPD_305          = 1;
    final public static int IHE_XCPD_306          = 2;
    final public static int NwHINPD_305     	   = 3;
    final public static int NwHINPD_306            = 4;
    //the next three types are needed to validate a HITSP_C32.
    //the scheamtron rules will be fired for each dependent required specification
    final public static int C32                    = 5;
    final public static int NCPDP 				   = 6;
    
    static String[] InitList = {"IHE_XCPD_305"};
    static String[] RespList = {"IHE_XCPD_306"};
    static String[] NwHINxpcdInit = {"NwHINPD_305"};
    static String[] NwHINxcpdResp = {"NwHINPD_306"};
    static String[] C32List = {"CCD","CDT4CDA","MU_HITSP_C32"};
    static String[] NCPDPList = {"NCPDP_MESSAGE"};
    
    
	public static String[] getSchematronMetadataTypeName(int type) {
		if (type < 7) {
			if (type == IHE_XCPD_305) {
				return InitList;
			}
			if (type == IHE_XCPD_306) {
				return RespList;
			}
			if (type == NwHINPD_305) {
				return NwHINxpcdInit;
			}
			if (type == NwHINPD_306) {
				return NwHINxcpdResp;
			}
			if (type == C32) {
				return C32List;
			}
			if (type == NCPDP) {
				return NCPDPList;
			}
		}
		return null;
    	
    }
 }