package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.interactiondiagram.client.InteractionDiagram;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.client.InteractionIdentifierTerm;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
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
	public String getTabTitle() { return "FindDocs"; }

	@Override
	public String getToolTitle() { return "Find Documents Stored Query"; }

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
	public void run() {
		origin.setBegin(new Date());
		toolkitService.findDocuments(queryBoilerplate.getSiteSelection(), pidTextBox.getValue().trim(), selectOnDemand.getValue(), fdCallback);
	}

	protected AsyncCallback<List<Result>> fdCallback = new AsyncCallback<List<Result>>() {
		@Override
		public void onFailure(Throwable throwable) {
			queryCallback.onFailure(throwable);

			//			TODO: handle interaction onFailure
//			try {
//				origin.setEnd(new Date());
//			} catch (Throwable t){}

		}

		@Override
		public void onSuccess(List<Result> results) {
			try {
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
							InteractionDiagram diagram = new InteractionDiagram(500,500);
							String svg = diagram.draw(interactingEntity,0);
							System.out.println(svg);
							resultPanel.add(new HTML(svg));
						}
					});
				}
				else {
					new PopupMessage("Null origin");
				}
			} catch (Throwable t){ new PopupMessage(t.toString());}

			queryCallback.onSuccess(results);
		}
	};

	@Override
	public String getWindowShortName() {
		return "finddocuments";
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

}
