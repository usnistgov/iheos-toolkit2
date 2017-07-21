package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.user.cellview.client.Column;

/**
 * Created by Diane Azais local on 10/13/2015.
 */
public abstract class CommandsColumn<T> extends Column<T, String> {

    public CommandsColumn() {
        super(new CommandsCell());
    }

}

