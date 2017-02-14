package gov.nist.toolkit.xdstools2.client.tabs;

import com.baselet.gwt.client.element.DiagramXmlParser;
import com.baselet.gwt.client.view.DrawPanel;
import com.baselet.gwt.client.view.DrawPanelDiagram;
import com.baselet.gwt.client.view.MainView;
import com.baselet.gwt.client.view.widgets.propertiespanel.PropertiesTextArea;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.client.InteractionIdentifierTerm;
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

        MainView mainView = new MainView();

        mainGrid.setWidget(row, 0, mainView);
        row++;
        drawUMLTest(mainView);
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


    public InteractingEntity getInteractionModel() {
        // begin interaction model
        InteractingEntity registryEntity = new InteractingEntity(); // Destination

        origin.setName(null); // Matches with the transactionSettings origin. null=TestClient
        origin.setDescription("Document Consumer - Toolkit");

        registryEntity.setName(getSiteSelection().getName());
        registryEntity.setDescription("Registry - SUT");
        registryEntity.setSourceInteractionLabel("Stored Query (ITI-18)");

        List<InteractionIdentifierTerm> identifierTerms = new ArrayList<>();
        InteractionIdentifierTerm identifierTerm
                = new InteractionIdentifierTerm("$patient_id$", InteractionIdentifierTerm.Operator.EQUALTO, pidTextBox.getValue().trim());
        identifierTerms.add(identifierTerm);
        registryEntity.setInteractionIdentifierTerms(identifierTerms);

        origin.setInteractions(new ArrayList<InteractingEntity>());
        origin.getInteractions().add(registryEntity);

        // end
        return origin;
    }


    public InteractingEntity testIG() {
        InteractingEntity initiator = new InteractingEntity();
        initiator.setName("Toolkit");

        InteractingEntity ig = new InteractingEntity();
        ig.setName("IG");

        InteractingEntity rg1 = new InteractingEntity("rg1");
        InteractingEntity reg1 = new InteractingEntity("reg1");
        InteractingEntity rep1 = new InteractingEntity("rep1");

        rg1.setInteractions(new ArrayList<InteractingEntity>());
        rg1.getInteractions().add(reg1);
        rg1.getInteractions().add(rep1);

        ig.setInteractions(new ArrayList<InteractingEntity>());
        ig.getInteractions().add(rg1);

        InteractingEntity rg2 = new InteractingEntity("rg2");
        InteractingEntity reg2 = new InteractingEntity("reg2");
        InteractingEntity rep2 = new InteractingEntity("rep2");

        rg2.setInteractions(new ArrayList<InteractingEntity>());
        rg2.getInteractions().add(reg2);
        rg2.getInteractions().add(rep2);

        ig.getInteractions().add(rg2);

        initiator.setInteractions(new ArrayList<InteractingEntity>());
        initiator.getInteractions().add(ig);

        return initiator;
    }

    public InteractingEntity testReuseLL() {
        InteractingEntity initiator = new InteractingEntity();
        initiator.setName("Toolkit");

        InteractingEntity ig = new InteractingEntity();
        ig.setName("IG");

        InteractingEntity rg1 = new InteractingEntity("rg1");
        InteractingEntity reg1 = new InteractingEntity("reg1");
        InteractingEntity rep1 = new InteractingEntity("rep1");

        rg1.setInteractions(new ArrayList<InteractingEntity>());
        rg1.getInteractions().add(reg1);
        rg1.getInteractions().add(rep1);

        ig.setInteractions(new ArrayList<InteractingEntity>());
        ig.getInteractions().add(rg1);

        ig.getInteractions().add(rep1);

        return ig;
    }

    public List<InteractingEntity> testTwoActors() {

        List<InteractingEntity> interactingEntityList = new ArrayList<InteractingEntity>();

        InteractingEntity initiator = new InteractingEntity();
        initiator.setName("Tc/Rep");


        InteractingEntity reg = new InteractingEntity("reg");

        initiator.setInteractions(new ArrayList<InteractingEntity>());
        initiator.getInteractions().add(reg);

        InteractingEntity initiator2 = new InteractingEntity();
        initiator2.setName("Tc/DocCons");
        initiator2.setInteractions(new ArrayList<InteractingEntity>());
        initiator2.getInteractions().add(reg);

        interactingEntityList.add(initiator);
        interactingEntityList.add(initiator2);

        return interactingEntityList;

    }

    public void drawUMLTest(MainView mainView) {
        PropertiesTextArea propertiesPanel = new PropertiesTextArea();
        SimpleLayoutPanel palettePanelWrapper;
        final DrawPanel diagramPanel;
        diagramPanel = new DrawPanelDiagram(mainView, propertiesPanel);

        String diagramXmlStr =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<diagram program=\"umlet\" version=\"14.1.0\">\n" +
                "  <zoom_level>10</zoom_level>\n" +
                "  <element>\n" +
                "    <id>UMLNote</id>\n" +
                "    <coordinates>\n" +
                "      <x>400</x>\n" +
                "      <y>400</y>\n" +
                "      <w>120</w>\n" +
                "      <h>70</h>\n" +
                "    </coordinates>\n" +
                "    <panel_attributes>     Note..\n" +
                "     Placeholder\n" +
                "     laurem\n" +
                "ipsum\n" +
                "customelement=\n" +
                "drawArc(5,5,10,50,50,80,false) fg=red bg=red //Parameters (x, y, width, height, start, extent, open)\n" +
                "drawCircle(10,40,3) fg=red bg=red //Parameters (x, y, radius)</panel_attributes>\n" +
                "    <additional_attributes/>\n" +
                "  </element></diagram>";


        diagramPanel.setDiagram(DiagramXmlParser.xmlToDiagram(diagramXmlStr));

    }

}
