package gov.nist.toolkit.interactiondiagram.client.widgets;


import com.google.gwt.dom.client.Element;
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
import gov.nist.toolkit.interactiondiagram.client.events.DiagramClickedEvent;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.StepOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPolygonElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.OMText;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 Sunil.Bhaskarla on 8/12/2016.
 */
// TODO: Use a style sheet.
public class InteractionDiagram extends Composite {

    public static final int NUM_LINES = 3;
    int g_depth = 0;
    int g_x = 0;
    int g_y = 0;

    static final int half_cross_height = 5;
    static final int line_height = 13;
    static final int ll_boxWidth = 70;
    static final int ll_boxHeight = 25;
    static final int LL_FEET = 10; // life line feet (extra) lines after the last transaction

    int ll_margin = 108; // The with of transaction connector
    int maxLabelLen = 0;
    int MAX_LABEL_DISPLAY_LEN = 27;
    int connection_topmargin = (NUM_LINES * line_height) + (half_cross_height*2) + 2; // top margin of a transaction
    int error_box_offset = 30;


    int diagramHeight = 0;
    int diagramWidth = 0;

    OMSVGDocument doc = OMSVGParser.createDocument();
    OMSVGSVGElement svg =  doc.createSVGSVGElement();

    TestOverviewDTO testOverviewDTO;
    EventBus eventBus;

    static final int max_tooltips = 5;
    static final int hide_tooltip_on_mouseout = -1;
    Tooltip tooltip = new Tooltip();

    public static enum DiagramPart {
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
                   if (cx==max_tooltips) {
                      contents.setHTML(contents.getHTML() + "<p>"+ (count-max_tooltips) +" more...</p>");
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

        Element svg = draw(interactingEntity);
        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svg);

      initWidget(container);
    }

    public InteractionDiagram(List<InteractingEntity> interactingEntity, int diagramHeight, int diagramWidth) {
        setDiagramArea(diagramHeight, diagramWidth);

        Element svg = draw(interactingEntity);
        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svg);

