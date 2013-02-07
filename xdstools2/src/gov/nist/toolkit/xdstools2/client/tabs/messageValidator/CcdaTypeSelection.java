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
	 * @param ccdaTypeNames if null, use names taken from TkProps
	 */
	public void addCcdaTypesRadioGroup(VerticalPanel panel, List<String> ccdaTypeNames) {
		if (ccdaTypeNames == null)
			ccdaTypeNames = ccdaTypes();
		panel.add(HtmlMarkup.html(HtmlMarkup.bold("CCDA Types for XDM or XDR content (CCDA validation may take a minute or more to run)")));
		ccdaTypes = new ArrayList<RadioButton>();
		for (String name : ccdaTypeNames) {
			RadioButton r = new RadioButton(ccdaTypesGroupName, name); 
			ccdaTypes.add(r);
			panel.add(r);
		}
		RadioButton r = new RadioButton(ccdaTypesGroupName, "Non-CCDA content (no validation)");
		ccdaTypes.add(r);
		if (defaultValidationContext != null)
			select(defaultValidationContext.ccdaType);
		panel.add(r);
	}

	void select(String value) {
		if (value == null) { // unselect all
			for (RadioButton rb : ccdaTypes)
				rb.setValue(false);
		} else {
			for (RadioButton rb : ccdaTypes) {
				if (value.equals(rb.getText())) {
					rb.setValue(true); // select matching RB
					return;
				}
			}
		}
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
