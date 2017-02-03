package gov.nist.toolkit.interactiondiagram.client.events;


import com.google.gwt.event.shared.EventHandler;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.TestInstance;

/**
 * Created by skb1 on 8/31/2016.
 */
public interface DiagramPartClickedEventHandler extends EventHandler {
    void onClicked(TestInstance testInstance, InteractionDiagram.DiagramPart part);

}
