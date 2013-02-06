package gov.nist.toolkit.xdstools2.client.tabs.messageValidator;

import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdstools2.client.HtmlMarkup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CcdaTypeSelection {
	TkProps tkProps;
	List<RadioButton> ccdaTypes;
	String ccdaTypesGroupName = "CCDATypesGroupName";
	
	public CcdaTypeSelection(TkProps props) {
		tkProps = props;
	}
	
	public List<String> ccdaTypes() {
		List<String> types = new ArrayList<String>();
		
		TkProps ccdaProps = tkProps.withPrefixRemoved("direct.reporting.ccdatype");
		for (int i=1; i<30; i++) {
			String en = Integer.toString(i);
			
			String ctype = null;
			String display = null;
			try {
				ctype = ccdaProps.get("type" + en);
				display = ccdaProps.get("display" + en);
			} catch (PropertyNotFoundException e) {
			}
			if (ctype == null || display == null)
				break;
			types.add(ctype + " - " + display);
		}
		return types;
	}
	
	public void addCcdaTypesRadioGroup(VerticalPanel panel, List<String> ccdaTypeNames) {
		panel.add(HtmlMarkup.html(HtmlMarkup.bold("CCDA Types for XDM or XDR content (CCDA validation may take a minute or more to run)")));
		ccdaTypes = new ArrayList<RadioButton>();
		for (String name : ccdaTypeNames) {
			RadioButton r = new RadioButton(ccdaTypesGroupName, name); 
			ccdaTypes.add(r);
			panel.add(r);
		}
		RadioButton r = new RadioButton(ccdaTypesGroupName, "Non-CCDA content");
		ccdaTypes.add(r);
		panel.add(r);
	}
	
	public void enableCcdaTypesRadioGroup(boolean enable) {
		for (RadioButton r : ccdaTypes) {
			r.setEnabled(enable);
		}
	}
	
	public String getCcdaContentType() {
		for (RadioButton r : ccdaTypes) {
			if (r.getValue())
				return r.getText();
		}
		return "";
	}

	void addDocTypeToValidation(ValidationContext vc) {
		ValidationContext vc2 = new ValidationContext();
		vc2.ccdaType = getCcdaContentType();
		vc.addInnerContext(vc2);
	}


}
