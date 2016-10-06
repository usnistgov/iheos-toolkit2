package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.client.InteractionIdentifierTerm;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.AbstractTool;

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

    @Override
    public void initTool() {
        int row = 0;

        selectOnDemand = new CheckBox();
        selectOnDemand.setText("Include On-Demand DocumentEntries");
        mainGrid.setWidget(row, 0, selectOnDemand);
        row++;

        requirePatientId();
        declareTransactionTypes(transactionTypes);
    }

    @Override
    protected void bindUI() {
        addOnTabSelectionRedisplay();
    }

    @Override
    public String getWindowShortName() {
        return "finddocuments";
    }

    @Override
    public String getTabTitle() { return "FindDocs"; }

    @Override
    public String getToolTitle() { return "Find Documents Stored Query"; }


    @Override
    public void run() {
        origin.setBegin(new Date());
        getToolkitServices().findDocuments(queryBoilerplate.getSiteSelection(), pidTextBox.getValue().trim(), selectOnDemand.getValue(), fdCallback);
    }

    protected AsyncCallback<List<Result>> fdCallback = new AsyncCallback<List<Result>>() {
        @Override
        public void onFailure(Throwable throwable) {
            queryCallback.onFailure(throwable);
        }

		@Override
		public void onSuccess(List<Result> results) {
			queryCallback.onSuccess(results);
//			try {
				/*
				if (getInteractionModel()!=null) {
					getInteractionModel().setEnd(new Date());

					toolkitService.getInteractionFromModel(getInteractionModel(), new AsyncCallback<InteractingEntity>() {
						@Override
						public void onFailure(Throwable throwable) {
							String mapMsg = "mapping failed!";
							new PopupMessage(mapMsg);
						}

						@Override
						public void onSuccess(InteractingEntity interactingEntity) {
							String mapMsg = "mapping was successful!!" + " return is null? " + (interactingEntity==null) + " desc: " + interactingEntity.getInteractions().get(0).getName();
							new PopupMessage(mapMsg + " interaction status: " + interactingEntity.getInteractions().get(0).getStatus().name());

						}
					});
				}
				else {
					new PopupMessage("Null origin");
				}

				InteractionDiagram diagram = new InteractionDiagram(testTwoActors(), 500,900);
//							Element svg = diagram.draw(interactingEntity,0);
//							Element svg = diagram.draw(testIG(),0);
//							Element svg = diagram.draw(testReuseLL(),0);
//							System.out.println(svg);
				resultPanel.display(new HTML("<p style='font-weight:bold'>Interaction Sequence:</p>"));
				resultPanel.display(diagram);
				*/
//			} catch (Throwable t){ new PopupMessage(t.toString());}

		}
	};

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

}
