package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Now that TestInstance is a parameter, this is used as a general utility.
 * Needs renaming later.
 */
public class RegisterAndQueryRequest extends CommandContext{

    private List<Submission> submissions = new ArrayList<>();
//    private String pid;
    private SiteSpec site;
//    private TestInstance testInstance;

    public RegisterAndQueryRequest(){}
    public RegisterAndQueryRequest(CommandContext context, TestInstance testInstance, SiteSpec site, String pid){
        copyFrom(context);
        Submission submission = new Submission();
        submissions.add(submission);
        submission.testInstance = testInstance;
        submission.pid=pid;
        this.site=site;
    }

    public RegisterAndQueryRequest(CommandContext context, SiteSpec site){
        copyFrom(context);
        this.site=site;
    }

    public SiteSpec getSite() {
        return site;
    }

    public void setSite(SiteSpec site) {
        this.site = site;
    }

    public String getPid() {
        return submissions.get(0).pid;
    }

    public void setPid(String pid) {
        submissions.get(0).pid = pid;
    }

    public TestInstance getTestInstance() {
        return submissions.get(0).testInstance;
    }

    public RegisterAndQueryRequest addSubmission(String pid, TestInstance testInstance) {
        Submission submission = new Submission();
        submission.pid = pid;
        submission.testInstance = testInstance;
        submissions.add(submission);
        return this;
    }

    public List<Submission> getSubmissions() { return submissions; }

}
