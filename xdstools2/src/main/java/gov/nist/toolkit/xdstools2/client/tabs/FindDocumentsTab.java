package gov.nist.toolkit.xdstools2.client.tabs;

import com.baselet.gwt.client.element.DiagramXmlParser;
import com.baselet.gwt.client.view.DrawPanel;
import com.baselet.gwt.client.view.MainView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.command.command.FindDocumentsCommand;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.AbstractTool;
import gov.nist.toolkit.xdstools2.shared.command.request.FindDocumentsRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FindDocumentsTab extends AbstractTool {

    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }

    static CoupledTransactions couplings = new CoupledTransactions();

    CheckBox selectOnDemand;
    InteractingEntity origin = new InteractingEntity(); //  new InteractingEntity(); // Destination
    private Pid patientId;
    private SiteSpec site;
    private boolean onDemand;

    @Override
    public void initTool() {
        int row = 0;

        selectOnDemand = new CheckBox();
        selectOnDemand.setText("Include On-Demand DocumentEntries");
        mainGrid.setWidget(row, 0, selectOnDemand);
        row++;

        requirePatientId();
        declareTransactionTypes(transactionTypes);


        getTabTopPanel().add(new HTML("hello, this is a test."));

        MainView mainView = new MainView();
        mainView.getElement().getStyle().setWidth(600, Style.Unit.PX);
        mainView.getElement().getStyle().setHeight(600, Style.Unit.PX);
        getTabTopPanel().add(  mainView );
        drawUMLTest(mainView);

//        SimpleLayoutPanel sp = new SimpleLayoutPanel();
//        sp.getElement().getStyle().setWidth(100 , Style.Unit.PX);
//        sp.getElement().getStyle().setHeight(100, Style.Unit.PX);
//        sp.add(drawUMLTest(mainView));
//        getTabTopPanel().add(sp);
    }

    @Override
    protected void bindUI() {
//        addOnTabSelectionRedisplay();
    }

    @Override
    public String getWindowShortName() {
        return "finddocuments";
    }

    @Override
    public String getTabTitle() { return "FindDocs"; }

    @Override
    public String getToolTitle() { return "Find Documents Stored Query"; }

    /**
     * run as a utility from another tool
     * @param patientID
     * @param siteSpec
     * @param onDemand
     */
    public void run(Pid patientID, SiteSpec siteSpec, boolean onDemand) {
        this.patientId=patientID;
        this.site=siteSpec;
        this.onDemand=onDemand;
        new FindDocumentsCommand() {
            @Override
            public void onComplete(List<Result> results) {
                queryCallback.onSuccess(results);
                transactionSelectionManager.selectSite(site);
            }
        }.run(new FindDocumentsRequest(getCommandContext(), siteSpec, patientID.asString(), onDemand));
    }

    @Override
    public void run() {
        origin.setBegin(new Date());
        new FindDocumentsCommand(){
            @Override
            public void onComplete(List<Result> results) {
                queryCallback.onSuccess(results);
            }
        }.run(new FindDocumentsRequest(getCommandContext(),queryBoilerplate.getSiteSelection(), pidTextBox.getValue().trim(), selectOnDemand.getValue()));
    }


    public DrawPanel drawUMLTest(MainView mainView) {
//        PropertiesTextArea propertiesPanel = new PropertiesTextArea();
//        SimpleLayoutPanel palettePanelWrapper;
//        final DrawPanel diagramPanel;
//        diagramPanel = new DrawPanelDiagram(mainView, propertiesPanel);

        String diagramXmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<diagram program=\"umlet\" version=\"13.3\">\n" +
                "  <zoom_level>8</zoom_level>\n" +
                "  <element>\n" +
                "    <id>UMLClass</id>\n" +
                "    <coordinates>\n" +
                "      <x>16</x>\n" +
                "      <y>48</y>\n" +
                "      <w>168</w>\n" +
                "      <h>152</h>\n" +
                "    </coordinates>\n" +
                "    <panel_attributes>&lt;&lt;Stereotype&gt;&gt;\n" +
                "Package::FatClass\n" +
                "{Some Properties}\n" +
                "--\n" +
                "-id: Long {composite}\n" +
                "_-ClassAttribute: Long_\n" +
                "--\n" +
                "#Operation(i: int): int\n" +
                "/+AbstractOperation()/\n" +
                "--\n" +
                "Responsibilities\n" +
                "-- Resp1\n" +
                "-- Resp2</panel_attributes>\n" +
                "    <additional_attributes/>\n" +
                "  </element></diagram>"                ;

        mainView.getDiagramPanel().setDiagram(DiagramXmlParser.xmlToDiagram(diagramXmlStr));
        return mainView.getDiagramPanel();


        /*
        mainView.getDiagramPanel().setDiagram(DiagramXmlParser.xmlToDiagram(diagramXmlStr));
        mainView.getDiagramPanel().getCanvas().getWidget().getElement().getStyle().setWidth(100, Style.Unit.PX);
        mainView.getDiagramPanel().getCanvas().getWidget().getElement().getStyle().setHeight(100, Style.Unit.PX);
        return mainView.getDiagramPanel();
        */
    }

}
