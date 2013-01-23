package gov.nist.direct.config;



public class Config {
	
	// Configuration variables, loaded from config.txt file.
	public static int ACCEPTED_DELAY_FOR_MDN_RECEPTION;
	
	
	
	// Reads configuration values from config.txt file.
	
	

	
	
	// Getters and setters
	public static int getACCEPTED_DELAY_FOR_MDN_RECEPTION() {
		return ACCEPTED_DELAY_FOR_MDN_RECEPTION;
	}

	public static void setACCEPTED_DELAY_FOR_MDN_RECEPTION(
			int aCCEPTED_DELAY_FOR_MDN_RECEPTION) {
		ACCEPTED_DELAY_FOR_MDN_RECEPTION = aCCEPTED_DELAY_FOR_MDN_RECEPTION;
	}

		
			
	
	
}
