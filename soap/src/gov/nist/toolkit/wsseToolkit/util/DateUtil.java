package gov.nist.toolkit.wsseToolkit.util;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Contains a few helper methods to manipulate dates.
 * At later stage, it would be helpful to switch to more
 * full-featured third party library. A good candidate seems:
 * 
 * http://joda-time.sourceforge.net/
 * 
 * @author gerardin
 *
 */
public class DateUtil {
	
	//TODO make it more generic and let's have a random date, not the current one
	/** 
	* This utility method returns a past date before number of days. 
	* @param days 
	* @return 
	*/  
	public static Date getDateBeforeDays(int days) {  
	long backDateMS = System.currentTimeMillis() - ((long)days) *24*60*60*1000;  
	Date backDate = new Date();  
	backDate.setTime(backDateMS);  
	return backDate;  
	} 

	/** 
	* This utility method returns a future date after number of days. 
	* @param days 
	* @return 
	*/  
	public static Date getDateAfterDays(int days) {  
	long backDateMS = System.currentTimeMillis() + ((long)days) *24*60*60*1000;  
	Date backDate = new Date();  
	backDate.setTime(backDateMS);  
	return backDate;  
	}
	
	/**
	 * Returns an UTC String representation of a date conform to the Nwhin spec.
	 *  For instance, 2004-03-20T05:53:32Z.
	 * 
	 * @param date Date object.
	 */
	public static String toUTCDateFormat(Date date) {
		final String UTC_DATE_Z_FORMAT = "{0}-{1}-{2}T{3}:{4}:{5}Z";
		return dateToString(date, UTC_DATE_Z_FORMAT);
	}

	private static String dateToString(Date date, String format) {
		final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
		//TODO check GregorianCalendar representation seems to rely on the default locale
		GregorianCalendar cal = new GregorianCalendar(UTC_TIME_ZONE);
		cal.setTime(date);
		String[] params = new String[6];

		params[0] = formatInteger(cal.get(Calendar.YEAR), 4);
		params[1] = formatInteger(cal.get(Calendar.MONTH) + 1, 2);
		params[2] = formatInteger(cal.get(Calendar.DAY_OF_MONTH), 2);
		params[3] = formatInteger(cal.get(Calendar.HOUR_OF_DAY), 2);
		params[4] = formatInteger(cal.get(Calendar.MINUTE), 2);
		params[5] = formatInteger(cal.get(Calendar.SECOND), 2);

		//build up the string according to the UTC_DATE_Z_FORMAT pattern
		return MessageFormat.format(format, (Object[]) params);
	}

	//fixed number of characters are expected for each value
	private static String formatInteger(int value, int length) {
		String val = Integer.toString(value);
		int diff = length - val.length();

		for (int i = 0; i < diff; i++) {
			val = "0" + val;
		}

		return val;
	}

}
