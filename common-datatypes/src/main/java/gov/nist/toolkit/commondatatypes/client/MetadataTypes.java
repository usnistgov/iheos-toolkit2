package gov.nist.toolkit.commondatatypes.client;

public class MetadataTypes {
    final public static int METADATA_TYPE_UNKNOWN = 0;
    final public static int METADATA_TYPE_R       = 1;
    final public static int METADATA_TYPE_PR      = 2;
    final public static int METADATA_TYPE_Q       = 3;
    final public static int METADATA_TYPE_SQ      = 4;
    final public static int METADATA_TYPE_Rb      = 5;
    final public static int METADATA_TYPE_RET     = 6;
    final public static int AUDIT_LOG             = 7;
    final public static int METADATA_TYPE_PRb     = 8;
    final public static int METADATA_TYPE_REGISTRY_RESPONSE3 = 9;
    final public static int METADATA_TYPE_EPSOS      = 10;

    static String[] names = new String[11];
    
    public static String getMetadataTypeName(int type) {
    	if (names[0] == null) {
    		names[0] = "METADATA_TYPE_UNKNOWN";
    		names[1] = "METADATA_TYPE_R";
    		names[2] = "METADATA_TYPE_PR";
    		names[3] = "METADATA_TYPE_Q";
    		names[4] = "METADATA_TYPE_SQ";
    		names[5] = "METADATA_TYPE_Rb";
    		names[6] = "METADATA_TYPE_RET";
    		names[7] = "AUDIT_LOG";
    		names[8] = "METADATA_TYPE_PRb";
    		names[9] = "METADATA_TYPE_REGISTRY_RESPONSE3";
    		names[10] = "METADATA_TYPE_EPSOS";
    	}
    	
    	if (type < 11)
    		return names[type];
    	return "INVALID_TYPE";
    }
}
