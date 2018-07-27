package gov.nist.toolkit.interactiondiagram.client.widgets;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.session.client.logtypes.SectionOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.StepOverviewDTO;
import gov.nist.toolkit.session.client.logtypes.TestOverviewDTO;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skb1 Sunil.Bhaskarla on 8/12/2016.
 */
// TODO: Use a style sheet.
// TODO: Use newer NamedBox class
public class InteractionDiagram extends Composite {
    public static final int NUM_LINES = 3;
    public static final String RGB_RED = "rgb(255,0,0)";
    public static final String RGB_BLUE = "rgb(0,0,255)";
    public static final String RGB_ORANGE = "rgb(255,165,0)";
    public static final int MAX_FIRST_TRAN_REPEAT = 10;

    static final int HALF_CROSS_HEIGHT = 5;
    static final int LINE_HEIGHT = 13;
    static final int LL_BOX_WIDTH = 82;
    static final int LL_BOX_HEIGHT = 50;
    static final int TRANSACTION_PAIR_WIDTH=190;   // request origin | ------ width: 190px ------> | request destination
    static final int MAX_LL_DISPLAY_NAME = 14;

    private int g_depth = 0;
    private int g_x = 0;
    private int g_y = 0;
    private int MAX_LABEL_DISPLAY_LEN = 27;
    private int LL_FEET = 10; // life line feet (extra) lines after the last transaction
    private int ll_margin = 108; // The width of a transaction connector
    private int maxLabelLen = 16;

    private int connection_topmargin = (NUM_LINES * LINE_HEIGHT) + (HALF_CROSS_HEIGHT *2) + 2; // top margin of a transaction
    private int error_box_offset = 30;

    private int diagramHeight = 0;
    private int diagramWidth = 0;

    private OMSVGDocument doc = OMSVGParser.createDocument();
    private OMSVGSVGElement svgsvgElement =  doc.createSVGSVGElement();

    private TestOverviewDTO testOverviewDTO;
    private EventBus eventBus;
    private SiteSpec targetSite;
    private String sutSystemName;
    private String sutActorRoleName;
    private String sessionName;
    private boolean atleastOneSectionWasRun = false;
    /**
     * Transaction is not mappable since there is no matching transaction key in the InteractionSequences.xml file.
     * When this happens, the entire test sequence is not meaningful since at least one transaction is not mappable.
     */
    private boolean hasUnmappableTransaction = false;

    private static final int MAX_TOOLTIPS = 5;
    private static final int HIDE_TOOLTIP_ON_MOUSEOUT = -1;
    private Tooltip tooltip = new Tooltip();
    private List<String> legends = new ArrayList<>();
    private List<InteractingEntity> entityList;

    public enum DiagramPart {
       RequestConnector,
       ResponseConnector
    }

    private static class Tooltip extends PopupPanel {
        private HTML contents;
        private Timer timer;

        public Tooltip() {
            super(true);
            contents = new HTML();
            add(contents);
//            setStyleName(WidgetsSampleBundle.INSTANCE.getCss().tooltip());
        }

        void setContents(List<String> text) {

            contents.setHTML("");

           if (text!=null)  {
              int count = text.size();
               for (int cx=0; cx<count; cx++) {
                  contents.setHTML(contents.getHTML() + "<p>" + text.get(cx) + "</p>");
                   if (cx== MAX_TOOLTIPS) {
                      contents.setHTML(contents.getHTML() + "<p>"+ (count- MAX_TOOLTIPS) +" more...</p>");
                   }
               }
           }
        }

