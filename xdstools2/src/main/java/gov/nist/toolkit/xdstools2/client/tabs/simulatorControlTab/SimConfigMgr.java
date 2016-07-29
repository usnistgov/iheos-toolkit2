package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;


import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;



/**
 * Manages the content of a single Simulator on the screen
 * @author bill
 *
 */
public class SimConfigMgr extends BaseSimConfigMgr {
    /**
     *
     */
    SimConfigMgr(SimulatorControlTab simulatorControlTab, VerticalPanel panel, SimulatorConfig config, String testSession) {
        super(simulatorControlTab, panel, config, testSession);
    }

    @Override
    public void displayBasicSimulatorConfig() {
        super.displayBasicSimulatorConfig();
    }

<<<<<<< HEAD
=======
    void displayInPanel() {
        tbl.clear();
        int row = 0;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator Type"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getActorTypeFullName()));

        row++;

        tbl.setWidget(row, 0, HtmlMarkup.html("Simulator ID"));
        tbl.setWidget(row, 1, HtmlMarkup.html(config.getId().toString()));

        row++;

        for (SimulatorConfigElement ele : config.getElements()) {

            // String
            if (ele.isString()) {
                if (ele.isEditable()) {
                    new ConfigEditBox(ele, tbl, row);
                } else {
                    new ConfigTextDisplayBox(ele, tbl, row);
                }
                row++;
            }

            // Boolean
            else if (ele.isBoolean()) {
                new ConfigBooleanBox(ele, tbl, row);
                row++;
            }

            // Selecting RGs for the IG
            else if (SimulatorProperties.respondingGateways.equals(ele.name)) {
                final SimulatorConfigElement configEle = ele;
                HorizontalPanel rgBoxes = new HorizontalPanel();
                final RGSelectionPresenter rgSelectionPresenter = new RGSelectionPresenter(simulatorControlTab.toolkitService, configEle.asList(), rgBoxes);
                tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
                tbl.setWidget(row, 1, rgBoxes);
                saveButton.addClickHandler(
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent clickEvent) {
                                configEle.setValue(rgSelectionPresenter.getSelected());
//                                config.updateDocTypeSelection();
//                                saveSimConfig();
                            }
                        }
                );
                row++;
            }
            
            // Selecting RIGs for the IIG
            else if (SimulatorProperties.respondingImagingGateways.equals(ele.name)) {
               final SimulatorConfigElement configEle = ele;
               HorizontalPanel rigBoxes = new HorizontalPanel();
               final RigSelectionPresenter rigSelectionPresenter = new RigSelectionPresenter(simulatorControlTab.toolkitService, configEle.asList(), rigBoxes);
               tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
               tbl.setWidget(row, 1, rigBoxes);
               saveButton.addClickHandler(
                       new ClickHandler() {
                           @Override
                           public void onClick(ClickEvent clickEvent) {
                               configEle.setValue(rigSelectionPresenter.getSelected());
                           }
                       }
               );
               row++;
            }
            
            // Selecting IDS for the RG
            else if (SimulatorProperties.imagingDocumentSources.equals(ele.name)) {
               final SimulatorConfigElement configEle = ele;
               HorizontalPanel idsBoxes = new HorizontalPanel();
               final IDSSelectionPresenter idsSelectionPresenter = new IDSSelectionPresenter(simulatorControlTab.toolkitService, configEle.asList(), idsBoxes);
               tbl.setWidget(row, 0, HtmlMarkup.html(ele.name));
               tbl.setWidget(row, 1, idsBoxes);
               saveButton.addClickHandler(
                  new ClickHandler() {
                      @Override
                      public void onClick(ClickEvent clickEvent) {
                          configEle.setValue(idsSelectionPresenter.getSelected());
                      }
                  }
               );
               row++;
           }
>>>>>>> feature/GatewayTool-I

    @Override
    public void displayInPanel() {
        super.displayInPanel();

        addTable(getTbl());

        addSaveHandler();
    }
}
