package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.cellview.client.Column;

/**
 * Created by Diane Azais local on 10/13/2015.
 */
public abstract class TestButtonsColumn<T> extends Column<T, String> {

    public TestButtonsColumn() {
        super(new TestButtonsCell());
    }

}

