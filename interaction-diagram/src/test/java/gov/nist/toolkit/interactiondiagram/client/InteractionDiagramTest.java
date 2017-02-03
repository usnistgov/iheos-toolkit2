package gov.nist.toolkit.interactiondiagram.client;

import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.client.InteractionIdentifierTerm;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by skb1 on 8/12/2016.
 */
@Ignore
public class InteractionDiagramTest /*extends GWTTestCase*/ {

	/*@Override*/
	public String getModuleName() {
		return "gov.nist.toolkit.interactiondiagram.interactionDiagram";
	}

	@Test
	public void testGen() {
		System.out.println("Running...");

		InteractionDiagram diagram = new InteractionDiagram(getFDInteractionModel(),500,500);
		if (diagram==null)
			Assert.fail("Null svg element");
//		System.out.println(svg);

	}

	private InteractingEntity getFDInteractionModel() {
		// begin interaction model
		InteractingEntity origin = new InteractingEntity(); //  new InteractingEntity(); // Destination
		InteractingEntity registryEntity = new InteractingEntity(); // Destination

		origin.setName(null); // Matches with the transactionSettings origin. null=TestClient
		origin.setDescription("Document Consumer - Toolkit");

		registryEntity.setName("Registry site");
		registryEntity.setDescription("Registry - SUT");
		registryEntity.setSourceInteractionLabel("Stored Query (ITI-18)");

		List<InteractionIdentifierTerm> identifierTerms = new ArrayList<>();
		InteractionIdentifierTerm identifierTerm
				= new InteractionIdentifierTerm("$patient_id$", InteractionIdentifierTerm.Operator.EQUALTO, "pid");
		identifierTerms.add(identifierTerm);
		registryEntity.setInteractionIdentifierTerms(identifierTerms);

		origin.setInteractions(new ArrayList<InteractingEntity>());
		origin.getInteractions().add(registryEntity);

		// end
		return origin;
	}

}
