package gov.nist.toolkit.interactionmodel.server;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.shared.TransactionSequenceNotFoundException;
import gov.nist.toolkit.utilities.xml.Util;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by skb1 on 2/8/2017.
 */

public class InteractionSequences {
    private final static Logger logger = Logger.getLogger(InteractionSequences.class);

    static final ConcurrentHashMap<String,List<InteractingEntity>> interactingEntityMap = new ConcurrentHashMap<>();
    static boolean initialized = false;

    private InteractionSequences() {
    }

    static public void init(File interactionSequencesFile) throws Exception {
        if (!initialized) {
       synchronized (InteractionSequences.class) {
                initialized=true;
                OMElement interactionSequences = Util.parse_xml(interactionSequencesFile);

                if (interactionSequences!=null) {
                    Iterator intSeqIt = interactionSequences.getChildElements();
                    while (intSeqIt.hasNext()) {
                        OMElement intSeqEl = (OMElement) intSeqIt.next();
                        xformSequenceToEntity(intSeqEl);
                    }

                }
            }
       }
    }

    static public String xformSequenceToEntity(OMElement intSeqEl) throws Exception {
        String partName = intSeqEl.getLocalName();
        String sequenceId = null;

        if (partName.equals("InteractionSequence")) {
            sequenceId = intSeqEl.getAttributeValue(new QName("id"));

            Iterator actorIt =  intSeqEl.getChildElements();
            List<InteractingEntity> ies = new ArrayList<>();
            while (actorIt.hasNext()) {
                OMElement actorEl = (OMElement)actorIt.next();
                ies.add(xformSequenceToEntity(actorEl,null,null));
            }
            interactingEntityMap.put(sequenceId,ies);
        }

        return sequenceId;

    }

    static InteractingEntity xformSequenceToEntity(OMElement actorEl, InteractingEntity originActorIe, String srcTransactionLabel) {

        InteractingEntity actorIe = null;
           String actorElLocalName = actorEl.getLocalName();
           if ("Actor".equals(actorElLocalName))  {
               actorIe = new InteractingEntity();
               String actorRole = actorEl.getAttributeValue(new QName("role"));
               actorIe.setRole(actorRole);
               String actorProvider = actorEl.getAttributeValue(new QName("provider"));
               actorIe.setProvider(actorProvider);
               String actorName = actorEl.getAttributeValue(new QName("name"));
               if (actorName!=null) {
                actorIe.setName(actorName);
               } else {
                   actorIe.setName(actorProvider);
               }

               if (srcTransactionLabel!=null) {
                  actorIe.setSourceInteractionLabel(srcTransactionLabel);
               }

               Iterator transactionIt = actorEl.getChildElements();
               List<InteractingEntity> interactingEntities = new ArrayList<>();

               while (transactionIt.hasNext()) {
                  OMElement tranEl = (OMElement)transactionIt.next();
                   String tranEllLocalName = tranEl.getLocalName();
                   if ("Transaction".equals(tranEllLocalName)) {
                       String tranName = tranEl.getAttributeValue(new QName("type"));
                       Iterator destinationEls = tranEl.getChildElements();
                       while (destinationEls.hasNext()) {
                        OMElement destEl = (OMElement)destinationEls.next();

                           String destElLocalName = destEl.getLocalName();
                           if ("Actor".equals(destElLocalName))  {
                                interactingEntities.add(xformSequenceToEntity(destEl,actorIe,tranName));
                           }
                       }
                       if (interactingEntities.size()>0) {
                          actorIe.setInteractions(interactingEntities);
                       }

                   }
               }
           }
       return actorIe;

    }

    public static List<InteractingEntity> getInteractionSequenceById(String sequenceId) throws TransactionSequenceNotFoundException {
        if (getSequencesMap()!=null && sequenceId!=null) {
            if (!getSequencesMap().containsKey(sequenceId)) {
                throw new TransactionSequenceNotFoundException(sequenceId + " is not found in the interaction sequence mapping (check InteractionSequences.xml file).", sequenceId);
            }
            return getSequencesMap().get(sequenceId);
        } return null;
    }

    public static Map<String, List<InteractingEntity>> getSequencesMap() {
        return interactingEntityMap;
    }

}
