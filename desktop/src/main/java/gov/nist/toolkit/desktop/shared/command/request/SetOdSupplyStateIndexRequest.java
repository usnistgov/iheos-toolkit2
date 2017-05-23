package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.results.client.DocumentEntryDetail;

/**
 * Created by skb1 on 2/16/17.
 */
public class SetOdSupplyStateIndexRequest extends CommandContext {
    DocumentEntryDetail ded;
    SimId oddsSimId;
    int newIdx;

    public SetOdSupplyStateIndexRequest(){}
    public SetOdSupplyStateIndexRequest(CommandContext context, SimId oddsSimId, DocumentEntryDetail ded, int newIdx){
        copyFrom(context);
        this.ded = ded;
        this.oddsSimId = oddsSimId;
        this.newIdx = newIdx;
    }

    public DocumentEntryDetail getDed() {
        return ded;
    }

    public void setDed(DocumentEntryDetail ded) {
        this.ded = ded;
    }

    public SimId getOddsSimId() {
        return oddsSimId;
    }

    public void setOddsSimId(SimId oddsSimId) {
        this.oddsSimId = oddsSimId;
    }

    public int getNewIdx() {
        return newIdx;
    }

    public void setNewIdx(int newIdx) {
        this.newIdx = newIdx;
    }
}
