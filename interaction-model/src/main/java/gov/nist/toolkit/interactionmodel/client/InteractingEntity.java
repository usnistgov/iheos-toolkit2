package gov.nist.toolkit.interactionmodel.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by skb1 on 8/1/2016.
 */
public class InteractingEntity implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String description;
    int id = System.identityHashCode(this);
    /**
     *
    (null if head/parent)
     */
    /**
     * How this entity is being interacted with the Source entity.
     */
    String sourceInteractionLabel;
    ArrayList<InteractingEntity> interactions;

    /**
     *
     InteractionIdentifiers = {PatientId, timestamp}
     */
    List<InteractionIdentifierTerm> interactionIdentifierTerms;
    // TODO: is request status needed here?
    /**
     *  Repsonse status
     */
    INTERACTIONSTATUS status;

    /**
     *(null if leaf)
     * @return
     */
    Date begin;

    /**
     *	(null if leaf)
     * @return
     */
    Date end;
    int displayOrder = 0;

    public static enum INTERACTIONSTATUS {
        COMPLETED,
        ERROR,
        UNKNOWN

    }

    public InteractingEntity() {
    }

    public InteractingEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }


    public String getSourceInteractionLabel() {
        return sourceInteractionLabel;
    }

    public void setSourceInteractionLabel(String sourceInteractionLabel) {
        this.sourceInteractionLabel = sourceInteractionLabel;
    }

    public ArrayList<InteractingEntity> getInteractions() {
        return interactions;
    }

    public void setInteractions(ArrayList<InteractingEntity> interactions) {
        this.interactions = interactions;
    }

    public List<InteractionIdentifierTerm> getInteractionIdentifierTerms() {
        return interactionIdentifierTerms;
    }

    public void setInteractionIdentifierTerms(List<InteractionIdentifierTerm> interactionIdentifierTerms) {
        this.interactionIdentifierTerms = interactionIdentifierTerms;
    }

    public INTERACTIONSTATUS getStatus() {
        return status;
    }

    public void setStatus(INTERACTIONSTATUS status) {
        this.status = status;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
