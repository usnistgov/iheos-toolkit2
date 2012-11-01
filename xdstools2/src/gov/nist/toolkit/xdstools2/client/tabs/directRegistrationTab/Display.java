package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

public class Display {
	static String red   = "#FF0000";
	static String black = "#000000";
	
	static public String asRed(String text) {
		return "<font color=\"" + red + "\">" + text + "</font>" ;
	}
	
	static public String asBlack(String text) {
		return "<font color=\"" + black + "\">" + text + "</font>" ;
	}

}
