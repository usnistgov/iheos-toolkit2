package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

public class ConnectathonTab extends GenericQueryTab {

	public ConnectathonTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "Connectathon", select);
		addToolHeader(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Connectathon Tools</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);
		
		int row = 0;
		int col = 0;
		
		mainGrid.setWidget(row, col, addHTML("<b>Connectathon Validations</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.srcStoresDocValTabLabel, new ToolLauncher(container, ToolLauncher.srcStoresDocValTabLabel)));
		row++;

        row = 0;
		col = 1;
		
		mainGrid.setWidget(row, col, addHTML("<b>Registry Validations</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryDoThisFirstTabLabel, new ToolLauncher(container, ToolLauncher.registryDoThisFirstTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryLifecycleTabLabel, new ToolLauncher(container, ToolLauncher.registryLifecycleTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryFolderHandlingTabLabel, new ToolLauncher(container, ToolLauncher.registryFolderHandlingTabLabel)));
		row++;
		
		row = 0;
		col = 2;
		
		mainGrid.setWidget(row, col, addHTML("<b>Repository Validations</b>"));
		row++;
				
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.repositoryDoThisFirstTabLabel, new ToolLauncher(container, ToolLauncher.repositoryDoThisFirstTabLabel)));
		row++;
		
		row = 0;
		col = 3;
		
		mainGrid.setWidget(row, col, addHTML("<b>Load Test Data</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(container, ToolLauncher.registryTestDataTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(container, ToolLauncher.repositoryTestDataTabLabel)));
		row++;
		
		row = 0;
		col = 4;
		
		mainGrid.setWidget(row, col, addHTML("<b>Tools</b>"));
		row ++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.sitesTabLabel, new ToolLauncher(myContainer, ToolLauncher.sitesTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(myContainer, ToolLauncher.repositoryTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.link(ToolLauncher.dashboardTabLabel, new ToolLauncher(container, ToolLauncher.dashboardTabLabel)));
		row++;

		topPanel.add(mainGrid);

	}


	public String getWindowShortName() {
		return "connectathon";
	}
	
//	public void onTabLoad(TabContainer container, boolean select) {
//	}




}
