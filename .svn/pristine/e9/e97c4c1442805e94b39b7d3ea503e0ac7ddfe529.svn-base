package gov.nist.toolkit.xdstools2.client.tabs;


import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FindPatientTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.XC_QUERY);
	}
	
	static CoupledTransactions couplings = new CoupledTransactions();

	/*
	 * Required parameters:
	 * firstName
	 * lastName
	 * gender
	 * dob
	 * 
	 * Optional parameters:
	 */
	TextBox firstName;
	TextBox secondName;
	TextBox lastName;
	TextBox suffix;
	TextBox gender;
	TextBox dob;
	TextBox ssn;
	TextBox pid;
	TextBox Required;
	TextBox homeAddress1;
	TextBox homeAddress2;
	TextBox homeCity;
	TextBox homeState;
	TextBox homeZip;
	TextBox homeCountry;
	TextBox mothersFirstName;
	TextBox mothersSecondName;
	TextBox mothersLastName;
	TextBox mothersSuffix;
	TextBox homePhone;
	TextBox workPhone;
	TextBox principleCareProvider;
	TextBox pob;
	TextBox pobAddress1;
	TextBox pobAddress2;
	TextBox pobCity;
	TextBox pobState;
	TextBox pobZip;
	TextBox pobCountry;
	
	public FindPatientTab() {
		super(new FindDocumentsSiteActorManager());
	}
	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
			
		container.addTab(topPanel, "FindPatient", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Find Patient</h2>");
		topPanel.add(title);

		FlexTable mainGrid2;
		mainGrid2 = new FlexTable();
		
		//mainGrid2.setBorderWidth(1);
		
		topPanel.add(mainGrid2);
		/****************************************************************************/
	    /* Top Row will be Required Fields 
	     * 
	     */
		
		int row = 0;
		int label_col = 0;
		int field_col = 1;
		
		// Required text
		HTML requiredLabel = new HTML();
		requiredLabel.setText("Required Field *");
		mainGrid2.setWidget(row,label_col, requiredLabel);
		mainGrid2.getCellFormatter().addStyleName(row, label_col, "requiredFieldLabel");
		HTML blankLabel = new HTML();
		blankLabel.setText("");
		mainGrid2.setWidget(row,field_col, blankLabel);
	
		row = 1;
		label_col = 0;
		field_col = 1;
		
		// 6 columns: Patient - Address - Mother
		// 1) Name
		// first Name
		HTML firstNameLabel = new HTML();
		firstNameLabel.setText("First Name *");
		mainGrid2.setWidget(row,label_col,firstNameLabel);
		mainGrid2.getCellFormatter().addStyleName(row, label_col, "requiredFieldLabel");
		firstName = new TextBox();
		firstName.setWidth("150");
		mainGrid2.setWidget(row, field_col, firstName);
		
		label_col = label_col + 2;
		field_col = field_col + 2;
		
		// last Name		
		HTML lastNameLabel = new HTML();
		lastNameLabel.setText("Last Name *");
		mainGrid2.setWidget(row,label_col, lastNameLabel);
		mainGrid2.getCellFormatter().addStyleName(row, label_col, "requiredFieldLabel");
		lastName = new TextBox();
		lastName.setWidth("150");
		mainGrid2.setWidget(row, field_col, lastName);
		
		label_col = label_col + 2;
		field_col = field_col + 2;
		
		
		// 2) Gender		
		HTML genderLabel = new HTML();
		genderLabel.setText("Gender *");
		mainGrid2.setWidget(row,label_col, genderLabel);
		mainGrid2.getCellFormatter().addStyleName(row, label_col, "requiredFieldLabel");
		gender = new TextBox();
		gender.setWidth("50");
		mainGrid2.setWidget(row, field_col, gender);
		
		label_col = label_col + 2;
		field_col = field_col + 2;
		
		// 3) Date of Birth - dob
		HTML dobLabel = new HTML();
		dobLabel.setText("Date of Birth *");
		mainGrid2.setWidget(row,label_col, dobLabel);
		mainGrid2.getCellFormatter().addStyleName(row, label_col, "requiredFieldLabel");
		dob = new TextBox();
		dob.setWidth("75");
		mainGrid2.setWidget(row, field_col, dob);
		
		/****************************************************************/
		/*
		 * Now handle everything by column
		 */
		
		row = 2;
		label_col = 0;
		field_col = 1;
		
		// second Name
		HTML secondNameLabel = new HTML();
		secondNameLabel.setText("Middle Name");
		mainGrid2.setWidget(row, label_col, secondNameLabel);		
		secondName = new TextBox();
		secondName.setWidth("150");
		mainGrid2.setWidget(row, field_col, secondName);
		row++;

		//  Suffix
		HTML suffixLabel = new HTML();
		suffixLabel.setText("Name Suffix");
		mainGrid2.setWidget(row,label_col, suffixLabel);
		suffix = new TextBox();
		suffix.setWidth("50");
		mainGrid2.setWidget(row, field_col, suffix);
		row++;
		
		// 4) Social Security Number - ssn
		HTML ssnLabel = new HTML();
		ssnLabel.setText("Social Security Number");
		mainGrid2.setWidget(row,label_col, ssnLabel);
		ssn = new TextBox();
		ssn.setWidth("75");
		mainGrid2.setWidget(row, field_col, ssn);
		row++;
		
		// 5) Patient ID
		HTML pidLabel = new HTML();
		pidLabel.setText("Patient ID");
		mainGrid2.setWidget(row,label_col, pidLabel);
		pid = new TextBox();
		pid.setWidth("75");
		mainGrid2.setWidget(row, field_col, pid);
		row++;
		
		// 7) Telephone
		// Home Phone
		HTML homePhoneLabel = new HTML();
		homePhoneLabel.setText("Home Phone");
		mainGrid2.setWidget(row, label_col, homePhoneLabel);
		homePhone = new TextBox();
		homePhone.setWidth("100");
		mainGrid2.setWidget(row, field_col, homePhone);
		row++;	
		
		// Work Phone
		HTML workPhoneLabel = new HTML();
		workPhoneLabel.setText("Work Phone");
		mainGrid2.setWidget(row, label_col, workPhoneLabel);
		workPhone = new TextBox();
		workPhone.setWidth("100");
		mainGrid2.setWidget(row, field_col, workPhone);
		row++;	
		
		
		// 8) Principle Care Provider
		HTML principleCareProviderLabel = new HTML();
		principleCareProviderLabel.setText("Principle Care Provider ID");
		mainGrid2.setWidget(row,label_col, principleCareProviderLabel);
		principleCareProvider = new TextBox();
		principleCareProvider.setWidth("150");
		mainGrid2.setWidget(row, field_col, principleCareProvider);
		row++;
		
		row = 2;
		label_col = 2;
		field_col = 3;
		
		// 6) Address 
		// Home Address 1
		HTML homeAddress1Label = new HTML();
		homeAddress1Label.setText("Home Address 1");
		mainGrid2.setWidget(row,label_col,homeAddress1Label);
		homeAddress1 = new TextBox();
		homeAddress1.setWidth("250");
		mainGrid2.setWidget(row, 3, homeAddress1);
		row++;

		// Home Address 2
		HTML homeAddress2Label = new HTML();
		homeAddress2Label.setText("Home Address 2");
		mainGrid2.setWidget(row, label_col, homeAddress2Label);
		homeAddress2 = new TextBox();
		homeAddress2.setWidth("250");
		mainGrid2.setWidget(row, field_col, homeAddress2);
		row++;			
		
		// Home City
		HTML homeCityLabel = new HTML();
		homeCityLabel.setText("City");
		mainGrid2.setWidget(row, label_col, homeCityLabel);
		homeCity = new TextBox();
		homeCity.setWidth("200");
		mainGrid2.setWidget(row, field_col, homeCity);
		row++;	
		
		// Home State
		HTML homeStateLabel = new HTML();
		homeStateLabel.setText("State");
		mainGrid2.setWidget(row, label_col, homeStateLabel);
		homeState = new TextBox();
		homeState.setWidth("50");
		mainGrid2.setWidget(row, field_col, homeState);
		row++;	
		
		// Home Zip Code
		HTML homeZipLabel = new HTML();
		homeZipLabel.setText("Zip");
		mainGrid2.setWidget(row, label_col, homeZipLabel);
		homeZip = new TextBox();
		homeZip.setWidth("50");
		mainGrid2.setWidget(row, field_col, homeZip);
		row++;	
		
		// pob Country
		HTML homeCountryLabel = new HTML();
		homeCountryLabel.setText("Country");
		mainGrid2.setWidget(row, label_col, homeCountryLabel);
		homeCountry = new TextBox();
		homeCountry.setWidth("100");
		mainGrid2.setWidget(row, field_col, homeCountry);
		row++;	
		
		row = 2;
		label_col = 4;
		field_col = 5;
		// 9) Mothers Name
		// Mothers first Name
		HTML mothersFirstNameLabel = new HTML();
		mothersFirstNameLabel.setText("Mothers First Name");
		mainGrid2.setWidget(row,label_col,mothersFirstNameLabel);
		mothersFirstName = new TextBox();
		mothersFirstName.setWidth("150");
		mainGrid2.setWidget(row, field_col, mothersFirstName);
		row++;

		// Mothers second Name
		HTML mothersSecondNameLabel = new HTML();
		mothersSecondNameLabel.setText("Mothers Middle Name");
		mainGrid2.setWidget(row,label_col, mothersSecondNameLabel);		
		mothersSecondName = new TextBox();
		mothersSecondName.setWidth("150");
		mainGrid2.setWidget(row, field_col, mothersSecondName);
		row++;

		// Mothers last Name
		HTML mothersLastNameLabel = new HTML();
		mothersLastNameLabel.setText("Mothers Last Name");
		mainGrid2.setWidget(row,label_col, mothersLastNameLabel);
		mothersLastName = new TextBox();
		mothersLastName.setWidth("150");
		mainGrid2.setWidget(row, field_col, mothersLastName);
		row++;
		
		//  Mothers Suffix
		HTML mothersSuffixLabel = new HTML();
		mothersSuffixLabel.setText("Mothers Suffix");
		mainGrid2.setWidget(row,label_col, mothersSuffixLabel);
		mothersSuffix = new TextBox();
		mothersSuffix.setWidth("50");
		mainGrid2.setWidget(row, field_col, mothersSuffix);
		row++;
		
		
		row = 2;
		label_col = 6;
		field_col = 7;
		// 10) Place of Birth ID
		HTML pobLabel = new HTML();
		pobLabel.setText("Place of Birth");
		mainGrid2.setWidget(row,label_col,pobLabel);
		pob = new TextBox();
		pob.setWidth("250");
		mainGrid2.setWidget(row, field_col, pob);
		row++;
		
		// 11) Place of Birth
		// 11) Home Address 1
		HTML pobAddress1Label = new HTML();
		pobAddress1Label.setText("Place of Birth Address 1");
		mainGrid2.setWidget(row,label_col,pobAddress1Label);
		pobAddress1 = new TextBox();
		pobAddress1.setWidth("250");
		mainGrid2.setWidget(row, field_col, pobAddress1);
		row++;

		// pob Address 2
		HTML pobAddress2Label = new HTML();
		pobAddress2Label.setText("Place of Birth Address 2");
		mainGrid2.setWidget(row, label_col, pobAddress2Label);
		pobAddress2 = new TextBox();
		pobAddress2.setWidth("250");
		mainGrid2.setWidget(row, field_col, pobAddress2);
		row++;			
		
		// pob City
		HTML pobCityLabel = new HTML();
		pobCityLabel.setText("Place of Birth City");
		mainGrid2.setWidget(row, label_col, pobCityLabel);
		pobCity = new TextBox();
		pobCity.setWidth("200");
		mainGrid2.setWidget(row, field_col, pobCity);
		row++;	
		
		// pob State
		HTML pobStateLabel = new HTML();
		pobStateLabel.setText("Place of Birth State");
		mainGrid2.setWidget(row, label_col, pobStateLabel);
		pobState = new TextBox();
		pobState.setWidth("50");
		mainGrid2.setWidget(row, field_col, pobState);
		row++;	
		
		// pob Zip Code
		HTML pobZipLabel = new HTML();
		pobZipLabel.setText("Place of Birth Zip");
		mainGrid2.setWidget(row, label_col, pobZipLabel);
		pobZip = new TextBox();
		pobZip.setWidth("50");
		mainGrid2.setWidget(row, field_col, pobZip);
		row++;	
		
		// pob Country
		HTML pobCountryLabel = new HTML();
		pobCountryLabel.setText("Place of Birth Country");
		mainGrid2.setWidget(row, label_col, pobCountryLabel);
		pobCountry = new TextBox();
		pobCountry.setWidth("100");
		mainGrid2.setWidget(row, field_col, pobCountry);
		row++;	

		row = 0;
		mainGrid = new FlexTable();
		topPanel.add(mainGrid);
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings);
	}
	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			if (firstName.getValue() == null || firstName.getValue().equals("")) {
				new PopupMessage("You must enter a First Name");
				return;
			}
			if (lastName.getValue() == null || lastName.getValue().equals("")) {
				new PopupMessage("You must enter a Last Name");
				return;
			}
			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);
			toolkitService.findPatient(siteSpec, firstName.getValue().trim(), secondName.getValue().trim(), lastName.getValue().trim(), suffix.getValue().trim(), 
					                   gender.getValue().trim(), dob.getValue().trim(), ssn.getValue().trim(), pid.getValue().trim(),
					                   homeAddress1.getValue().trim(), homeAddress2.getValue().trim(), homeCity.getValue().trim(), homeState.getValue().trim(), homeZip.getValue().trim(), homeCountry.getValue().trim(),
					                   mothersFirstName.getValue().trim(), mothersSecondName.getValue().trim(), mothersLastName.getValue().trim(), mothersSuffix.getValue().trim(), 
					                   homePhone.getValue().trim(), workPhone.getValue().trim(), principleCareProvider.getValue().trim(), 
					                   pob.getValue().trim(), pobAddress1.getValue().trim(), pobAddress2.getValue().trim(), pobCity.getValue().trim(), pobState.getValue().trim(), pobZip.getValue().trim(), pobCountry.getValue().trim(),
					                   queryCallback);
		}
		
	}	
	
	
	
	public String getWindowShortName() {
		return "findpatient";
	}
}
