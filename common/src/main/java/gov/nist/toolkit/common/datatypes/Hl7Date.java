package gov.nist.toolkit.common.datatypes;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Hl7Date {
	public String now() {
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);
		Calendar c = new GregorianCalendar();
		formatter.format("%s%02d%02d%02d%02d%02d", 
				c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH)+1, 
				c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));
		return sb.toString();
	}
	
	int daysThisMonth(int month) {
		switch (month) {
		case Calendar.JANUARY : return 31;
		case Calendar.FEBRUARY : return 28;
		case Calendar.MARCH : return 31;
		case Calendar.APRIL : return 30;
		case Calendar.MAY : return 31;
		case Calendar.JUNE : return 30;
		case Calendar.JULY : return 31;
		case Calendar.AUGUST : return 31;
		case Calendar.SEPTEMBER : return 30;
		case Calendar.OCTOBER : return 31;
		case Calendar.NOVEMBER : return 30;
		case Calendar.DECEMBER : return 31;
		}
		return 31;
	}

	public String plusMinutes(int minutes) {
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);
		Calendar c = new GregorianCalendar();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
		minute += minutes;
		if (hour > 24) {
			hour = hour - 24;
			day++;
		}
		if (day > daysThisMonth(month)) {
			day = day - daysThisMonth(month);
			month++;
		}
		if (month > 12) {
			month = month - 12;
			year++;
		}
		
		
		formatter.format("%s%02d%02d%02d%02d%02d", 
				year, 
				month, 
				day,
				hour,
				minute,
				second);
		return sb.toString();
	}

	// useful for testing
	public String lastyear() {
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);
		Calendar c = new GregorianCalendar();
		formatter.format("%s%02d%02d%02d%02d%02d", 
				c.get(Calendar.YEAR)-1, 
				c.get(Calendar.MONTH)+1, 
				c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));
		return sb.toString();
	}



}
