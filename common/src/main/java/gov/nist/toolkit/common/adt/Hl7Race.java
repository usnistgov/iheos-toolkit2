/*
 * Hl7Race.java
 *
 * Created on August 15, 2005, 10:16 AM
 *
 */

package gov.nist.toolkit.common.adt;

/**
 *
 * @author mccaffrey
 */
public class Hl7Race {
    
    
    private String parent = null;   //  uuid link to record -- not the patient's biological parent                                    
        
    private String race = null;
    
    public Hl7Race() {}        
    
    /** Creates a new instance of Hl7Race */
    public Hl7Race(String parent) {
        this.setParent(parent);
    }
    
    public Hl7Race(String parent, String race) {
        this.setParent(parent);
        this.setRace(race);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }
    
}
