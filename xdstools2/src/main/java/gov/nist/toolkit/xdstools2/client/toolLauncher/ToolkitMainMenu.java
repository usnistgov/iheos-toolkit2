package gov.nist.toolkit.xdstools2.client.toolLauncher;

/**
 *
 */
public class ToolkitMainMenu implements MainMenu {

    @Override
    public void loadMenu(MenuManagement menuManager) {
        menuManager.clearMainMenu();

        menuManager.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.homeTabLabel, new ToolLauncher(ToolLauncher.homeTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.toolConfigTabLabel, new ToolLauncher(ToolLauncher.toolConfigTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.sitesTabLabel, new ToolLauncher(ToolLauncher.sitesTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.pidFavoritesLabel, new ToolLauncher(ToolLauncher.pidFavoritesLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorControlTabLabel, new ToolLauncher(ToolLauncher.simulatorControlTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.simulatorMessageViewTabLabel, new ToolLauncher(ToolLauncher.simulatorMessageViewTabLabel)));

        // **********************************************************************

        menuManager.addtoMainMenu(addHTML("<h3>Queries & Retrieves</h3>"));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsTabLabel, new ToolLauncher(ToolLauncher.findDocumentsTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsAllParametersTabLabel, new ToolLauncher(ToolLauncher.findDocumentsAllParametersTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findDocumentsByRefIdTabLabel, new ToolLauncher(ToolLauncher.findDocumentsByRefIdTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mpqFindDocumentsTabLabel, new ToolLauncher(ToolLauncher.mpqFindDocumentsTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getDocumentsTabLabel, new ToolLauncher(ToolLauncher.getDocumentsTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getRelatedTabLabel, new ToolLauncher(ToolLauncher.getRelatedTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.findFoldersTabLabel, new ToolLauncher(ToolLauncher.findFoldersTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFoldersTabLabel, new ToolLauncher(ToolLauncher.getFoldersTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getFolderAndContentsTabLabel, new ToolLauncher(ToolLauncher.getFolderAndContentsTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getSubmissionSetTabLabel, new ToolLauncher(ToolLauncher.getSubmissionSetTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.getAllTabLabel, new ToolLauncher(ToolLauncher.getAllTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.documentRetrieveTabLabel, new ToolLauncher(ToolLauncher.documentRetrieveTabLabel)));

//			mainGrid.setWidget(row, col, HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));
        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.imagingDocumentSetRetrieveTabLabel, new ToolLauncher(ToolLauncher.imagingDocumentSetRetrieveTabLabel)));

        // ***************************************************************************
        // Test data


        menuManager.addtoMainMenu(addHTML("<h3>Submit</h3>"));

        menuManager.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.registryTestDataTabLabel, new ToolLauncher(ToolLauncher.registryTestDataTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.repositoryTestDataTabLabel, new ToolLauncher(ToolLauncher.repositoryTestDataTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.link(ToolLauncher.recipientTestDataTabLabel, new ToolLauncher(ToolLauncher.recipientTestDataTabLabel)));


        // ***************************************************************************
        // Tools

        menuManager.addtoMainMenu(addHTML("<h3>Other Tools</h3>"));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.repositoryTabLabel, new ToolLauncher(ToolLauncher.repositoryTabLabel)));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.connectathonTabLabel, new ToolLauncher(ToolLauncher.connectathonTabLabel)));

//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.messageValidatorTabLabel, new ToolLauncher(ToolLauncher.messageValidatorTabLabel)));

        // ***************************************************************************
        // Tests

        menuManager.addtoMainMenu(addHTML("<h3>Testing</h3>"));

        menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.conformanceTestsLabel, new ToolLauncher(ToolLauncher.conformanceTestsLabel)));

//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.mesaTabLabel, new ToolLauncher(ToolLauncher.mesaTabLabel)));
//
//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.igTestsTabLabel, new ToolLauncher(ToolLauncher.igTestsTabLabel)));
//
//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rgTestsTabLabel, new ToolLauncher(ToolLauncher.rgTestsTabLabel)));

//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.iigTestsTabLabel, new ToolLauncher(ToolLauncher.iigTestsTabLabel)));

//      menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rigTestsTabLabel, new ToolLauncher(ToolLauncher.rigTestsTabLabel)));

//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.idsTestsTabLabel, new ToolLauncher(ToolLauncher.idsTestsTabLabel)));

//		menuManager.addtoMainMenu(HyperlinkFactory.launchTool(ToolLauncher.rsnaedgeTestsTabLabel, new ToolLauncher(ToolLauncher.rsnaedgeTestsTabLabel)));



    }

    }

}
