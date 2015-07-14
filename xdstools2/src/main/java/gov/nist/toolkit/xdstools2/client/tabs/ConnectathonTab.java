package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConnectathonTab extends GenericQueryTab {

	public ConnectathonTab() {
		super(new GetDocumentsSiteActorManager());
	}
	

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		
		
		container.addTab(topPanel, "Connectathon", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Connectathon Tools</h2>");
		topPanel.add(title);

		mainGrid = new FlexTable();
		mainGrid.setCellSpacing(20);
		
		int row = 0;
		int col = 0;
		
		mainGrid.setWidget(row, col, addHTML("<b>Connectathon Validations</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.srcStoresDocValTabLabel, new TabLauncher(container, TabLauncher.srcStoresDocValTabLabel)));
		row++;
								
		row = 0;
		col = 1;
		
		mainGrid.setWidget(row, col, addHTML("<b>Registry Validations</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryDoThisFirstTabLabel, new TabLauncher(container, TabLauncher.registryDoThisFirstTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryLifecycleTabLabel, new TabLauncher(container, TabLauncher.registryLifecycleTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryFolderHandlingTabLabel, new TabLauncher(container, TabLauncher.registryFolderHandlingTabLabel)));
		row++;
		
		row = 0;
		col = 2;
		
		mainGrid.setWidget(row, col, addHTML("<b>Repository Validations</b>"));
		row++;
				
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.repositoryDoThisFirstTabLabel, new TabLauncher(container, TabLauncher.repositoryDoThisFirstTabLabel)));
		row++;
		
		row = 0;
		col = 3;
		
		mainGrid.setWidget(row, col, addHTML("<b>Load Test Data</b>"));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.registryTestDataTabLabel, new TabLauncher(container, TabLauncher.registryTestDataTabLabel)));
		row++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.repositoryTestDataTabLabel, new TabLauncher(container, TabLauncher.repositoryTestDataTabLabel)));
		row++;
		
		row = 0;
		col = 4;
		
		mainGrid.setWidget(row, col, addHTML("<b>Tools</b>"));
		row ++;
		
		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.adminTabLabel, new TabLauncher(myContainer, TabLauncher.adminTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(TabLauncher.repositoryTabLabel, new TabLauncher(myContainer, TabLauncher.repositoryTabLabel)));
		row++;

		mainGrid.setWidget(row, col, HyperlinkFactory.link(TabLauncher.dashboardTabLabel, new TabLauncher(container, TabLauncher.dashboardTabLabel)));
		row++;

		topPanel.add(mainGrid);

	}


	public String getWindowShortName() {
		return "connectathon";
	}
	
//	public void onTabLoad(TabContainer container, boolean select) {
//	}




}
