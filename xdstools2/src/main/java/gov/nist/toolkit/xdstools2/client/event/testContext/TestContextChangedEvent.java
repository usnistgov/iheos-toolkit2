package gov.nist.toolkit.xdstools2.client.event.testContext;

import com.google.gwt.event.shared.GwtEvent;

public class TestContextChangedEvent extends GwtEvent<TestContextChangedEventHandler> {
    public static final Type<TestContextChangedEventHandler> TYPE = new Type<>();

    private String value;

    public TestContextChangedEvent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Type<TestContextChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TestContextChangedEventHandler testContextChangedEventHandler) {
        testContextChangedEventHandler.onTestContextChanged(this);
    }
}
