package gov.nist.toolkit.saml.util;

public class TimeStampValidate {

	 /**
     * Validate the credential argument. It must contain a non-null Timestamp.
     * 
     * @param credential the Credential to be validated
     * @param data the RequestData associated with the request
     * @throws WSSecurityException on a failed validation
     */
    public static TimeStamp validate(TimeStamp timestamp) throws Exception {
       
        boolean timeStampStrict = true;
        int timeStampTTL = 300;
        int futureTimeToLive = 0;
        
        TimeStamp timeStamp = timestamp;
        // Validate whether the security semantics have expired
        if ((timeStampStrict && timeStamp.isExpired()) 
            || !timeStamp.verifyCreated(timeStamp.getCreated().getTime(), timeStamp.getExpires().getTime())) {
        	timeStamp.validateResult = "The security semantics of the message have expired" ;
        	timeStamp.errorVal = 1 ;
            //throw new Exception(
               // "The security semantics of the message have expired"
            //);
        }
        return timeStamp;
    }
    
    
    
 
	
	
}
