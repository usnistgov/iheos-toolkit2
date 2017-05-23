package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.server.shared.command.CommandContext;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

/**
 * Created by onh2 on 11/4/16.
 */
public class RetrieveImagingDocSetRequest extends CommandContext {
    private String transferSyntax;
    private String studyRequest;
    private Uids uids;
    private SiteSpec site;

    public RetrieveImagingDocSetRequest(){}
    public RetrieveImagingDocSetRequest(CommandContext context, SiteSpec site, Uids uids, String studyRequest, String transferSyntax){
        copyFrom(context);
        this.site=site;
        this.uids=uids;
        this.studyRequest=studyRequest;
        this.transferSyntax=transferSyntax;
    }

    public Uids getUids() {
        return uids;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setUids(Uids uids) {
        this.uids = uids;
    }

    public String getStudyRequest() {
        return studyRequest;
    }

    public void setStudyRequest(String studyRequest) {
        this.studyRequest = studyRequest;
    }

    public String getTransferSyntax() {
        return transferSyntax;
    }

    public void setTransferSyntax(String transferSyntax) {
        this.transferSyntax = transferSyntax;
    }
}
