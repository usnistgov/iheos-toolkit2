package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import com.google.gwt.user.client.ui.SimplePanel;
import gov.nist.toolkit.interactiondiagram.client.widgets.InteractionDiagram;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.SetSutInitiatedTransactionInstanceCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.SetSutInitiatedTransactionInstanceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class InteractionDiagramDisplay extends FlowPanel {
    final private static String viewDiagramLabel = "&boxplus;View Interaction Sequence";
    final private static String hideDiagramLabel = "&boxminus;Hide Interaction Sequence";

    private HTML diagramCtl = new HTML(viewDiagramLabel);
    private SimplePanel contentPanel = new SimplePanel();

    TestOverviewDTO testOverviewDTO;

    String sessionName;
    SiteSpec siteSpec;
    String pid;
    ActorOptionConfig actorOption;
    String sutSystemName;


    public InteractionDiagramDisplay(TestOverviewDTO testResultDTO, String sessionName, SiteSpec siteSpec, String sutSystemName, ActorOptionConfig actorOption, String pid) {

        diagramCtl.addStyleName("iconStyle");
        diagramCtl.addStyleName("inlineLink");
        diagramCtl.addClickHandler(new ViewDiagramClickHandler());
        contentPanel.setVisible(false); // Hidden by default

        setTestOverviewDTO(testResultDTO);
        setSessionName(sessionName);
        setSiteSpec(siteSpec);
        setSutSystemName(sutSystemName);
        setActorOption(actorOption);
        setPid(pid);

    }

    private class ViewDiagramClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            contentPanel.setVisible(!contentPanel.isVisible());
            if (contentPanel.isVisible()) {
                diagramCtl.setHTML(hideDiagramLabel);
            } else {
                diagramCtl.setHTML(viewDiagramLabel);
            }
        }
    }

    public InteractionDiagramDisplay render() {
        if (getTestOverviewDTO()!=null) {
            try {
                final InteractionDiagram diagram = new InteractionDiagram(ClientUtils.INSTANCE.getEventBus(), getTestOverviewDTO(), sessionName, siteSpec, sutSystemName, ActorType.findActor(getActorOption().getActorTypeId()).getName());
                if (diagram != null) {

                        clear();

                        boolean hasSutInitiatedTrans = false;
                        for (InteractingEntity ie : diagram.getEntityList()) {
                            if (ie.hasSutInitiatedTransactions()) {
                                hasSutInitiatedTrans = true;
                                break;
                            }

                        }

                        if (pid != null && getTestOverviewDTO().isRun() && hasSutInitiatedTrans) {
                            new SetSutInitiatedTransactionInstanceCommand() {
                                @Override
                                public void onComplete(List<InteractingEntity> result) {
                                    diagram.setEntityList(result);
                                    diagram.draw();
                                    if ((diagram!=null && diagram.hasMeaningfulDiagram())) {
                                        add(diagramCtl);
                                        add(contentPanel);
                                        contentPanel.add(diagram);
                                    }
                                }
                            }.run(new SetSutInitiatedTransactionInstanceRequest(ClientUtils.INSTANCE.getCommandContext(), diagram.getEntityList(), SimIdFactory.simIdBuilder(getSiteSpec().getName()), getPid()));
                        } else {
                            diagram.draw();
                            if ((diagram!=null && diagram.hasMeaningfulDiagram())) {
                                add(diagramCtl);
                                add(contentPanel);
                                contentPanel.add(diagram);
                            }
                        }
                    }


            } catch (Exception ex) {
                add(new HTML("<!-- Sequence Diagram Error: "
                        + ex.toString()
                        + " -->"));
            }
        }

        return this;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }

    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
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

    public ActorOptionConfig getActorOption() {
        return actorOption;
    }

    public void setActorOption(ActorOptionConfig actorOption) {
        this.actorOption = actorOption;
    }


    public InteractionDiagramDisplay copy() {
        TestOverviewDTO testOverviewDTO = new TestOverviewDTO();
        testOverviewDTO.setSectionNames(new ArrayList<String>());
        testOverviewDTO.setTestInstance(getTestOverviewDTO().getTestInstance());
        testOverviewDTO.setDescription(getTestOverviewDTO().getDescription());
        testOverviewDTO.setTitle(getTestOverviewDTO().getTitle());
        testOverviewDTO.setRun(getTestOverviewDTO().isRun());
        testOverviewDTO.setPass(getTestOverviewDTO().isPass());

       InteractionDiagramDisplay diagramDisplay =  new InteractionDiagramDisplay(testOverviewDTO, getSessionName(), getSiteSpec(), getSutSystemName(), getActorOption(), getPid());
       return diagramDisplay;
    }

    public String getSutSystemName() {
        return sutSystemName;
    }

    public void setSutSystemName(String sutSystemName) {
        this.sutSystemName = sutSystemName;
    }
}
