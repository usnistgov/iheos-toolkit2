package gov.nist.toolkit.actorfactory.client;

import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CcdaTypeSelection {
	TkProps tkProps;
	List<RadioButton> ccdaTypesRBs;
	String ccdaTypesGroupName = "CCDATypesGroupName";
	ValidationContext defaultValidationContext = null;

	/**
	 * Selectable CCDA types are taken from TkProps.  defaultValidationContext, if
	 * non-null, holds the default CCDA type. This default value is
	 * pre-selected when radio button group is created.
	 * @param props - if this is null then type names must be passed into 
	 * method addCcdaTypesRadioGroup as a list.
	 * @param defaultValidationContext - to initialize selected type
	 */
	public CcdaTypeSelection(TkProps props, ValidationContext defaultValidationContext) {
		tkProps = props;
		this.defaultValidationContext = defaultValidationContext;
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

	/**
	 * Add CCDA Type Names to panel as radio buttons. 
	 * @param panel
	 * @param ccdaTypeNames if null, use names taken from TkProps.
	 * Old caption was 
	 * "CCDA Types for XDM or XDR content (CCDA validation may take a minute or more to run)"
	 */
	public void addCcdaTypesRadioGroup(VerticalPanel panel, List<String> ccdaTypeNames, String caption, boolean noValidationByDefault) {
		if (ccdaTypeNames == null)
			ccdaTypeNames = ccdaTypes();
		panel.add(HtmlMarkup.html(HtmlMarkup.bold(caption)));
		ccdaTypesRBs = new ArrayList<RadioButton>();
		for (String name : ccdaTypeNames) {
			RadioButton r = new RadioButton(ccdaTypesGroupName, name); 
			ccdaTypesRBs.add(r);
			panel.add(r);
		}
		
		// This name is expected in
		// gov/nist/toolkit/valregmsg/validation/factories/MessageValidatorFactory.java
		RadioButton r = new RadioButton(ccdaTypesGroupName, "Non-CCDA content (no validation)");
		ccdaTypesRBs.add(r);
		if (defaultValidationContext != null)
			select(defaultValidationContext.ccdaType);
		panel.add(r);
		if (noValidationByDefault && selected() == null)
			r.setValue(true);
	}

	public void addCcdaTypesRadioGroup(VerticalPanel panel, List<String> ccdaTypeNames, String caption) {
		addCcdaTypesRadioGroup(panel, ccdaTypeNames, caption, false);
	}
	
	void select(String value) {
		if (value == null) { // unselect all
			for (RadioButton rb : ccdaTypesRBs)
				rb.setValue(false);
		} else {
			for (RadioButton rb : ccdaTypesRBs) {
				if (value.equals(rb.getText())) {
					rb.setValue(true); // select matching RB
					return;
				}
			}
		}
	}
	
	RadioButton selected() {
		for (RadioButton rb : ccdaTypesRBs) {
			if (rb.getValue())
				return rb;
		}
		return null;
	}
	
	public void enableCcdaTypesRadioGroup(boolean enable) {
		for (RadioButton r : ccdaTypesRBs) {
			r.setEnabled(enable);
		}
	}

	public String getCcdaContentType() {
		for (RadioButton r : ccdaTypesRBs) {
			if (r.getValue())
				return r.getText();
		}
		return "";
	}

	public void addDocTypeToValidation(ValidationContext vc) {
		ValidationContext vc2 = new ValidationContext();
		vc2.ccdaType = getCcdaContentType();
		vc.addInnerContext(vc2);
	}


}
