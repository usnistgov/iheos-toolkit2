package gov.nist.toolkit.interactionmodel.server;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.Iterator;

/**
 * Created by skb1 on 2/8/2017.
 */

// TODO: make this a singleton initialized on startup?
public class InteractionSequence {
    File interactionSequenceFile;
    private final static Logger logger = Logger.getLogger(InteractionSequence.class);

    public InteractionSequence(File interactionSequenceFile) {
        this.interactionSequenceFile = interactionSequenceFile;
    }

    public File getInteractionSequenceFile() {
        return interactionSequenceFile;
    }

    public void setInteractionSequenceFile(File interactionSequenceFile) {
        this.interactionSequenceFile = interactionSequenceFile;
    }


    public InteractingEntity getInteractionEntity() {
        InteractingEntity interactingEntity = new InteractingEntity();

        try {
            OMElement interactionSequences = Util.parse_xml(getInteractionSequenceFile());

            Iterator elements = interactionSequences.getChildElements();
            while (elements.hasNext()) {
                OMElement part = (OMElement) elements.next();
                String part_name = part.getLocalName();

                if (part_name.equals("InteractionSequence")) {

                    getSequence(part);
                }
            }
            } catch (XdsInternalException xie){
                logger.warn(xie.toString(), xie);
                return null;
            }

        return interactingEntity;
    }

    // TODO: This needs to be called directly from TestStep Log since it may have an embedded InteractionSequence
    public void getSequence(OMElement part) {
        String transaction = part.getAttributeValue(new QName("type"));
        // TODO: Need a map of sequences by transaction as key??
    }
}
