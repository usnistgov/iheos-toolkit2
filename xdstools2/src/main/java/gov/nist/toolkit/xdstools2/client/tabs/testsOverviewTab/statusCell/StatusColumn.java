package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.statusCell;

import com.google.gwt.user.cellview.client.Column;

/**
 * Created by Diane Azais local on 10/13/2015.
 */
public abstract class StatusColumn<T> extends Column<T, String> {


    public StatusColumn() {super(new StatusCell()); }

    }

