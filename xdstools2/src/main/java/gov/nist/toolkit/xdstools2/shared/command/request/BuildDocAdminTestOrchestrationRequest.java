package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.services.client.DocAdminOrchestrationRequest;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class BuildDocAdminTestOrchestrationRequest extends CommandContext {
    private DocAdminOrchestrationRequest docAdminOrchestrationRequest;

    public BuildDocAdminTestOrchestrationRequest(){}
    public BuildDocAdminTestOrchestrationRequest(CommandContext context, DocAdminOrchestrationRequest docAdminOrchestrationRequest){
        copyFrom(context);
        this.docAdminOrchestrationRequest=docAdminOrchestrationRequest;
    }

    public DocAdminOrchestrationRequest getDocAdminOrchestrationRequest() {
        return docAdminOrchestrationRequest;
    }

    public void setDocAdminOrchestrationRequest(DocAdminOrchestrationRequest docAdminOrchestrationRequest) {
        this.docAdminOrchestrationRequest = docAdminOrchestrationRequest;
    }
}
