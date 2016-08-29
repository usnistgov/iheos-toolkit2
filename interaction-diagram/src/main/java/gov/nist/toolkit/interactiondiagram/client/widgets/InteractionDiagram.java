package gov.nist.toolkit.interactiondiagram.client.widgets;


import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.session.client.SectionOverviewDTO;
import gov.nist.toolkit.session.client.TestOverviewDTO;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGElement;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGLineElement;
import org.vectomatic.dom.svg.OMSVGPolygonElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.OMText;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 Sunil.Bhaskarla on 8/12/2016.
 */
public class InteractionDiagram extends Composite {

    int g_depth = 0;
    int g_x = 0;
    int g_y = 0;
    int ll_boxWidth = 70;
    int ll_boxHeight = 25;
    int ll_margin = 40;
    int connection_topmargin = 22;
    int diagramHeight = 0;
    int diagramWidth = 0;

    OMSVGDocument doc = OMSVGParser.createDocument();
    OMSVGSVGElement svg =  doc.createSVGSVGElement();

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

    public InteractionDiagram(TestOverviewDTO testOverviewDTO) {
        setDiagramArea(diagramHeight, diagramWidth);

        List<InteractingEntity> interactingEntity = transformTestResultToInteractingEntity(testOverviewDTO);
        if (interactingEntity==null)
            return;

        Element svg = draw(interactingEntity);
        setDiagramArea(g_y+10,g_x);

        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svg);

        initWidget(container);
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
                destination.setSourceInteractionLabel(sectionOverviewDTO.getName());
                if (sectionOverviewDTO.isPass())
                    destination.setStatus(InteractingEntity.INTERACTIONSTATUS.COMPLETED);
                else
                    destination.setStatus(InteractingEntity.INTERACTIONSTATUS.ERROR);
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
            line.setAttribute("y2",""+(g_y+10));
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
                svg.appendChild(connect(parentll,childll,false,child.getSourceInteractionLabel(),child.getStatus()));
                if (child.getInteractions()!=null) {
                    sequence(child, childll);
                    if (childll!=null) {
                        g_depth++;
                        svg.appendChild(connect(childll, parentll, true, child.getSourceInteractionLabel(), child.getStatus()));
                    }
                } else {
                    g_depth++;
                    svg.appendChild(connect(childll, parentll, true, child.getSourceInteractionLabel(), child.getStatus()));
                }
            }

        }
    }

    OMSVGElement connect(LL originll, LL destinationll, boolean response, String description, InteractingEntity.INTERACTIONSTATUS status) {
        OMSVGGElement origin = originll.getLlEl();
        OMSVGGElement destination = destinationll.getLlEl();

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

        if (!response) {
            if (x2>x1)
                group.appendChild(arrow_request_right(x2, y));
            else
                group.appendChild(arrow_request_left(x2, y));

            // Set description
            OMSVGTextElement text = doc.createSVGTextElement();
            int centerTextX = (x2+x1)/2;
            text.setAttribute("x",""+centerTextX);
            text.setAttribute("y",""+y);
            text.setAttribute("dy","-"+3); // Shift text up the connecting line
            text.setAttribute("text-anchor","middle");
            text.setAttribute("font-family","Verdana");
            text.setAttribute("font-size","10");
            if (InteractingEntity.INTERACTIONSTATUS.ERROR.equals(status))
                text.setAttribute("fill","red");

            String shortDesc = description;
            if (description!=null && description.length()>21)
                shortDesc = description.substring(0,18) + "..."; // Truncate so the text does not overflow
            OMText textValue = doc.createTextNode(shortDesc);
            text.appendChild(textValue);
            group.appendChild(text);

        } else {
            if (x2<x1)
                group.appendChild(arrow_response_left(x2, y));
            else
                group.appendChild(arrow_response_right(x2, y));
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
                   originFrame = new TouchPoint();
                    originFrame.setX_min(x1);
                    originFrame.setY_min(y);
                    originFrame.setY_max(0);
                    originFrame.setFrom(originll.getName());
                    originFrame.setTo(destinationll.getName());
                    originll.getActivityFrames().add(originFrame);
                }


            int lastDestAf = destinationll.getActivityFrames().size()-1;
            TouchPoint destFrame = null;
            if (lastDestAf>-1) {
                destFrame = destinationll.getActivityFrames().get(lastDestAf);
            }
                if (lastDestAf==-1 || (destFrame!=null && destFrame.getY_max()!=0)) {
                    destFrame = new TouchPoint();
                    destFrame.setX_min(x2);
                    destFrame.setY_min(y);
                    destFrame.setY_max(0);
                    destFrame.setFrom(originll.getName());
                    destFrame.setTo(destinationll.getName());
                    destinationll.getActivityFrames().add(destFrame);
                }




        }

        return group;
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

}
