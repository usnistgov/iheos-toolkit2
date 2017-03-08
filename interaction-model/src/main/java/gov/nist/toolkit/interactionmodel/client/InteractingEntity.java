package gov.nist.toolkit.interactionmodel.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by skb1 on 8/1/2016.
 */
public class InteractingEntity implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String role;
    String provider;
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
    List<InteractingEntity> interactions;

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
    List<String> errors;

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
        ERROR_EXPECTED,
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

    public List<InteractingEntity> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<InteractingEntity> interactions) {
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

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        try {
            return seq(this, new StringBuilder()).toString();
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    private StringBuilder seq(InteractingEntity parent, StringBuilder sb) throws Exception {

        if (sb==null) throw new Exception("Null StringBuilder object!");

        sb.append("" + parent.getRole() + ":" + parent.getName() + " ------> ");

        if (parent.getInteractions()!=null)  {
            for (InteractingEntity child : parent.getInteractions()) {

                sb.append("" + child.getRole() + ":" + child.getName());
                sb.append("\n");

                if (child.getInteractions()!=null) {
                    seq(child,sb);
                }
            }
        }

        return sb;
    }

    public void setPlaceholders(InteractingEntity parent, Map<String,String> map) {

        if (parent==null) parent = this;

        setProviderPlaceholderValue(parent, map);

        if (parent.getInteractions()!=null)  {
            for (InteractingEntity child : parent.getInteractions()) {

                setProviderPlaceholderValue(child,map);

                if (child.getInteractions()!=null) {
                    setPlaceholders(child,map);
                }
            }
        }

    }

    private void setProviderPlaceholderValue(InteractingEntity entity, Map<String, String> map) {
        if (map.get("SystemUnderTest")!=null && "SystemUnderTest".equals(entity.getProvider())) {
                entity.setName(map.get("SystemUnderTest"));
        } else if (map.get("Simulator")!=null && "Simulator".equals(entity.getProvider())) {
                entity.setName(map.get("Simulator"));
        }
    }

    public InteractingEntity copy() {
       InteractingEntity newIe = new InteractingEntity();
       newIe.setStatus(this.getStatus());
        newIe.setBegin(this.getBegin());
        newIe.setEnd(this.getEnd());
        newIe.setDescription(this.getDescription());
        newIe.setDisplayOrder(this.getDisplayOrder());
        newIe.setErrors(this.getErrors());
        newIe.setInteractionIdentifierTerms(this.getInteractionIdentifierTerms());
        newIe.setName(this.getName());
        newIe.setProvider(this.getProvider());
        newIe.setRole(this.getRole());
        newIe.setSourceInteractionLabel(this.getSourceInteractionLabel());

        if (this.getInteractions()!=null) {
            newIe.setInteractions(new ArrayList<InteractingEntity>());
           for (InteractingEntity child : this.getInteractions())  {
               newIe.getInteractions().add(child.copy());
           }
        }

        return newIe;
    }

}
