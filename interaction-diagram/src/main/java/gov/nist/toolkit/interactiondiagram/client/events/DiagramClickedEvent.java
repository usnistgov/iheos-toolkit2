package gov.nist.toolkit.interactiondiagram.client.events;

import com.google.gwt.event.shared.GwtEvent;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.results.client.TestInstance;

/**
 * Created by skb1 on 8/31/2016.
 */
public class DiagramClickedEvent extends GwtEvent<DiagramPartClickedEventHandler> {
    public static final Type<DiagramPartClickedEventHandler> TYPE = new Type<DiagramPartClickedEventHandler>();
    TestInstance testInstance;
    InteractionDiagram.DiagramPart part;

    @Override
    public Type<DiagramPartClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramPartClickedEventHandler handler) {
        handler.onClicked(getTestInstance(),getPart());
    }

    public DiagramClickedEvent(TestInstance testInstance, InteractionDiagram.DiagramPart part) {
        setTestInstance(testInstance);
        setPart(part);
    }

    public InteractionDiagram.DiagramPart getPart() {
        return part;
    }

    public void setPart(InteractionDiagram.DiagramPart part) {
        this.part = part;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }
}