        initWidget(container);
    }

    public InteractionDiagram(EventBus eventBus, final TestOverviewDTO testOverviewDTO) {
        setEventBus(eventBus);
        setTestOverviewDTO(testOverviewDTO);

        List<InteractingEntity> interactingEntity = transformTestResultToInteractingEntity(testOverviewDTO);
        if (interactingEntity==null)
            return;

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

        Element svg = draw(interactingEntity);
        setDiagramArea(g_y+ LL_FEET,g_x);

        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svg);

        initWidget(container);
    }


    String setLabelAndErrors(InteractingEntity entity, SectionOverviewDTO sectionOverviewDTO) {
        String label = "";
        if (sectionOverviewDTO.getStepNames().size()>0) {
            String stepName = sectionOverviewDTO.getStepNames().get(0);
            label = "Section: " + sectionOverviewDTO.getName() + "^" + "Step: " + stepName;
            StepOverviewDTO step = sectionOverviewDTO.getStep(stepName);
            entity.setErrors(step.getErrors());
            entity.setSourceInteractionLabel(step.getTransaction());
        } else
            label = sectionOverviewDTO.getName();

        int labelLen = label.length();
        if (labelLen> maxLabelLen)
            maxLabelLen =labelLen;


        entity.setDescription(label);
        return label;
    }

    List<InteractingEntity> transformTestResultToInteractingEntity(TestOverviewDTO testResultDTO) {
        if (testResultDTO==null || testResultDTO.getSectionNames()==null) return null;

        List<String> sectionNames = testResultDTO.getSectionNames();

        if (sectionNames==null || (sectionNames!=null && sectionNames.isEmpty()))
            return null;

        List<InteractingEntity> result = new ArrayList<InteractingEntity>();

        for (String section : sectionNames) {
           SectionOverviewDTO sectionOverviewDTO = testResultDTO.getSectionOverview(section);
            if (sectionOverviewDTO.isRun()) {
                InteractingEntity source = new InteractingEntity();
                source.setName("Toolkit"); // TODO: Source should come from the SectionOverviewDTO
                InteractingEntity destination = new InteractingEntity();
                destination.setName(sectionOverviewDTO.getSite());
                setLabelAndErrors(destination,sectionOverviewDTO);
                if (sectionOverviewDTO.isPass()) {
                    String stepName = sectionOverviewDTO.getStepNames().get(0);
                    StepOverviewDTO stepOverviewDTO = sectionOverviewDTO.getStep(stepName);
                    if (stepOverviewDTO.isExpectedSuccess())
                        destination.setStatus(InteractingEntity.INTERACTIONSTATUS.COMPLETED);
                    else { // Special case
                        destination.setStatus(InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED);
                        if (destination.getErrors()==null) {
                           destination.setErrors(new ArrayList<String>());
                        }
                        destination.getErrors().add(0,""+ section + "/"+  stepName +":<br/> Response message contains errors as expected.");
                    }
                } else {
                    destination.setStatus(InteractingEntity.INTERACTIONSTATUS.ERROR);
                }
                source.setInteractions(new ArrayList<InteractingEntity>());
                source.getInteractions().add(destination);
                result.add(source);
            }
        }

        return result;
    }




    private void setDiagramArea(int diagramHeight, int diagramWidth) {
        setDiagramHeight(diagramHeight);
        setDiagramWidth(diagramWidth);

        svg.setAttribute("height",""+ getDiagramHeight());
        svg.setAttribute("width",""+ getDiagramWidth());
        svg.setNodeValue("Sorry, your browser does not seem to support inline SVG.");
    }

    public Element draw(InteractingEntity parent_entity) {

        sequence(parent_entity,null);
        ll_stem();
        ll_activitybox();

        return svg.getElement();
    }

    public Element draw(List<InteractingEntity> entityList) {

        for (InteractingEntity interactingEntity : entityList) {
            sequence(interactingEntity,null);
        }
        ll_stem();
        ll_activitybox();

        return svg.getElement();
    }

    void ll_activitybox() {
            for (LL ll : lls) {
                if (ll.getActivityFrames().size()>0) {
                    int lastFrameIdx = ll.getActivityFrames().size()-1;
                    TouchPoint tpLast = ll.getActivityFrames().get(lastFrameIdx);
//                    if (!ll.isRoot() && ll.getActivityRange().getY_max()>=tpLast.getY_max())
//                        svg.appendChild(getAcitivyBoxEl(ll.getActivityRange().getX_min(), ll.getActivityRange().getY_min(), ll.getActivityRange().getY_max()));
//                    else
                       for (TouchPoint activityFrame : ll.getActivityFrames()) {
                         svg.appendChild(getAcitivyBoxEl(activityFrame.getX_min(), activityFrame.getY_min(), activityFrame.getY_max()));
                       }
                }

            }
    }

    OMSVGElement getAcitivyBoxEl(int x, int y1, int y2) {
        OMSVGRectElement box = doc.createSVGRectElement();
        box.setAttribute("width", "" + activity_box_width);
        box.setAttribute("height", "" + (y2 - y1));
        box.setAttribute("x", "" + (x-(activity_box_width/2)));
        box.setAttribute("y", "" + y1);
        box.setAttribute("style", "fill:rgb(255,255,255);stroke-width:1;stroke:rgb(0,0,0)");
        return box;

    }


    void ll_stem() {

        for (LL ll: lls) {
            OMSVGLineElement line = doc.createSVGLineElement();
            line.setAttribute("x1",""+ll.getLl_stem_center());
            int y = ll_boxHeight;
            line.setAttribute("y1",""+y);
            line.setAttribute("x2",""+ll.getLl_stem_center());
            line.setAttribute("y2",""+(g_y+10)); // +10
            line.setAttribute("style","stroke:rgb(0,0,0);stroke-dasharray:2,2");

            svg.appendChild(line);
        }
    }

    void sequence(InteractingEntity parent_entity, LL parent ) {
        LL parentll = null;
        if (parent==null) {
            parentll = create_LL(parent_entity);
            svg.appendChild(parentll.getLlEl());
        } else {
            parentll = parent;
        }
        LL childll = null;

        if (parent_entity.getInteractions()!=null) {
            for (InteractingEntity child : parent_entity.getInteractions()) {
                g_depth++;
                childll = create_LL(child);
                svg.appendChild(childll.getLlEl());
                svg.appendChild(connect(parentll,childll,false,child));
                if (child.getInteractions()!=null) {
                    sequence(child, childll);
                    if (childll!=null) {
                        g_depth++;
                        svg.appendChild(connect(childll, parentll, true, child));
                    }
                } else {
                    g_depth++;
                    svg.appendChild(connect(childll, parentll, true, child));
                }
            }

        }
    }

    OMSVGElement connect(LL originll, LL destinationll, boolean response, final InteractingEntity entity) {
        OMSVGGElement origin = originll.getLlEl();
        OMSVGGElement destination = destinationll.getLlEl();

        InteractingEntity.INTERACTIONSTATUS status = entity.getStatus();

        OMSVGLineElement line = doc.createSVGLineElement();
        int x1 = (Integer.parseInt(((OMSVGRectElement)origin.getFirstChild()).getAttribute("x").toString())+(ll_boxWidth/2));
        line.setAttribute("x1",""+x1);
        int y = Integer.parseInt(((OMSVGRectElement)origin.getFirstChild()).getAttribute("y").toString())+ll_boxHeight+g_depth*connection_topmargin;
        g_y = y;

        line.setAttribute("y1",""+y);
        int x2 = (Integer.parseInt(((OMSVGRectElement)destination.getFirstChild()).getAttribute("x").toString())+(ll_boxWidth/2));
        line.setAttribute("x2",""+x2);
        line.setAttribute("y2",""+y);
        line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:1;" + ((response)?"stroke-dasharray:4,8":""));

        OMSVGGElement group = doc.createSVGGElement();
        group.appendChild(line);

        int centerTextX = (x2+x1)/2;
        int textY = y;

        if (!response) {
            group.setAttribute("style","cursor:pointer");

            if (x2>x1)
                group.appendChild(arrow_request_right(x2, y));
            else
                group.appendChild(arrow_request_left(x2, y));

            final List<String> messages = new ArrayList<String>();
            messages.add("Click to inspect results");
            addTooltip(group,messages,5000);

            String description = entity.getDescription();
            String transaction = entity.getSourceInteractionLabel();
            String[] lines = new String[] {"","",""}; // Length should equal NUM_LINES
            if (description.indexOf("^")>-1) {
                String[] descArray = description.split("\\^");
                lines[0] = descArray[0];
                lines[1] = descArray[1];
            }
            lines[2] = transaction;
            group.appendChild(getTransactionLabel(centerTextX,textY,lines));

            // -----
            group.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    getEventBus().fireEvent(new DiagramClickedEvent(getTestOverviewDTO().getTestInstance(), DiagramPart.RequestConnector));
                }
            } );

        } else {
            group.setAttribute("style","cursor:pointer");

            if (x2<x1)
                group.appendChild(arrow_response_left(x2, y));
            else
                group.appendChild(arrow_response_right(x2, y));

            // -----
            String x_mark_Rgb = null;
            if (InteractingEntity.INTERACTIONSTATUS.ERROR.equals(status)) {
               x_mark_Rgb = "rgb(255,0,0)";
            } else if (InteractingEntity.INTERACTIONSTATUS.ERROR_EXPECTED.equals(status)) {
                x_mark_Rgb = "rgb(0,0,255)";
            }

            if (x_mark_Rgb!=null) {
                final List<String> errors = entity.getErrors();
                group.appendChild(centered_cross_mark(centerTextX,y,x_mark_Rgb));
//                textY -= (half_cross_height); // two lines of text

                if (errors!=null) {
                    addTooltip(group,errors,hide_tooltip_on_mouseout);
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
                    originFrame = getTouchPoint(originll, destinationll, y, x1);
                    originll.getActivityFrames().add(originFrame);
                }


            int lastDestAf = destinationll.getActivityFrames().size()-1;
            TouchPoint destFrame = null;
            if (lastDestAf>-1) {
                destFrame = destinationll.getActivityFrames().get(lastDestAf);
            }
                if (lastDestAf==-1 || (destFrame!=null && destFrame.getY_max()!=0)) {
                    destFrame = getTouchPoint(originll, destinationll, y, x2);
                    destinationll.getActivityFrames().add(destFrame);
                }
        }

        return group;
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

    private TouchPoint getTouchPoint(LL originll, LL destinationll, int y, int x2) {
        TouchPoint destFrame;
        destFrame = new TouchPoint();
        destFrame.setX_min(x2);
        destFrame.setY_min(y);
        destFrame.setY_max(0);
        destFrame.setFrom(originll.getName());
        destFrame.setTo(destinationll.getName());
        return destFrame;
    }

    /**
     * x,y is the center the cross mark
     * @param x
     * @param y
     * @return
     */
    OMSVGGElement centered_cross_mark(int x, int y, String strokeRgb) {
        OMSVGGElement x_group = doc.createSVGGElement();
        OMSVGPathElement l_part = doc.createSVGPathElement();
       l_part.setAttribute("d","M " + (x-half_cross_height) + " " + (y-half_cross_height) // constant is the half-height of the cross
               + " l5 5 l-5 5");
        l_part.setAttribute("style","fill:rgb(255,255,255);stroke-width:1.5;stroke:"+strokeRgb+";stroke-linecap:round");
        x_group.appendChild(l_part);
        OMSVGPathElement r_part = doc.createSVGPathElement();
        r_part.setAttribute("d","M " + (x+half_cross_height) + " " + (y-half_cross_height)
                + " l-5 5 l5 5");
        r_part.setAttribute("style","fill:rgb(255,255,255);stroke-width:1.5;stroke:"+strokeRgb+";stroke-linecap:round");
        x_group.appendChild(r_part);

        return x_group;
    }

    OMSVGTextElement getTransactionLabel(int x, int y, String[] lines) {
        OMSVGTextElement text = doc.createSVGTextElement();
        text.setAttribute("x",""+x);
        text.setAttribute("y",""+(y-(4+(line_height * NUM_LINES)))); // Shift text up the connecting line, 3 for top of the line
        text.setAttribute("text-anchor","middle");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size","10");

        OMSVGTSpanElement line1 = getOmsvgtSpanElement(x, "" + lines[0]);
        text.appendChild(line1);

        OMSVGTSpanElement line2 = getOmsvgtSpanElement(x, "" + lines[1]);
        text.appendChild(line2);

        OMSVGTSpanElement line3 = getOmsvgtSpanElement(x, "" + lines[2]);
        text.appendChild(line3);

       return text;
    }

    private OMSVGTSpanElement getOmsvgtSpanElement(int x, String string2) {
        OMSVGTSpanElement line = doc.createSVGTSpanElement();
        OMText line2Node = doc.createTextNode(getShortLabel(string2));
        line.setAttribute("x",""+x);
        line.setAttribute("dy",""+line_height);
        line.appendChild(line2Node);
        return line;
    }

    String getShortLabel(String label) {
       if (label.length()>MAX_LABEL_DISPLAY_LEN) {
          return label.substring(0,MAX_LABEL_DISPLAY_LEN-3) + "...";
       } else
           return label;
    }


    OMSVGPolygonElement arrow_request_left(int x, int y) {
            x += (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                   "" + x + "," + y
                + " " + (x+5) + "," + (y-5)
                + " " + (x+5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:black");

        return arrow;
    }
    OMSVGPolygonElement arrow_request_right(int x, int y) {
        x -= (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
              "" + x + "," + y
               + " " + (x-5) + "," + (y-5)
               + " " + (x-5) + "," + (y+5)
            );
            arrow.setAttribute("style","fill:black");

        return arrow;
    }

    OMSVGPolygonElement arrow_response_left(int x, int y) {
        x += (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                         " " + (x+5) + "," + (y-5)
                       + " " + x + "," + y
                        + " " + (x+5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:white;stroke:black;stroke-width:1");

        return arrow;
    }
    OMSVGPolygonElement arrow_response_right(int x, int y) {
        x -= (activity_box_width/2);

        OMSVGPolygonElement arrow = doc.createSVGPolygonElement();
        arrow.setAttribute("points",
                " " + (x-5) + "," + (y-5)
                        + " " + x + "," + y
                        + " " + (x-5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:white;stroke:black;stroke-width:1");
        return arrow;
    }

    LL create_LL(InteractingEntity entity) {

        final String name = (entity.getName()==null)?"Toolkit":entity.getName();
        LL ll = getLL(name);

        if (ll!=null)
            return ll;

        ll = new LL();
        ll.setName(name);

        int ll_count = lls.size();
        if (ll_count==0) // First LL being created is the root
            ll.setRoot(true);

        OMSVGRectElement rect = doc.createSVGRectElement();
        rect.setAttribute("width",""+ll_boxWidth);
        rect.setAttribute("height",""+ll_boxHeight);
        int x = ll_count* ll_boxWidth;
        x+=ll_margin*ll_count; // margin that separates this lifeline from the previous one
        g_x = x + ll_boxWidth;
        ll.setLl_stem_center(x + (ll_boxWidth/2));
        rect.setAttribute("x",""+x);
        rect.setAttribute("y","0");
        rect.setAttribute("style","fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)" );

        rect.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.alert(name);
            }
        });

        OMSVGTextElement text = doc.createSVGTextElement();
        text.setAttribute("x",""+ll.getLl_stem_center());
        text.setAttribute("y","" + (ll_boxHeight/2));
        text.setAttribute("dy","3");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size","10");
        text.setAttribute("text-anchor","middle");

        String shortName = name;
        if (name.length()>12)
            shortName = name.substring(0,12);

        OMText textValue = doc.createTextNode(shortName);
        text.appendChild(textValue);

        OMSVGGElement group = doc.createSVGGElement();
        group.appendChild(rect);
        group.appendChild(text);

        ll.setLlEl(group);
        lls.add(ll);


        return ll;
    }

    LL getLL(String name) {
        if (name==null)
            return null;
        for (LL ll : lls) {
            if (name.equals(ll.getName())) {
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

    public boolean hasMeaningfulDiagram() { // At-least two life lines for a meaningful diagram
       return (lls.size()>1);
    }
}
