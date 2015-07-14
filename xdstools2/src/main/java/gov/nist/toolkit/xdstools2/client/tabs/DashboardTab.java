package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.xdstools2.client.RegistryStatus;
import gov.nist.toolkit.xdstools2.client.RepositoryStatus;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DashboardTab  extends GenericQueryTab {
	//	final protected ToolkitServiceAsync toolkitService = GWT
	//	.create(ToolkitService.class);

	List<RegistryStatus> regData;
	List<RepositoryStatus> repData;

	VerticalPanel mainDataArea = new VerticalPanel();
	Anchor reload;


	public DashboardTab() {
		super(new GetDocumentsSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();


		container.addTab(topPanel, "Dashboard", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>XDS Dashboard</h2>");
		topPanel.add(title);

		topPanel.add(mainDataArea);

		reload = new Anchor();
		reload.setTitle("Reload actors configuration");
		reload.setText("[reload]");
		menuPanel.add(reload);
		reload.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				load();
			}

		});


		load();
	}

	void draw() {

		try {
			mainDataArea.add(HtmlMarkup.html(repData.get(0).date));
		} catch (Exception e) {

		}

		mainDataArea.add(HtmlMarkup.html(HtmlMarkup.h4("Repository => Registry Mapping")));

		String happyGif = "icons/happy0024.gif";
		String sadGif = "icons/sad0019.gif";

		String happyHtml = "<img src=\"" + happyGif + "\"/>";
		String sadHtml = "<img src=\"" + sadGif + "\"/>";

		FlexTable repTable = new FlexTable();
		repTable.setBorderWidth(2);
		repTable.setCellSpacing(0);
		int row=0;

		repTable.setHTML(row, 1, HtmlMarkup.h5("Repository"));
		repTable.setHTML(row, 2, HtmlMarkup.h5("Registry"));
		repTable.setHTML(row, 3, HtmlMarkup.h5("Repository PnR Endpoint"));
		repTable.setHTML(row, 4, HtmlMarkup.h5("Messages"));

		row++;

		for (RepositoryStatus rep : repData) {
			if (rep.status)
				repTable.setHTML(row, 0, happyHtml);
			else
				repTable.setHTML(row, 0, sadHtml);
			repTable.setHTML(row, 1, HtmlMarkup.red(rep.name, !rep.status));
			repTable.setText(row, 2, rep.registry);
			repTable.setText(row, 3, rep.endpoint);
			repTable.setText(row, 4, start(rep.getErrorsAsString() + rep.fatalError));
			row++;
		}

		mainDataArea.add(repTable);

		mainDataArea.add(HtmlMarkup.html(HtmlMarkup.h4("Registry Status")));

		FlexTable regTable = new FlexTable();
		regTable.setBorderWidth(2);
		regTable.setCellSpacing(0);
		row=0;

		regTable.setHTML(row, 1, HtmlMarkup.h5("Registry"));
		regTable.setHTML(row, 2, HtmlMarkup.h5("SQ Endpoint"));
		regTable.setHTML(row, 3, HtmlMarkup.h5("Messages"));

		row++;

		for (RegistryStatus reg : regData) {
			if (reg.status)
				regTable.setHTML(row, 0, happyHtml);
			else
				regTable.setHTML(row, 0, sadHtml);
			regTable.setHTML(row, 1, HtmlMarkup.red(reg.name, !reg.status));
			regTable.setText(row, 2, reg.endpoint);
			regTable.setText(row, 3, start(reg.getErrorsAsString() + reg.fatalError));
			row++;
		}

		mainDataArea.add(regTable);


	}

	String start(String txt) {
		if (txt == null)
			return "";
		if (txt.equals("null"))
			return "";
		return txt.substring(0, min(256, txt.length()));
	}

	int min(int a, int b) {
		if (a < b)
			return a;
		return b;
	}


	boolean regDone = false;
	boolean repDone = false;

	void load() {

		regDone = false;
		repDone = false;

		mainDataArea.clear();

		toolkitService.getDashboardRegistryData(new AsyncCallback<List<RegistryStatus>>() {

			public void onFailure(Throwable caught) {
				mainDataArea.add(HtmlMarkup.html(caught.getMessage()));
			}

			public void onSuccess(List<RegistryStatus> result) {
				regData = result;
				regDone = true;

				if (regDone && repDone)
					draw();
			}

		});

		toolkitService.getDashboardRepositoryData(new AsyncCallback<List<RepositoryStatus>>() {

			public void onFailure(Throwable caught) {
				mainDataArea.add(HtmlMarkup.html(caught.getMessage()));
			}

			public void onSuccess(List<RepositoryStatus> result) {
				repData = result;
				repDone = true;

				if (regDone && repDone)
					draw();
			}

		});


	}


	public String getWindowShortName() {
		return "dashboard";
	}
}
