package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when there is a change to the test sessions.
 * Can carry one of three changes:
 * <ul>
 *     <li>delete;</li>
 *     <li>display;</li>
 *     <li>selection change.</li>
 * </ul>
 * @see ChangeType
 * @see TestSessionChangedEventHandler
 */
public class TestSessionChangedEvent extends GwtEvent<TestSessionChangedEvent.TestSessionChangedEventHandler> {
    public static final Type<TestSessionChangedEventHandler> TYPE = new Type<>();

    private ChangeType changeType;
    private String value;

    /**
     * Event constructor.
     * @param changeType type of change made to the test session.
     * @param value name of the test session affected by the change.
     */
    public TestSessionChangedEvent(ChangeType changeType, String value) {
        this.changeType = changeType;
        this.value = value;
    }

    @Override
    public Type<TestSessionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getValue() {
        return value;
    }

    @Override
    protected void dispatch(TestSessionChangedEventHandler testSessionChangedEventHandler) {
        testSessionChangedEventHandler.onTestSessionChanged(this);
    }

    /**
     * Enumerated class that list the different type of changes that can be made to a test session.
     */
    public enum ChangeType {ADD, DELETE, SELECT}

    public interface TestSessionChangedEventHandler extends EventHandler {

        void onTestSessionChanged(TestSessionChangedEvent event);
    }
}
