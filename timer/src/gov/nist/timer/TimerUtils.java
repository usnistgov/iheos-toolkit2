package gov.nist.timer;

import java.util.Date;

public class TimerUtils {
	

	/**
	 * Computes the difference in milliseconds between two dates. The order in which the dates are given does not matter.
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getTimeDifference(Date date1, Date date2){
	   return (int) ((date1.getTime() - date2.getTime()));
		
	}
	
	/**
	 * Returns the Accepted Delay for MDN Reception, in minutes. Delay is now hardcoded but will be later stored in config file.
	 * @return
	 */
	public static int getACCEPTED_DELAY_FOR_MDN_RECEPTION() {
		return 30;
	}
	

}
