package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

public class ConnectathonTab extends GenericQueryTab {

	public ConnectathonTab() {
		super(new GetDocumentsSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel fpanel = new FlowPanel();

		HTML title = new HTML();
		title.setHTML("<h2>Connectathon Tools</h2>");
		fpanel.add(title);

		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);

		int row = 0;
		int col = 0;
		mainGrid.setWidget(row, col, addHTML("<b>Connectathon Validations</b>"));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.srcStoresDocValTabLabel, new ToolLauncher(ToolLauncher.srcStoresDocValTabLabel)));
		row++;

		row = 0;
		col++;
		mainGrid.setWidget(row, col, addHTML("<b>Registry Validations</b>"));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryDoThisFirstTabLabel, new ToolLauncher(ToolLauncher.registryDoThisFirstTabLabel)));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryLifecycleTabLabel, new ToolLauncher(ToolLauncher.registryLifecycleTabLabel)));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryFolderHandlingTabLabel, new ToolLauncher(ToolLauncher.registryFolderHandlingTabLabel)));
		row++;

		row = 0;
		col++;
		mainGrid.setWidget(row, col, addHTML("<b>Repository Validations</b>"));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.repositoryDoThisFirstTabLabel, new ToolLauncher(ToolLauncher.repositoryDoThisFirstTabLabel)));
		row++;

		row = 0;
		col++;
		mainGrid.setWidget(row, col, addHTML("<b>Load Test Data</b>"));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(ToolLauncher.registryTestDataTabLabel)));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(ToolLauncher.repositoryTestDataTabLabel)));
		row++;

		row = 0;
		col++;
		mainGrid.setWidget(row, col, addHTML("<b>Tools</b>"));
		row ++;
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.sitesTabLabel, new ToolLauncher(ToolLauncher.sitesTabLabel)));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(ToolLauncher.repositoryTabLabel)));
		row++;
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.dashboardTabLabel, new ToolLauncher(ToolLauncher.dashboardTabLabel)));
		row++;

		fpanel.add(mainGrid);

        return fpanel;
	}

	@Override
	protected void bindUI() {
        // empty here
	}

	@Override
	protected void configureTabView() {
        // empty here
	}

	@Override
	public String getWindowShortName() {
		return "connectathon";
	}

}
