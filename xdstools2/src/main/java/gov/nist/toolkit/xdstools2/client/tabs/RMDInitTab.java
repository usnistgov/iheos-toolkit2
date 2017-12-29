package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.RegisterAndQueryCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PidWidget;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.RegisterAndQueryRequest;

import java.util.ArrayList;
import java.util.List;

public class RMDInitTab extends GenericQueryTab {

	static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
	static {
		transactionTypes.add(TransactionType.PROVIDE_AND_REGISTER);
	}

	static CoupledTransactions couplings = new CoupledTransactions();

	String help = "";

	public RMDInitTab() {
		super(new GetDocumentsSiteActorManager());
	}

	PidWidget pid2Widget = new PidWidget();
	HTML summary = new HTML();

	private void setSummary(String content) {
		summary.setHTML("<h2>Results Summary</h2>" + content);
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>RMD Test Initialization</h2>");
		container.add(title);

		mainGrid = new FlexTable();
		int row = 0;

		mainGrid.setWidget(row, 0, new HTML("Second Patient ID"));
		HTMLTable.CellFormatter formatter = mainGrid.getCellFormatter();
		formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

		pid2Widget.setWidth("500px");
//		pid2Widget.addChangeHandler(new PidChangeHandler(this));
		mainGrid.setWidget(row++, 1, pid2Widget);


		container.add(mainGrid);
		return container;
	}

	@Override
	protected void bindUI() {
	}

	@Override
	protected void configureTabView() {
		queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
	}

	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();

            final SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
            if (siteSpec == null) {
                new PopupMessage("You must select a site first");
                return;
            }

            if (pidTextBox.getValue() == null || pidTextBox.getValue().equals("")) {
                new PopupMessage("You must enter a Patient ID");
                return;
            }
			if (pid2Widget.getValue() == null || pid2Widget.getValue().equals("")) {
				new PopupMessage("You must enter a Second Patient ID");
				return;
			}

			if (pidTextBox.getValue().equals(pid2Widget.getValue())) {
            	new PopupMessage("The Patient IDs must be different");
            	return;
			}

			resultPanel.add(summary);

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			new RegisterAndQueryCommand(){
				@Override
				public void onComplete(List<Result> result) {
					buildSummary(result);
					queryCallback.onSuccess(result);
				}
			}.run(
					new RegisterAndQueryRequest(getCommandContext(), siteSpec)
						.addSubmission(pidTextBox.getValue(), new TestInstance("RMDInit1"))
						.addSubmission(pid2Widget.getValue(), new TestInstance("RMDInit2"))
			);
		}
		
	}

	private void buildSummary(List<Result> results) {
		StringBuilder buf = new StringBuilder();
		List<String> sectionLabels = new ArrayList<>();

		for (Result result : results) {
			for (StepResult sr : result.stepResults) {
				sectionLabels.add(sr.toString());
			}
		}

		int sectionLabelIndex = 0;
		for (Result result : results) {
			AssertionResults ars = result.assertions;
			for (AssertionResult ar : ars.assertions) {
				String a = ar.assertion;
				if (a.contains("...$patientid$")) {

				}
				else if (a.startsWith("Section:") && sectionLabelIndex < sectionLabels.size()) {
					buf.append("<h3>").append(sectionLabels.get(sectionLabelIndex++)).append("</h3>");
				}
				else if (a.contains("_uid") || a.contains("_uuid") || a.contains("$patientid$")) {
					buf.append(rmReportBuilder(a)).append("<br />");
				}
			}
		}

		buf.append("<h2>Detailed Results</h2>");

		setSummary(buf.toString());
	}

	private String rmReportBuilder(String x) {
		return x.replace("ReportBuilder: ", "");
	}

	public String getWindowShortName() {
		return "regandquery";
	}



}
