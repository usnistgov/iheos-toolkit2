package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Author;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.RegistryObject;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditDisplay {
	VerticalPanel detailPanel;
	MetadataEditorTab it;
	String textBoxWidth = "400";
	boolean isUpdate = true;
	RegistryObject currentObject;
	VerticalPanel buttonPanel;
	
	final protected ToolkitServiceAsync toolkitService = GWT
	.create(ToolkitService.class);


	public EditDisplay(VerticalPanel detailPanel, VerticalPanel buttonPanel, MetadataEditorTab it) {
		this.detailPanel = detailPanel;
		this.buttonPanel = buttonPanel;
		this.it = it;
		
		buttonPanel.setSpacing(20);

		Button verifyButton = new Button("Verify");
		verifyButton.setEnabled(false);
		buttonPanel.add(verifyButton);
		
		Button sendButton = new Button("Submit");
		sendButton.setEnabled(false);
		sendButton.addClickHandler(new SubmitClickHandler());
		buttonPanel.add(sendButton);
		
		Button saveButton = new Button("Save");
		saveButton.setEnabled(false);
		buttonPanel.add(saveButton);
	}

	TextBox newTB(String text) {
		TextBox tb = new TextBox();
		tb.setWidth(textBoxWidth);
		if (text != null && !text.equals(""))
			tb.setText(text);
		return tb;
	}
	
	class SubmitClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}

	static String submissionTypesGroup = "submissionTypesGroup";
	static String[] submissionTypes = {"New Submission", "Update"};
	RadioButton[] submissionTypeButtons = {
			new RadioButton(submissionTypesGroup, submissionTypes[0]),
			new RadioButton(submissionTypesGroup, submissionTypes[1])
	};

	void displayDetail(RegistryObject ro) {
		currentObject = ro;
		if (ro instanceof DocumentEntry)
			displayDetail((DocumentEntry) ro);
	}

	void displayDetail(DocumentEntry de) {
		detailPanel.clear();
		detailPanel.add(HyperlinkFactory.addHTML("<h4>DocumentEntry</h4>"));
		FlexTable ft = new FlexTable();
		int row=0;

		HorizontalPanel controls = new HorizontalPanel();
		controls.add(bold("Submission Type:  "));
		controls.add(submissionTypeButtons[0]);
		controls.add(submissionTypeButtons[1]);
		submissionTypeButtons[0].addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				isUpdate = false;
				displayDetail(currentObject);
			}

		});
		submissionTypeButtons[1].addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				isUpdate = true;
				displayDetail(currentObject);
			}

		});

		if (isUpdate) 
			submissionTypeButtons[1].setValue(true);
		else
			submissionTypeButtons[0].setValue(true);

		ft.setWidget(row, 1, controls);
		row++;

		ft.setHTML(row, 0, "title");
		ft.setWidget(row, 1, newTB(de.title));
		row++;

		ft.setHTML(row, 0, "comments");
		ft.setWidget(row, 1, newTB(de.comments));
		row++;

		//		ft.setHTML(row, 0, "id");
		//		ft.setWidget(row, 1, newTB(de.id));
		//		row++;

		if (isUpdate) {
			ft.setHTML(row, 0, "lid");
			ft.setWidget(row, 1, new Label(de.lid));
			
			row++;
		}

		if (isUpdate) {
			ft.setHTML(row, 0, "version");
			ft.setWidget(row, 1, newTB(de.version));
			if (de.version != null && de.version.equals("1.1")) {
				ft.setWidget(row, 2, redAsHTML("A version of 1.1 implies that this Registry does not support Metadata Update"));
			}
			
			row++;
		}

		//		ft.setHTML(row, 0, "uniqueId");
		//		ft.setWidget(row, 1, newTB(de.uniqueId));
		//		row++;

		ft.setHTML(row, 0, "patientId");
		ft.setWidget(row, 1, newTB(de.patientId));
		row++;

		//		ft.setHTML(row, 0, "availabilityStatus");
		//		ft.setWidget(row, 1, newTB(de.status));
		//		row++;

		//		ft.setHTML(row, 0, "homeCommunityId");
		//		ft.setWidget(row, 1, newTB(de.home));
		//		row++;

		ft.setHTML(row, 0, "mimeType");
		ft.setWidget(row, 1, newTB(de.mimeType));
		row++;

		ft.setHTML(row, 0, "hash");
		ft.setWidget(row, 1, newTB(de.hash));
		row++;

		ft.setHTML(row, 0, "size");
		ft.setWidget(row, 1, newTB(de.size));
		row++;

		ft.setHTML(row, 0, "repositoryUniqueId");
		ft.setWidget(row, 1, newTB(de.repositoryUniqueId));
		row++;

		ft.setHTML(row, 0, "lang");
		ft.setWidget(row, 1, newTB(de.lang));
		row++;

		ft.setHTML(row, 0, "legalAuthenticator");
		ft.setWidget(row, 1, newTB(de.legalAuth));
		row++;

		ft.setHTML(row, 0, "serviceStartTime");
		ft.setWidget(row, 1, newTB(de.serviceStartTime));
		row++;

		ft.setHTML(row, 0, "serviceStopTime");
		ft.setWidget(row, 1, newTB(de.serviceStopTime));
		row++;

		ft.setHTML(row, 0, "creationTime");
		ft.setWidget(row, 1, newTB(de.creationTime));
		row++;

		ft.setHTML(row, 0, "sourcePatientId");
		ft.setWidget(row, 1, newTB(de.sourcePatientId));
		row++;

		row = displayDetail(ft, row, "sourcePatientInfo", de.sourcePatientInfo);

		row = displayDetail(ft, row, "classCode", de.classCode);

		row = displayDetail(ft, row, "confCodes", de.confCodes);

		row = displayDetail(ft, row, "eventCodeList", de.eventCodeList);

		row = displayDetail(ft, row, "formatCode", de.formatCode);

		row = displayDetail(ft, row, "healthcareFacilityType", de.hcftc);

		row = displayDetail(ft, row, "practiceSetting", de.pracSetCode);

		row = displayDetail(ft, row, "typeCode", de.typeCode);

		row = displayDetailAuthor(ft, row, de.authors);

		detailPanel.add(ft);

	}

	/*
	DocumentEntry collectDetail(FlexTable ft) {
		DocumentEntry de = new DocumentEntry();
		TextBox tb;
		int row=0;

		tb = (TextBox) ft.getWidget(row, 1);
		de.title = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.comments = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.id = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.lid = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.version = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.uniqueId = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.patientId = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.status = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.home = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.mimeType = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.hash = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.size = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.repositoryUniqueId = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.lang = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.legalAuth = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.serviceStartTime = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.serviceStopTime = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.creationTime = tb.getText();
		row++;

		tb = (TextBox) ft.getWidget(row, 1);
		de.sourcePatientId = tb.getText();
		row++;

		row = displayDetail(ft, row, "sourcePatientInfo", de.sourcePatientInfo);

		row = displayDetail(ft, row, "classCode", de.classCode);

		row = displayDetail(ft, row, "confCodes", de.confCodes);

		row = displayDetail(ft, row, "eventCodeList", de.eventCodeList);

		row = displayDetail(ft, row, "formatCode", de.formatCode);

		row = displayDetail(ft, row, "healthcareFacilityType", de.hcftc);

		row = displayDetail(ft, row, "practiceSetting", de.pracSetCode);

		row = displayDetail(ft, row, "typeCode", de.typeCode);

		row = displayDetailAuthor(ft, row, de.authors);

		detailPanel.add(ft);

	}

	 */

	int displayDetail(FlexTable ft, int row, String label, List<String> values) {
		int startRow = row;

		for (String value : values) {
			if (row == startRow)
				ft.setText(row, 0, label);

			ft.setWidget(row, 1, newTB(value));

			row++;
		}

		return row;
	}

	int displayDetailAuthor(FlexTable ft, int row, List<Author> values) {
		int startRow;

		for (Author a : values) {
			ft.setText(row, 0, "author");
			ft.setWidget(row, 1, newTB(a.person));
			row++;

			if (a.institutions.size() > 0) {
				startRow = row;
				for (String in : a.institutions) {
					if (startRow == row)
						ft.setText(row, 0, "institutions");
					ft.setWidget(row, 1, newTB(in));
					row++;
				}
			}

			if (a.roles.size() > 0) {
				startRow = row;
				for (String in : a.roles) {
					if (startRow == row)
						ft.setText(row, 0, "roles");
					ft.setWidget(row, 1, newTB(in));
					row++;
				}
			}

			if (a.specialties.size() > 0) {
				startRow = row;
				for (String in : a.specialties) {
					if (startRow == row)
						ft.setText(row, 0, "specialties");
					ft.setWidget(row, 1, newTB(in));
					row++;
				}
			}

		}

		return row;
	}

	HTML bold(String in) {
		HTML h = new HTML();
		h.setHTML("<b>" + in + "</b>");
		return h;
	}
	
	HTML redAsHTML(String in, boolean condition) {
		if (!condition)
			return redAsHTML(in);
		HTML h = new HTML();
		h.setText(in);
		return h;
	}

	HTML redAsHTML(String in) {
		HTML h = new HTML();
		h.setHTML("<font color=\"#FF0000\">" + in + "</font>");
		return h;
	}

}
