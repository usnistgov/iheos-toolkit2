package gov.nist.toolkit.interactionmodel.client;

import java.util.Vector;

/**
 * Created by skb1 on 8/1/2016.
 */
public class InteractionLog {

    static InteractionLog instance;
    static Vector<Interaction> interactions;

    static {
        instance = new InteractionLog();
        interactions = new Vector<Interaction>();
    }

    private InteractionLog() { }

    public static InteractionLog getInstance() { return instance; }

    public boolean add(Interaction interaction)  {
        System.out.println("adding interaction: " + interaction.getFrom() + " to: " + interaction.getTo() + " begin: " + interaction.getTime()) ;
        return interactions.add(interaction);
    }

    public static Vector<Interaction> getInteractions() {
        return interactions;
    }
}
