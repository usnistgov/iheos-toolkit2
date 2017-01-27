package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.actortransaction.shared.TransactionInstance;


/**
 *
 */
public class SimLog extends Place {

    public static class Tokenizer implements PlaceTokenizer<SimLog> {

        @Override
        public SimLog getPlace(String s) {
            return new SimLog(s);
        }

        @Override
        public String getToken(SimLog simLog) {
            return simLog.simIdString + "/" + simLog.actor + "/" + simLog.trans + "/" + simLog.messageId;
        }
    }

    private String simIdString;
    private String actor;
    private String trans;
    private String messageId;
    private boolean valid = false;

    public SimLog(TransactionInstance ti) {
        simIdString = ti.simId;
        actor = ti.actorType.getShortName();
        trans = ti.trans;
        messageId = ti.messageId;
    }

    private SimLog(String s) {
        String[] parts = s.split("/");
        if (parts.length == 4) {
            simIdString = parts[0].trim();
            actor = parts[1].trim();
            trans = parts[2].trim();
            messageId = parts[3].trim();
            valid = true;
        }
    }

    String getSimIdString() {
        return simIdString;
    }

    public void setSimIdString(String simIdString) {
        this.simIdString = simIdString;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimLog simLog = (SimLog) o;

        if (valid != simLog.valid) return false;
        if (simIdString != null ? !simIdString.equals(simLog.simIdString) : simLog.simIdString != null) return false;
        if (actor != null ? !actor.equals(simLog.actor) : simLog.actor != null) return false;
        if (trans != null ? !trans.equals(simLog.trans) : simLog.trans != null) return false;
        return messageId != null ? messageId.equals(simLog.messageId) : simLog.messageId == null;

    }

    @Override
    public int hashCode() {
        int result = simIdString != null ? simIdString.hashCode() : 0;
        result = 31 * result + (actor != null ? actor.hashCode() : 0);
        result = 31 * result + (trans != null ? trans.hashCode() : 0);
        result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }
}
