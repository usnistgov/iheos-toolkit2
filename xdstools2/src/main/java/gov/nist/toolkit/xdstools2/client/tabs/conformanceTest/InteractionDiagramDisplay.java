package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.SetSutInitiatedTransactionInstanceCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetSutInitiatedTransactionInstanceRequest;

import java.util.List;

/**
 *
 */
public class InteractionDiagramDisplay extends FlowPanel {

    TestOverviewDTO testOverviewDTO;
    String sessionName;
    SiteSpec testTarget;
    String pid;
    ActorOption actorOption;


    public InteractionDiagramDisplay(TestOverviewDTO testResultDTO, String sessionName, SiteSpec testTarget, String sutSystemName, ActorOption actorOption, String pid) {

        setTestOverviewDTO(testResultDTO);
        setSessionName(sessionName);
        setTestTarget(testTarget);
        setPid(pid);
        setActorOption(actorOption);

        try {
            final InteractionDiagram diagram = new InteractionDiagram(ClientUtils.INSTANCE.getEventBus(), testResultDTO, sessionName, testTarget, sutSystemName, ActorType.findActor(getActorOption().getActorTypeId()).getName());
            if (diagram!=null) {

                boolean hasSutInitiatedTrans = false;
                for (InteractingEntity ie : diagram.getEntityList()) {
                   if (ie.hasSutInitiatedTransactions()) {
                       hasSutInitiatedTrans = true;
                       break;
                   }

                }

                if (testResultDTO.isRun() && hasSutInitiatedTrans) {
                    new SetSutInitiatedTransactionInstanceCommand() {
                        @Override
                        public void onComplete(List<InteractingEntity> result) {
                            diagram.setEntityList(result);
                            diagram.draw();
                        }
                    }.run(new SetSutInitiatedTransactionInstanceRequest(ClientUtils.INSTANCE.getCommandContext(), diagram.getEntityList(), new SimId(getTestTarget().getName()), getPid()));
                } else {
                    diagram.draw();
                }


                add(new HTML("<p><b>Interaction Sequence:</b></p>"));
                add(diagram);
                add(new HTML("<br/>"));
            }
        } catch (Exception ex) {
            add(new HTML("<p>Sequence Diagram Error: " + ex.toString()
                    + "</p>"));
        }



    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public SiteSpec getTestTarget() {
        return testTarget;
    }

    public void setTestTarget(SiteSpec testTarget) {
        this.testTarget = testTarget;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public TestOverviewDTO getTestOverviewDTO() {
        return testOverviewDTO;
    }

    public void setTestOverviewDTO(TestOverviewDTO testOverviewDTO) {
        this.testOverviewDTO = testOverviewDTO;
    }

    public ActorOption getActorOption() {
        return actorOption;
    }

    public void setActorOption(ActorOption actorOption) {
        this.actorOption = actorOption;
    }
}
