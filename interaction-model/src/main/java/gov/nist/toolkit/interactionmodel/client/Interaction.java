package gov.nist.toolkit.interactionmodel.client;

/**
 * Created by skb1 on 8/3/2016.
 */

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by skb1 on 8/1/2016.
 */
public class Interaction implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;
    public enum Direction {
        RESPONDING,
        INITIATING
    };
    Date time;
    Direction direction;
    String from;
    String to;
    String messageId;
    boolean claimed;
    InteractingEntity.INTERACTIONSTATUS status;

    public Interaction() {
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public InteractingEntity.INTERACTIONSTATUS getStatus() {
        return status;
    }

    public void setStatus(InteractingEntity.INTERACTIONSTATUS status) {
        this.status = status;
    }
}