        public void show(int x, int y, final List<String> text, final int delay) {

            if (text==null) return;

            setContents(text);
            setPopupPosition(x, y);
            super.show();

            if (delay>0) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer() {
                    public void run() {
                        Tooltip.this.hide();
                        timer = null;
                    }
                };
                timer.schedule(delay);
            }
        }
    }

    /**
     * Touch points of a life line (LL)
     * Touch point means a contact point either a request/response touching the LL.
     * This is used to draw active portion of the LL as a interaction bounding vertical box.
     */
    class TouchPoint {
        int x_min;
        int y_min;
        int y_max;
        String from;
        String to;
        String lineFillColor;

        public TouchPoint() {
        }

        public int getY_min() {
            return y_min;
        }

        public void setY_min(int y_min) {
            this.y_min = y_min;
        }

        public int getY_max() {
            return y_max;
        }

        public void setY_max(int y_max) {
            this.y_max = y_max;
        }

        public int getX_min() {
            return x_min;
        }

        public void setX_min(int x_min) {
            this.x_min = x_min;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getLineFillColor() {
            return lineFillColor;
        }

        public void setLineFillColor(String lineFillColor) {
            this.lineFillColor = lineFillColor;
        }
    }

    int activity_box_width = 8;

    /**
     * Life line
     */
    class LL {

        int ll_stem_center; // only the x coordinate of the center
        OMSVGGElement llEl; // life line element
        // Leaf node draws each interaction activity box separately
        List<TouchPoint> activityFrames = new ArrayList<TouchPoint>();
        TouchPoint activityRange = new TouchPoint();
        TouchPoint tempTp = new TouchPoint();

        String id;
        String name;
        boolean root;

        public LL() {
        }

        public int getLl_stem_center() {
            return ll_stem_center;
        }

        public void setLl_stem_center(int ll_stem_center) {
            this.ll_stem_center = ll_stem_center;
        }


        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }


        public boolean isRoot() {
            return root;
        }

        public void setRoot(boolean root) {
            this.root = root;
        }

        public TouchPoint getActivityRange() {
            return activityRange;
        }

        public void setActivityRange(TouchPoint activityRange) {
            this.activityRange = activityRange;
        }

        public TouchPoint getTempTp() {
            return tempTp;
        }

        public void setTempTp(TouchPoint tempTp) {
            this.tempTp = tempTp;
        }

        public List<TouchPoint> getActivityFrames() {
            return activityFrames;
        }

        public void setActivityFrames(List<TouchPoint> activityFrames) {
            this.activityFrames = activityFrames;
        }

        public OMSVGGElement getLlEl() {
            return llEl;
        }

        public void setLlEl(OMSVGGElement llEl) {
            this.llEl = llEl;
        }
    }

    List<LL> lls = new ArrayList<LL>();

    public InteractionDiagram() {}

    public InteractionDiagram(InteractingEntity interactingEntity, int diagramHeight, int diagramWidth) {
        setDiagramArea(diagramHeight, diagramWidth);


        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svgsvgElement.getElement());

      initWidget(container);
    }



    public InteractionDiagram(EventBus eventBus, final TestOverviewDTO testOverviewDTO, String sessionName, final SiteSpec target, String sutName, String sutActorName) {
        setEventBus(eventBus);
        setTestOverviewDTO(testOverviewDTO);
        setSessionName(sessionName);
        setTargetSite(target);
        setSutSystemName(sutName);
        setSutActorRoleName(sutActorName);


        setEntityList(getInteractingEntity(testOverviewDTO, target));
        if (getEntityList()==null) {
            return;
        }

        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svgsvgElement.getElement());

        initWidget(container);

    }

    private void setMargins() {
        if (maxLabelLen > MAX_LABEL_DISPLAY_LEN) {
            maxLabelLen = MAX_LABEL_DISPLAY_LEN;
            ll_margin = 108;
            error_box_offset = connection_topmargin - 1;
        }
        else {
            ll_margin = maxLabelLen * 4;
            connection_topmargin -= 3;
            error_box_offset = connection_topmargin - 2;
        }
    }



    String setLabelAndErrors(InteractingEntity entity, SectionOverviewDTO sectionOverviewDTO, String stepName) {
        String label = "";
        if (sectionOverviewDTO.getStepNames().size()>0) {
            label = /*"Section: " +*/ sectionOverviewDTO.getName() + "/" + /*"Step: " + */  stepName;
            StepOverviewDTO step = sectionOverviewDTO.getStep(stepName);
            entity.setErrors(step.getErrors());
            if (entity.getSourceInteractionLabel()==null && step.getTransaction()!=null)
                entity.setSourceInteractionLabel(step.getTransaction());
        } else
            label = sectionOverviewDTO.getName();

        int labelLen = label.length();
        if (labelLen> maxLabelLen)
            maxLabelLen =labelLen;


        entity.setDescription(label);
        return label;
    }

    public static boolean  isFirstTransactionRepeatingTooManyTimes(TestOverviewDTO testOverviewDTO) {
        int repeatCt = 0;
        String firstTransaction = null;

        if (testOverviewDTO == null || testOverviewDTO.getSectionNames() == null) return false;

        List<String> sectionNames = testOverviewDTO.getSectionNames();

        if (sectionNames == null || (sectionNames != null && sectionNames.isEmpty()))
            return false;

        if (sectionNames.size() > 0 && testOverviewDTO.getSections().size() > 0) {
            for (String section : sectionNames) {
                SectionOverviewDTO sectionOverviewDTO = testOverviewDTO.getSectionOverview(section);
                if (sectionOverviewDTO.getStepNames() != null && sectionOverviewDTO.getStepNames().size() > 0) {
                    String stepName = sectionOverviewDTO.getStepNames().get(0);
                    StepOverviewDTO stepOverviewDTO = sectionOverviewDTO.getStep(stepName);

                    // Manage display to avoid very long one-transaction type diagrams
                    if (firstTransaction == null) {
                        firstTransaction = stepOverviewDTO.getTransaction();
                        repeatCt++;
                    } else if (firstTransaction.equals(stepOverviewDTO.getTransaction())) {
                        if (++repeatCt>MAX_FIRST_TRAN_REPEAT)
                            return true;
                    } else {
                        return false; // Break away when the transaction doesn't repeat
                    }


                }
            }
        }
        return false;
    }


    List<InteractingEntity> getInteractingEntity(TestOverviewDTO testResultDTO, SiteSpec targetSite) {
        if (testResultDTO == null || testResultDTO.getSectionNames() == null) return null;

        List<String> sectionNames = testResultDTO.getSectionNames();

        if (sectionNames == null || (sectionNames != null && sectionNames.isEmpty()))
            return null;

        List<InteractingEntity> result = new ArrayList<InteractingEntity>();
        setAtleastOneSectionWasRun(false);


        if (sectionNames.size()>0 && testResultDTO.getSections().size()>0)
         for (String section : sectionNames) {
            SectionOverviewDTO sectionOverviewDTO = testResultDTO.getSectionOverview(section);
            if (sectionOverviewDTO.getStepNames()!=null && sectionOverviewDTO.getStepNames().size()>0) {
                String stepName = sectionOverviewDTO.getStepNames().get(0);
                StepOverviewDTO stepOverviewDTO = sectionOverviewDTO.getStep(stepName);

                List<InteractingEntity> interactionSequence = stepOverviewDTO.getInteractionSequence();

                if (interactionSequence!=null) {

                    if (interactionSequence.size()==1) {
                        if (InteractingEntity.INTERACTIONSTATUS.UNKNOWN.equals(interactionSequence.get(0).getStatus())) {
//                            if (interactionSequence.get(0).getErrors()!=null && interactionSequence.get(0).getErrors().get(0)!=null) {
//                                String error = interactionSequence.get(0).getErrors().get(0);
//                                if (error.contains(TransactionSequenceNotFoundException.class.getSimpleName())) {
                                    hasUnmappableTransaction = true;
//                                }
//                            }
                        } else {
                            setIePlaceholderValues(interactionSequence);
                            setIeTransactionStatus(section, sectionOverviewDTO, stepName, stepOverviewDTO, interactionSequence);

//                    if ("11981".equals(testResultDTO.getTestInstance().getId()))
//                    alert("section: " + section + " step: " + stepName + " is hashcode: " + interactionSequence.hashCode() + " step hashcode: " + stepOverviewDTO.hashCode());

                            result.addAll(interactionSequence);
                        }
                    }
                }
            }
         }

        return result;
    }

    private void setIeTransactionStatus(String section, SectionOverviewDTO sectionOverviewDTO, String stepName, StepOverviewDTO stepOverviewDTO, List<InteractingEntity> interactionSequence) {
        if (interactionSequence.size()>0) {
            InteractingEntity srcTranOrigin = interactionSequence.get(0);

            if (srcTranOrigin != null) {
                srcTranOrigin.setBegin(sectionOverviewDTO.getHl7Time());

             if (srcTranOrigin.getInteractions().size() > 0) {
                InteractingEntity dest = srcTranOrigin.getInteractions().get(0);
                 dest.setBegin(sectionOverviewDTO.getHl7Time());

                 if ("SystemUnderTest".equals(dest.getProvider()) || "SystemUnderTest".equals(srcTranOrigin.getProvider())) {
                    addLegend("SystemUnderTest");
                 }

                setLabelAndErrors(dest, sectionOverviewDTO, stepName);
                if (sectionOverviewDTO.isRun()) {
                    setAtleastOneSectionWasRun(true);
                    if (sectionOverviewDTO.isPass()) {
                        if (stepOverviewDTO.isExpectedSuccess()) {
                            dest.setStatus(InteractingEntity.INTERACTIONSTATUS.COMPLETED);
                        } else {
                            dest.setStatus(InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED);
                            if (dest.getErrors() == null) {
                                dest.setErrors(new ArrayList<String>());
                            }
                            dest.getErrors().add(0, "" + section + "/" + stepName + ":<br/> Response message contains errors as expected.");
                            addLegend(InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED.name());
                        }

                    } else {
                        dest.setStatus(InteractingEntity.INTERACTIONSTATUS.ERROR);
                        addLegend(InteractingEntity.INTERACTIONSTATUS.ERROR.name());
                    }
                } else if (isAtleastOneSectionWasRun()) {
                    dest.setStatus(InteractingEntity.INTERACTIONSTATUS.SKIPPED);
                }
            }
          }
        }
    }

    private void setIePlaceholderValues(List<InteractingEntity> interactionSequence) {

        for (InteractingEntity interactingEntity : interactionSequence) {
            interactingEntity.setSutActorByRole(null, getSutActorRoleName());
        }

        Map<String,String> placeholderMap = new HashMap<>();

        placeholderMap.put("SystemUnderTest", getSutSystemName());
        if (getSutSystemName().equals(getTargetSite().getName())) {
            // No simulators in use
        } else {
            placeholderMap.put("Simulator", getTargetSite().getName());
            // getTargetSite().getOrchestrationSiteName() is the actual SUT. For combined SiteSpec like the Repository actor
        }

        for (InteractingEntity interactingEntity : interactionSequence) {
            interactingEntity.setNameByProvider(null, placeholderMap);
        }
    }


    private void addLegend(String legend) {
        if (!legends.contains(legend)) {
            legends.add(legend);
        }
    }

    private void setDiagramArea(int diagramHeight, int diagramWidth) {
        setDiagramHeight(diagramHeight);
        setDiagramWidth(diagramWidth);

        svgsvgElement.setAttribute("xmlns","http://www.w3.org/2000/svg");
        svgsvgElement.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
        svgsvgElement.setAttribute("height",""+ getDiagramHeight());
        svgsvgElement.setAttribute("width",""+ getDiagramWidth());
        svgsvgElement.setNodeValue("Sorry, your browser does not seem to support inline SVG.");
    }


    public void draw() {

        setMargins();

        for (InteractingEntity interactingEntity : getEntityList()) {
            sequence(interactingEntity,null);
        }
        ll_stem();
        ll_activitybox();
        legend();

        setDiagramArea(g_y+ LL_FEET,g_x);
    }

    void legend() {
        final int legend_margin = 3;
        if (legends.size()>0) {
            int x = 0/* x of the diagram start area */ + HALF_CROSS_HEIGHT * 2;
            g_y+= LINE_HEIGHT;
            OMSVGGElement group = doc.createSVGGElement();
            group.appendChild(getSimpleLabel(0,g_y,"Legend:"));
            g_y+= LINE_HEIGHT + HALF_CROSS_HEIGHT *2;
            for (String legend : legends) {
                if (InteractingEntity.INTERACTIONSTATUS.ERROR.name().equals(legend)) {
                    group.appendChild(cross_mark(x, g_y+legend_margin, RGB_RED));
                    group.appendChild(getSimpleLabel(x+ HALF_CROSS_HEIGHT *2,g_y- HALF_CROSS_HEIGHT,"Error"));
//                    g_y+=(HALF_CROSS_HEIGHT *2);
                    g_y+=LINE_HEIGHT+legend_margin;
                } else if (InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED.name().equals(legend)) {
                    svgsvgElement.appendChild(cross_mark(x, g_y+legend_margin, RGB_BLUE));
                    group.appendChild(getSimpleLabel(x+ HALF_CROSS_HEIGHT *2,g_y- HALF_CROSS_HEIGHT,"Expected error"));
//                    g_y+= HALF_CROSS_HEIGHT *2;
                    g_y+=LINE_HEIGHT+legend_margin;
                } else if ("SystemUnderTest".equals(legend)) {
                    svgsvgElement.appendChild(box(x-HALF_CROSS_HEIGHT,g_y, RGB_ORANGE));
                    group.appendChild(getSimpleLabel(x+ HALF_CROSS_HEIGHT *2,g_y- HALF_CROSS_HEIGHT,"System Under Test"));
//                    g_y+= HALF_CROSS_HEIGHT *2;
                    g_y+=LINE_HEIGHT+legend_margin;
                }
            }
            svgsvgElement.appendChild(group);

        }
    }

    OMSVGTextElement getSimpleLabel(int x, int y, String caption) {
        OMSVGTextElement text = doc.createSVGTextElement();
        text.setAttribute("x",""+x);
        text.setAttribute("y",""+y); // Shift text up the connecting line, 3 for top of the line
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size","10");

        OMSVGTSpanElement line1 = getOmsvgtSpanElement(x, "" + caption);
        text.appendChild(line1);

        return text;
    }

    void ll_activitybox() {
            for (LL ll : lls) {
                if (ll.getActivityFrames().size()>0) {
                    int lastFrameIdx = ll.getActivityFrames().size()-1;
                    TouchPoint tpLast = ll.getActivityFrames().get(lastFrameIdx);
//                    if (!ll.isRoot() && ll.getActivityRange().getY_max()>=tpLast.getY_max())
//                        svg.appendChild(getActivityBoxEl(ll.getActivityRange().getX_min(), ll.getActivityRange().getY_min(), ll.getActivityRange().getY_max()));
//                    else
                       for (TouchPoint activityFrame : ll.getActivityFrames()) {
                         svgsvgElement.appendChild(getActivityBoxEl(activityFrame.getX_min(), activityFrame.getY_min(), activityFrame.getY_max(),activityFrame.getLineFillColor()));
                       }
                }

            }
    }

    /**
     * The box the shows activity in a life line for the transaction in question
     * @param x
     * @param y1
     * @param y2
     * @return
     */
    OMSVGElement getActivityBoxEl(int x, int y1, int y2, String strokeColor) {
        OMSVGRectElement box = doc.createSVGRectElement();
        box.setAttribute("width", "" + activity_box_width);
        box.setAttribute("height", "" + (y2 - y1));
        box.setAttribute("x", "" + (x-(activity_box_width/2)));
        box.setAttribute("y", "" + y1);
        box.setAttribute("style", "fill:rgb(255,255,255);stroke-width:1;stroke:" + ((strokeColor==null||"".equals(strokeColor))?"black":strokeColor));
        return box;

    }


    void ll_stem() {

        for (LL ll: lls) {
            OMSVGLineElement line = doc.createSVGLineElement();
            line.setAttribute("x1",""+ll.getLl_stem_center());
            int y = LL_BOX_HEIGHT;
            line.setAttribute("y1",""+y);
            line.setAttribute("x2",""+ll.getLl_stem_center());
            line.setAttribute("y2",""+(g_y+LL_FEET));
            line.setAttribute("style","stroke:rgb(0,0,0);stroke-dasharray:2,2");

            svgsvgElement.appendChild(line);
        }
        g_y+=LL_FEET;
    }

    void sequence(InteractingEntity parent_entity, LL parent ) {
        LL parentll = null;
        if (parent==null) {
            parentll = create_LL(parent_entity);
            svgsvgElement.appendChild(parentll.getLlEl());
        } else {
            parentll = parent;
        }
        LL childll = null;

        if (parent_entity.getInteractions()!=null) {
            for (InteractingEntity child : parent_entity.getInteractions()) {
                g_depth++;
                childll = create_LL(child);
                svgsvgElement.appendChild(childll.getLlEl());
                svgsvgElement.appendChild(connect(parentll,childll,false,child));
                if (!InteractingEntity.INTERACTIONSTATUS.ERROR.equals(child.getStatus()) && child.getInteractions()!=null) {
                    sequence(child, childll);
                    if (childll!=null) {
                        g_depth++;
                        svgsvgElement.appendChild(connect(childll, parentll, true, child));
                    }
                } else {
                    g_depth++;
                    svgsvgElement.appendChild(connect(childll, parentll, true, child));
                }
            }

        }
    }

    OMSVGElement connect(LL originll, LL destinationll, boolean response, final InteractingEntity entity) {
        OMSVGGElement origin = originll.getLlEl();
        OMSVGGElement destination = destinationll.getLlEl();

        InteractingEntity.INTERACTIONSTATUS status = entity.getStatus();
        String lineFillColor = (isInspectableInteractionStatus(status))?"black":"#dcdcdc";

        OMSVGLineElement line = doc.createSVGLineElement();
        int x1 = (Integer.parseInt(((OMSVGRectElement)origin.getFirstChild()).getAttribute("x").toString())+(LL_BOX_WIDTH /2));
        line.setAttribute("x1",""+x1);
        int y = Integer.parseInt(((OMSVGRectElement)origin.getFirstChild()).getAttribute("y").toString())+ LL_BOX_HEIGHT +g_depth*connection_topmargin;
        g_y = y;

        line.setAttribute("y1",""+y);
        int x2 = (Integer.parseInt(((OMSVGRectElement)destination.getFirstChild()).getAttribute("x").toString())+(LL_BOX_WIDTH /2));
        line.setAttribute("x2",""+x2);
        line.setAttribute("y2",""+y);
        line.setAttribute("style","stroke:"+lineFillColor+";stroke-width:1;" + ((response)?"stroke-dasharray:4,8":""));

        OMSVGGElement group = doc.createSVGGElement();
        group.appendChild(line);

        /*

        []    []
        |     |
        x1--->x2 (Request)
        x2<---x1 (Response)

         */



        int textY = y;

        if (!response) {
            int centerTextX = (x1 + x2)/2;

            if (x2>x1) {
                // Request
                // x1 ----------------> x2
                if (Math.abs(x2-x1)>TRANSACTION_PAIR_WIDTH) { // Make labels stick closer to the origin if the line spans a long distance over multiple LLs.
                    centerTextX = originll.getLl_stem_center() + activity_box_width + ll_margin;
                }

                group.appendChild(arrow_request_right(x2, y, lineFillColor));
            } else {
                // Request
                // x2 <----------------- x1
                if (Math.abs(x1-x2)>TRANSACTION_PAIR_WIDTH) {
                    centerTextX = originll.getLl_stem_center() + activity_box_width - (ll_margin);
                }
                group.appendChild(arrow_request_left(x2, y, lineFillColor));
            }

            if (status!=null) {
                final List<String> messages = new ArrayList<String>();
                String tooltipMessage = "(Click to inspect results)";
                TransactionInstance tranInstance = entity.getTransactionInstance();
                if (tranInstance!=null) {
                    tooltipMessage = "#SimLog:"+ tranInstance.simId.toString() + "/" + tranInstance.actorType.getShortName() + "/" + tranInstance.trans + "/" + tranInstance.messageId;
                }

                List<String> lines = new ArrayList<>();

                String description = entity.getDescription();
                String transaction = entity.getSourceInteractionLabel();
                if (description!=null && description.length()>MAX_LABEL_DISPLAY_LEN)
                    messages.add(description);
                messages.add(tooltipMessage);
                if (entity.getDescription()!=null)
                    lines.add(description);
                if (tranInstance!=null) {
                   lines.add(tranInstance.nameInterpretedAsTransactionType.getName());
                    group.setAttribute("style","cursor:pointer");
                    addTooltip(group, messages, HIDE_TOOLTIP_ON_MOUSEOUT);
                } else {
                    if (transaction!=null) {
                        TransactionType tranType = TransactionType.find(transaction);
                        if (tranType!=null) {
                            lines.add(tranType.getName());
                        } else {
                            lines.add(transaction.replace("Transaction",""));
                        }
                    } else {
                        lines.add("Unknown Tx");
                    }

                    if (InteractingEntity.INTERACTIONSTATUS.UNKNOWN.equals(status)) {
                        lines.add("Not Found");
                    } else if (!InteractingEntity.INTERACTIONSTATUS.SKIPPED.equals(status)) {
                        group.setAttribute("style", "cursor:pointer");
                        addTooltip(group, messages, HIDE_TOOLTIP_ON_MOUSEOUT);
                    }
                }

                group.appendChild(multiLineLabel(centerTextX, (textY - (4 + (LINE_HEIGHT * lines.size()))), lines.toArray(new String[0]), 10, MAX_LABEL_DISPLAY_LEN));
                if (tranInstance!=null) {
                    // Anchors don't seem to work in a Group.
                    final String url = tooltipMessage;
                    group.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            Window.open(url, "_blank", "");
                        }
                    });
                } else if (isInspectableInteractionStatus(status)) {
                    group.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            getEventBus().fireEvent(new DiagramClickedEvent(getTestOverviewDTO().getTestInstance(), DiagramPart.RequestConnector));
                        }
                    });
                }

            } else {
                /* No status - probably not yet executed */
                TransactionType tranType = TransactionType.find(entity.getSourceInteractionLabel());
                String transaction = (tranType!=null)?tranType.getName():entity.getSourceInteractionLabel();
                List<String> lines = new ArrayList<>();
                if (entity.getDescription() != null)
                    lines.add(entity.getDescription());
                if (transaction!=null)
                    lines.add(transaction);
                if (!lines.isEmpty())
                    group.appendChild(multiLineLabel(centerTextX,(textY-(4+(LINE_HEIGHT * lines.size()))),lines.toArray(new String[0]),10,MAX_LABEL_DISPLAY_LEN));
            }

        } else {
            /*
             * Response
             */

            int centerTextX = (x1+x2)/2;

            group.setAttribute("style","cursor:pointer");

            if (x2<x1) {
                // Response
                // x2 <---------------- x1
                if (Math.abs(x1-x2)>TRANSACTION_PAIR_WIDTH)  {
                    centerTextX = originll.getLl_stem_center() + activity_box_width - (ll_margin);
                }
                group.appendChild(arrow_response_left(x2, y, lineFillColor));
            } else {
                // Response
                // x1 ----------------> x2
                if (Math.abs(x2-x1)>TRANSACTION_PAIR_WIDTH) {
                    centerTextX = originll.getLl_stem_center() + activity_box_width + ll_margin;
                }
                group.appendChild(arrow_response_right(x2, y, lineFillColor));
            }

            // -----
            String x_mark_Rgb = null;
            if (InteractingEntity.INTERACTIONSTATUS.ERROR.equals(status)) {
               x_mark_Rgb = RGB_RED;
            } else if (InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED.equals(status)) {
                x_mark_Rgb = RGB_BLUE;
            }

            if (x_mark_Rgb!=null) {
                final List<String> errors = entity.getErrors();
                group.appendChild(cross_mark(centerTextX,y,x_mark_Rgb));
//                textY -= (half_cross_height); // two lines of text

                if (errors!=null) {
                    addTooltip(group,errors, HIDE_TOOLTIP_ON_MOUSEOUT);
                }
            }


            group.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    getEventBus().fireEvent(new DiagramClickedEvent(getTestOverviewDTO().getTestInstance(), DiagramPart.ResponseConnector));
                }
            } );

        }




        // Min/max Touch points
        // Origin y
        /*
        if (originll.getActivityRange().getY_min()==0 || y<originll.getActivityRange().getY_min())
            originll.getActivityRange().setY_min(y);
        if (originll.getActivityRange().getY_max()==0 || y>originll.getActivityRange().getY_max())
            originll.getActivityRange().setY_max(y);
        if (originll.getActivityRange().getX_min()==0 || x1<originll.getActivityRange().getX_min())
            originll.getActivityRange().setX_min(x1);
        // Destination y
        if (destinationll.getActivityRange().getY_min()==0 || y<destinationll.getActivityRange().getY_min())
            destinationll.getActivityRange().setY_min(y);
        if (destinationll.getActivityRange().getY_max()==0 || y>destinationll.getActivityRange().getY_max())
            destinationll.getActivityRange().setY_max(y);
            */

        if (response) {
            /*

             request frame:
             [from:origin] -> [to:destination]

             response frame match:
             [to:destination] <- [from:origin]

            What origin means:
            Origin is the entity that is sending source be it the request or the response.

            General diagram idea:
            Only a new activity box is opened for closed frames in the list. If a frame is open (y_max=0) then we assume that a synchronous socket is previously pending.

             */


            TouchPoint originFrame = null;
            int idx = originll.getActivityFrames().size()-1;
            for (int cx = idx; 0 <= cx; cx--) {
               originFrame = originll.getActivityFrames().get(cx);
                if (originFrame.getY_max() == 0 && originFrame.getFrom().equals(destinationll.getName()) && originFrame.getTo().equals(originll.getName())) { // This is would be the request frame match
                    originFrame.setY_max(y);
                }
            }

            TouchPoint destFrame = null;
            idx = destinationll.getActivityFrames().size()-1;
            for (int cx = idx; 0 <= cx; cx--) {
                destFrame = destinationll.getActivityFrames().get(cx);
                if (destFrame.getY_max() == 0 && destFrame.getFrom().equals(destinationll.getName()) && destFrame.getTo().equals(originll.getName())) {
                    destFrame.setY_max(y);
                }
            }


        } else {
            int lastOriginAf = originll.getActivityFrames().size()-1;

            TouchPoint originFrame = null;
            if (lastOriginAf>-1) {
                originFrame = originll.getActivityFrames().get(lastOriginAf);
            }
                if (lastOriginAf==-1 || (originFrame!=null && originFrame.getY_max()!=0)) {// Previous frame unfinished, do not start a new one
                    originFrame = getTouchPoint(originll, destinationll, y, x1, lineFillColor);
                    originll.getActivityFrames().add(originFrame);
                }


            int lastDestAf = destinationll.getActivityFrames().size()-1;
            TouchPoint destFrame = null;
            if (lastDestAf>-1) {
                destFrame = destinationll.getActivityFrames().get(lastDestAf);
            }
                if (lastDestAf==-1 || (destFrame!=null && destFrame.getY_max()!=0)) {
                    destFrame = getTouchPoint(originll, destinationll, y, x2, lineFillColor);
                    destinationll.getActivityFrames().add(destFrame);
                }
        }

        return group;
    }

    private boolean isInspectableInteractionStatus(InteractingEntity.INTERACTIONSTATUS status) {
        return !InteractingEntity.INTERACTIONSTATUS.UNKNOWN.equals(status)||!InteractingEntity.INTERACTIONSTATUS.SKIPPED.equals(status);
    }

    private void addTooltip(OMSVGGElement group, final List<String> messages, final int timeOutInMilliseconds) {
        group.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                showTooltip(mouseOverEvent, messages, timeOutInMilliseconds);
            }
        });

        group.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {
                getTooltip().hide();
            }
        });
    }

    private TouchPoint getTouchPoint(LL originll, LL destinationll, int y, int x2, String lineFillColor) {
        TouchPoint destFrame;
        destFrame = new TouchPoint();
        destFrame.setX_min(x2);
        destFrame.setY_min(y);
        destFrame.setY_max(0);
        destFrame.setFrom(originll.getName());
        destFrame.setTo(destinationll.getName());
        destFrame.setLineFillColor(lineFillColor);
        return destFrame;
    }

    /**
     * x,y is the center of the cross mark
     * @param x
     * @param y
     * @return
     */
    OMSVGGElement cross_mark(int x, int y, String strokeRgb) {
        OMSVGGElement x_group = doc.createSVGGElement();
        OMSVGPathElement l_part = doc.createSVGPathElement();
       l_part.setAttribute("d","M " + (x- HALF_CROSS_HEIGHT) + " " + (y- HALF_CROSS_HEIGHT) // constant is the half-height of the cross
               + " l5 5 l-5 5");
        l_part.setAttribute("style","fill:rgb(255,255,255);stroke-width:1.5;stroke:"+strokeRgb+";stroke-linecap:round");
        x_group.appendChild(l_part);
        OMSVGPathElement r_part = doc.createSVGPathElement();
        r_part.setAttribute("d","M " + (x+ HALF_CROSS_HEIGHT) + " " + (y- HALF_CROSS_HEIGHT)
                + " l-5 5 l5 5");
        r_part.setAttribute("style","fill:rgb(255,255,255);stroke-width:1.5;stroke:"+strokeRgb+";stroke-linecap:round");
        x_group.appendChild(r_part);

        return x_group;
    }

    OMSVGElement box(int x, int y, String strokeRgb) {
        OMSVGRectElement rect = doc.createSVGRectElement();
        rect.setAttribute("width",""+ HALF_CROSS_HEIGHT * 2);
        rect.setAttribute("height",""+ HALF_CROSS_HEIGHT * 2);
        rect.setAttribute("x",""+x);
        rect.setAttribute("y","" + y);
        rect.setAttribute("style","fill:"+strokeRgb+";stroke-width:2;stroke:" + strokeRgb );

        return rect;
    }

    OMSVGAElement svgAnchor(int x, int y, String url, String[] lines, int fontSize, int maxLineLen) {
        OMSVGAElement anchor = new OMSVGAElement();
        anchor.setAttribute("xlink:href", url);
        anchor.setAttribute("target","_blank");

        anchor.appendChild(multiLineLabel(x,y,lines,fontSize,maxLineLen));

        return anchor;
    }

    OMSVGTextElement multiLineLabel(int x, int y, String[] lines, int fontSize, int maxLineLen) {
        OMSVGTextElement text = getOmsvgTextElement(x, y, fontSize);

        for (String line : lines) {
            OMSVGTSpanElement line1 = getOmsvgtSpanElement(x, "" + getShortName(line,maxLineLen));
            text.appendChild(line1);
        }

       return text;
    }

    private OMSVGTextElement getOmsvgTextElement(int x, int y, int fontSize) {
        OMSVGTextElement text = doc.createSVGTextElement();
        text.setAttribute("x",""+x);
        text.setAttribute("y",""+y); // Shift text up the connecting line, 3 for top of the line
        text.setAttribute("text-anchor","middle");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size",""+fontSize);
        return text;
    }

    private OMSVGTSpanElement getOmsvgtSpanElement(int x, String string2) {
        OMSVGTSpanElement line = doc.createSVGTSpanElement();
        OMText line2Node = doc.createTextNode(getShortName(string2,MAX_LABEL_DISPLAY_LEN));
        line.setAttribute("x",""+x);
        line.setAttribute("dy",""+ LINE_HEIGHT);
        line.appendChild(line2Node);
        return line;
    }



    OMSVGPolygonElement arrow_request_left(int x, int y, String fillColor) {
            x += (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                   "" + x + "," + y
                + " " + (x+5) + "," + (y-5)
                + " " + (x+5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:" + fillColor);//black

        return arrow;
    }
    OMSVGPolygonElement arrow_request_right(int x, int y, String fillColor) {
        x -= (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
              "" + x + "," + y
               + " " + (x-5) + "," + (y-5)
               + " " + (x-5) + "," + (y+5)
            );
            arrow.setAttribute("style","fill:" + fillColor);// black

        return arrow;
    }

    OMSVGPolygonElement arrow_response_left(int x, int y, String fillColor) {
        x += (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                         " " + (x+5) + "," + (y-5)
                       + " " + x + "," + y
                        + " " + (x+5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:white;stroke:"+fillColor+";stroke-width:1"); // black

        return arrow;
    }
    OMSVGPolygonElement arrow_response_right(int x, int y, String fillColor) {
        x -= (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                " " + (x-5) + "," + (y-5)
                        + " " + x + "," + y
                        + " " + (x-5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:white;stroke:"+ fillColor +";stroke-width:1"); //black
        return arrow;
    }

    LL create_LL(InteractingEntity entity) {

        final String name = (entity.getName()==null)?"Toolkit":entity.getName();
        final String id = entity.getRole() + "_" + entity.getProvider() + "_" + entity.getName();
        LL ll = getLL(id);

        if (ll!=null)
            return ll;

        ll = new LL();
        ll.setName(name);
        ll.setId(id);

        int ll_count = lls.size();
        if (ll_count==0) // First LL being created is the root
            ll.setRoot(true);

        OMSVGRectElement rect = doc.createSVGRectElement();
        rect.setAttribute("width",""+ LL_BOX_WIDTH);
        rect.setAttribute("height",""+ LL_BOX_HEIGHT);
        int x = ll_count* LL_BOX_WIDTH;
        x+=ll_margin*ll_count; // margin that separates this lifeline from the previous one
        g_x = x + LL_BOX_WIDTH;
        ll.setLl_stem_center(x + (LL_BOX_WIDTH /2));
        rect.setAttribute("x",""+x);
        rect.setAttribute("y","0");
        String boxRgb = "rgb(255,255,255);";
        if ("SystemUnderTest".equals(entity.getProvider())) {
            boxRgb = RGB_ORANGE ; // Orange
        } /*else if ("Simulator".equals(entity.getProvider())) {
            boxRgb = "rgb(0,0,255);"; // Blue
        } */
        rect.setAttribute("style","fill:"+boxRgb+";stroke-width:2;stroke:rgb(0,0,0)" );

        /*
        rect.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.alert(name);
            }
        });
        */




//        OMSVGTextElement text = doc.createSVGTextElement();
//        text.setAttribute("x",""+ll.getLl_stem_center());
//        text.setAttribute("y","" + (LL_BOX_HEIGHT /2));
//        text.setAttribute("dy","3");
//        text.setAttribute("font-family","Verdana");
//        text.setAttribute("font-size","10");
//        text.setAttribute("text-anchor","middle");

        OMSVGGElement group = doc.createSVGGElement();
        String shortName = name;
        List<String> actorDetail = new ArrayList<>();
//        shortName = getShortName(name,MAX_LL_DISPLAY_NAME);
        if (!name.equals(entity.getProvider()))
            actorDetail.add(name);
        actorDetail.add(entity.getRole());
        actorDetail.add(entity.getProvider());
        addTooltip(group,actorDetail, HIDE_TOOLTIP_ON_MOUSEOUT);
//        OMText textValue = doc.createTextNode(shortName);
//        text.appendChild(textValue);
        group.appendChild(rect);

        // Max display line is only 3 or NUM_LINES. Need to enforce this limit.
        OMSVGTextElement providerTextEl = doc.createSVGTextElement();
        String[] providerNameLabel = splitName(name, MAX_LL_DISPLAY_NAME, ".", true);
        providerTextEl =  multiLineLabel(ll.getLl_stem_center(),2, providerNameLabel,9, MAX_LL_DISPLAY_NAME);
        group.appendChild(providerTextEl);

        OMSVGTextElement roleTextEl = doc.createSVGTextElement();
        String[] roleLabel = splitName(entity.getRole(), MAX_LL_DISPLAY_NAME, "-", false);
        roleTextEl =  multiLineLabel(ll.getLl_stem_center(),2 + providerNameLabel.length * LINE_HEIGHT, roleLabel,9, MAX_LL_DISPLAY_NAME);
        group.appendChild(roleTextEl);

        ll.setLlEl(group);
        lls.add(ll);


        return ll;
    }

    /**
     * Split string to a maximum of two lines.
     * @param name
     * @param truncateTo
     * @param trailingString
     * @return
     */
    private String[] splitName(String name, int truncateTo, String trailingString, boolean singleLineOnly) {
        String lineLimitExceededString = "...";
        String[] lines = new String[2];
        if (name!=null && name.length()>truncateTo && truncateTo>trailingString.length()) {
             lines[0] = name.substring(0, truncateTo-trailingString.length()) + trailingString;
             if (singleLineOnly) {
                 return new String[]{lines[0]};
             }
             int charsRemaining = name.length() - truncateTo;
             if (charsRemaining > truncateTo) {
                 lines[1] = name.substring(truncateTo-1, truncateTo*2-lineLimitExceededString.length()) + lineLimitExceededString;
             } else {
                 lines[1] = name.substring(truncateTo-1);
             }
             return lines;
        }
        else
             return new String[]{name};
    }

    private String getShortName(String name, int truncateTo) {
        return splitName(name, truncateTo, "...", true)[0];
    }

    LL getLL(String id) {
        if (id==null)
            return null;
        for (LL ll : lls) {
            if (id.equals(ll.getId())) {
                return ll;
            }
        }
        return null;
    }


    private void showTooltip(MouseEvent<? extends EventHandler> e, List<String> text, int milliseconds) {
        tooltip.show(e.getClientX() + 20, e.getClientY() + 30, text, milliseconds);
    }



    public int getDiagramHeight() {
        return diagramHeight;
    }

    public void setDiagramHeight(int diagramHeight) {
        this.diagramHeight = diagramHeight;
    }

    public int getDiagramWidth() {
        return diagramWidth;
    }

    public void setDiagramWidth(int diagramWidth) {
        this.diagramWidth = diagramWidth;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public TestOverviewDTO getTestOverviewDTO() {
        return testOverviewDTO;
    }

    public void setTestOverviewDTO(TestOverviewDTO testOverviewDTO) {
        this.testOverviewDTO = testOverviewDTO;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public boolean hasMeaningfulDiagram() {
        // At-least two life lines for a meaningful diagram
       return (lls.size()>1) && !hasUnmappableTransaction;
    }

    public SiteSpec getTargetSite() {
        return targetSite;
    }

    public void setTargetSite(SiteSpec targetSite) {
        this.targetSite = targetSite;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSutSystemName() {
        return sutSystemName;
    }

    public void setSutSystemName(String sutSystemName) {
        this.sutSystemName = sutSystemName;
    }

    public String getSutActorRoleName() {
        return sutActorRoleName;
    }

    public void setSutActorRoleName(String sutActorRoleName) {
        this.sutActorRoleName = sutActorRoleName;
    }

    public List<InteractingEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<InteractingEntity> entityList) {
        this.entityList = entityList;
    }

    public boolean isAtleastOneSectionWasRun() {
        return atleastOneSectionWasRun;
    }

    public void setAtleastOneSectionWasRun(boolean atleastOneSectionWasRun) {
        this.atleastOneSectionWasRun = atleastOneSectionWasRun;
    }

}
