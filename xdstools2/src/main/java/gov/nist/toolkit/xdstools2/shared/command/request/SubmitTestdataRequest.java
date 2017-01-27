package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actortransaction.shared.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/4/16.
 */
public class SubmitTestdataRequest extends CommandContext{
    private SiteSpec site;
    private String dataSetName;
    private String pid;

    public SubmitTestdataRequest(){}
    public SubmitTestdataRequest(CommandContext context, SiteSpec site, String datasetName, String pid){
        copyFrom(context);
        this.site=site;
        this.dataSetName=datasetName;
        this.pid=pid;
    }

    public String getPid() {
        return pid;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }
}
