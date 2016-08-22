package gov.nist.toolkit.interactiondiagram.client;


import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 Sunil.Bhaskarla on 8/12/2016.
 */
public class InteractionDiagram {

    public static final int LL_MAX = 100;
    int g_depth = 0;
    int g_x = 0;
    int g_y = 0;
    int ll_boxWidth = 70;
    int ll_boxHeight = 25;
    int ll_margin = 40;
    int connection_topmargin = 22;
    int diagramHeight = 0;
    int diagramWidth = 0;

    final Document doc = XMLParser.createDocument();
    final Element svg = doc.createElement("svg");


    /**
     * Touch points of a life line (LL)
     * Touch point means a contact point either a request/response touching the LL.
     * This is used to draw active portion of the LL as a interaction bounding vertical box.
     */
    class TouchPoint {
        int x_min;
        int y_min;
        int y_max;

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
    }
    int activity_box_width = 8;
    /**
     * Life line
     */
    class LL {

        int ll_stem_center; // only the x coordinate of the center
        Element llEl; // life line element
        TouchPoint tp = new TouchPoint();

        public LL() {
        }

        public int getLl_stem_center() {
            return ll_stem_center;
        }

        public void setLl_stem_center(int ll_stem_center) {
            this.ll_stem_center = ll_stem_center;
        }

        public Element getLlEl() {
            return llEl;
        }

        public void setLlEl(Element llEl) {
            this.llEl = llEl;
        }

        public TouchPoint getTp() {
            return tp;
        }

        public void setTp(TouchPoint tp) {
            this.tp = tp;
        }
    }

    List<LL> lls = new ArrayList<LL>();

    public InteractionDiagram(int diagramHeight, int diagramWidth) {
        setDiagramHeight(diagramHeight);
        setDiagramWidth(diagramWidth);

        svg.setAttribute("height",""+ getDiagramHeight());
        svg.setAttribute("width",""+ getDiagramWidth());
        svg.setNodeValue("Sorry, your browser does not seem to support inline SVG.");
        doc.appendChild(svg);
    }

    public String draw(InteractingEntity parent_entity, int local_depth) {

        sequence(parent_entity,null);
        ll_stem();
        ll_activitybox();

        return doc.toString();

    }

    void ll_activitybox() {
            for (LL ll : lls) {
                Element box = doc.createElement("rect");
                box.setAttribute("width", "" + activity_box_width);
                box.setAttribute("height", "" + (ll.getTp().getY_max() - ll.getTp().getY_min()));
                box.setAttribute("x", "" + (ll.getTp().getX_min()-(activity_box_width/2)));
                box.setAttribute("y", "" + ll.getTp().getY_min());
                box.setAttribute("style", "fill:rgb(255,255,255);stroke-width:1;stroke:rgb(0,0,0)");

                svg.appendChild(box);
            }
    }

    void ll_stem() {

        for (LL ll: lls) {
            Element line = doc.createElement("line");
            line.setAttribute("x1",""+ll.getLl_stem_center());
            int y = ll_boxHeight;
            line.setAttribute("y1",""+y);
            line.setAttribute("x2",""+ll.getLl_stem_center());
            line.setAttribute("y2",""+(g_y+10));
            line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:2;" + ((lls.size()>2)?"stroke-dasharray:2,2":""));

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
            int childCt = 0;
            for (InteractingEntity child : parent_entity.getInteractions()) {
                g_depth++;
                childCt++;
                childll = create_LL(child);
                svg.appendChild(childll.getLlEl());
                svg.appendChild(connect(parentll,childll,false));
                if (child.getInteractions()!=null) {
                    sequence(child, childll);
                    if (childll!=null) {
                        g_depth++;
                        svg.appendChild(connect(childll, parentll, true));
                    }
                } else {
                    g_depth++;
                    svg.appendChild(connect(childll, parentll, true));
                }
            }

        }
    }

    Element connect(LL originll, LL destinationll, boolean response) {
        Element origin = originll.getLlEl();
        Element destination = destinationll.getLlEl();

        Element line = doc.createElement("line");
        int x1 = (Integer.parseInt(origin.getFirstChild().getAttributes().getNamedItem("x").toString())+(ll_boxWidth/2));
        line.setAttribute("x1",""+x1);
        int y = Integer.parseInt(origin.getFirstChild().getAttributes().getNamedItem("y").toString())+ll_boxHeight+g_depth*connection_topmargin;
//        if (response)
//            y = y+ connection_topmargin;
        g_y = y;
        line.setAttribute("y1",""+y);
        int x2 = (Integer.parseInt(destination.getFirstChild().getAttributes().getNamedItem("x").toString())+(ll_boxWidth/2));
        line.setAttribute("x2",""+x2);
        line.setAttribute("y2",""+y);
        line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:1;" + ((response)?"stroke-dasharray:4,8":""));

        Element group = doc.createElement("g");
        group.appendChild(line);
        if (!response)
            group.appendChild(arrow_request(x2,y));
        else
            group.appendChild(arrow_response(x2,y));

        // Touch points
        // Origin y
        if (originll.getTp().getY_min()==0 || y<originll.getTp().getY_min())
            originll.getTp().setY_min(y);
        if (originll.getTp().getY_max()==0 || y>originll.getTp().getY_max())
            originll.getTp().setY_max(y);
        if (originll.getTp().getX_min()==0 || x1<originll.getTp().getX_min())
            originll.getTp().setX_min(x1);
        // Destination y
        if (destinationll.getTp().getY_min()==0 || y<destinationll.getTp().getY_min())
            destinationll.getTp().setY_min(y);
        if (destinationll.getTp().getY_max()==0 || y>destinationll.getTp().getY_max())
            destinationll.getTp().setY_max(y);

        return group;
    }

    Element arrow_request(int x, int y) {
        x -= (activity_box_width/2);
        Element arrow = doc.createElement("polygon");
        arrow.setAttribute("points",
                   "" + x + "," + y
                + " " + (x-5) + "," + (y-5)
                + " " + (x-5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:black");
        return arrow;
    }

    Element arrow_response(int x, int y) {
        x += (activity_box_width/2);
        Element arrow = doc.createElement("polygon");
        arrow.setAttribute("points",
                         " " + (x+5) + "," + (y-5)
                       + " " + x + "," + y
                        + " " + (x+5) + "," + (y+5)
        );
        arrow.setAttribute("style","fill:white;stroke:black;stroke-width:1");
        return arrow;
    }

    LL create_LL(InteractingEntity entity) {

        LL ll = new LL();
        int ll_count = lls.size();

        Element rect = doc.createElement("rect");
        rect.setAttribute("width",""+ll_boxWidth);
        rect.setAttribute("height",""+ll_boxHeight);
        int x = ll_count* ll_boxWidth;
        x+=ll_margin*ll_count; // margin that separates this lifeline from the previous one
        ll.setLl_stem_center(x + (ll_boxWidth/2));
        rect.setAttribute("x",""+x);
        rect.setAttribute("y","0");
        rect.setAttribute("style","fill:rgb(255,255,255);stroke-width:2;stroke:rgb(0,0,0)" );

        Element text = doc.createElement("text");
        text.setAttribute("x",""+x);
        text.setAttribute("y","10");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size","10");

        String name = (entity.getName()==null)?"Toolkit":entity.getName();
        Text textValue = doc.createTextNode(name);
        text.appendChild(textValue);

        Element group = doc.createElement("g");
        group.appendChild(rect);
        group.appendChild(text);

        ll.setLlEl(group);
        lls.add(ll);

        return ll;
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
