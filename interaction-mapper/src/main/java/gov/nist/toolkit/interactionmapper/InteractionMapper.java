package gov.nist.toolkit.interactionmapper;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.client.Interaction;
import gov.nist.toolkit.interactionmodel.client.InteractionIdentifierTerm;
import gov.nist.toolkit.interactionmodel.client.InteractionLog;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InteractionMapper {
    private final static Logger logger = Logger.getLogger(InteractionMapper.class);

    public InteractingEntity map(InteractingEntity model) throws Exception {

        if (model==null
                || ((model!=null && model.getBegin()==null) || (model!=null && model.getEnd()==null))) {
           throw new Exception("Null argument. Please check model instance, begin date, or end date.");
        }

        InteractingEntity actualInteraction = new InteractingEntity();
        List<Interaction> interactionsLog = filterByTimeRange(model.getBegin(),model.getEnd());
        if (interactionsLog!=null) {
            getStatus(model, interactionsLog); // TODO: not sure if the claimed status is reflected back to the main singleton holding the Vector??
        }

       return model;
    }

    InteractingEntity getStatus(InteractingEntity parent, List<Interaction> interactionLog) {
        // Iterate the model
        for (InteractingEntity child : parent.getInteractions())  {
            Iterator<Interaction> it = interactionLog.iterator();
            while (it.hasNext()) {
                Interaction interaction = it.next();
                if (!interaction.isClaimed()) {
                    if ((interaction.getFrom() == null && parent.getName() == null)
                            || (interaction.getFrom() != null && interaction.getFrom().equals(parent.getName()))
                            && (interaction.getTo() != null && interaction.getTo().equals(child.getName()))) {

                        Map<String, String> params = interaction.getParams();
                        List<InteractionIdentifierTerm> identifierTerms = child.getInteractionIdentifierTerms();
                        if (params!=null && identifierTerms!=null) {
                           for (InteractionIdentifierTerm identifierTerm : identifierTerms)  {
                               String val = params.get(identifierTerm.getPropName());
                               if (val!=null) {
                                   if (InteractionIdentifierTerm.Operator.EQUALTO.equals(identifierTerm.getOperator())) {
                                       if (val.equals(identifierTerm.getValues()[0])){
                                           logger.info("found interaction from: " + interaction.getFrom() + " to: " + interaction.getTo() + " status: " + interaction.getStatus().name() + " using identifier: " + identifierTerm.getPropName() + " op: " + identifierTerm.getOperator().name() + " value[0]: " + identifierTerm.getValues()[0] + " sz:" + identifierTerm.getValues().length);
                                           child.setStatus(interaction.getStatus()); // The response status
                                           interaction.setClaimed(true);
                                       }
                                   }
                               }
                           }

                        }

                        if (child.getInteractions()!=null)
                            getStatus(child, interactionLog);
                        if (interaction.isClaimed())
                            break;
                    }
                }
            }

        }
        return parent;
    }

    public List<Interaction> filterByTimeRange(Date begin, Date end) {

        List<Interaction> interactions = new ArrayList<>();
        InteractionLog interactionLog =  InteractionLog.getInstance();
        Iterator<Interaction> it = interactionLog.getInteractions().iterator();

        if (it!=null)
            while (it.hasNext()) {
               Interaction interaction = it.next();
                if (!interaction.isClaimed())
                    if (begin.before(interaction.getTime()) &&  end.after(interaction.getTime())) {
                        interactions.add(interaction);
                    }
            }
        return interactions;
    }
}
