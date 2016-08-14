package gov.nist.toolkit.interactiondiagram.client;


import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;

/**
 * Created by skb1 on 8/12/2016.
 */
public class InteractionDiagram {

    int g_depth = 0;
    int g_x = 0;
    int g_y = 0;
    int ll_boxWidth = 70;
    int ll_boxHeight = 25;
    int ll_margin = 40;
    int connection_topmargin = 10;
    int diagramHeight = 0;
    int diagramWidth = 0;
    int ll_count = 0;
    int[] ll_stem_center = new int[100]; // extract constant, max 100, only the x coordinate of the center

    final Document doc = XMLParser.createDocument();
    final Element svg = doc.createElement("svg");

    public InteractionDiagram(int diagramHeight, int diagramWidth) {
        setDiagramHeight(diagramHeight);
        setDiagramWidth(diagramWidth);

        svg.setAttribute("height",""+ getDiagramHeight());
        svg.setAttribute("width",""+ getDiagramWidth());
        svg.setNodeValue("Sorry, your browser does not seem to support inline SVG.");
        doc.appendChild(svg);
    }

    public String draw(InteractingEntity parent_entity, int local_depth) {

        sequence(parent_entity,local_depth);
        ll_stem();

        return doc.toString();

    }

    void ll_stem() {

        for (int cx=0; cx<ll_count; cx++) {
            Element line = doc.createElement("line");
            line.setAttribute("x1",""+ll_stem_center[cx]);
            int y = ll_boxHeight;
            line.setAttribute("y1",""+y);
            line.setAttribute("x2",""+ll_stem_center[cx]);
            line.setAttribute("y2",""+(g_y+10));
            line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:3");

            svg.appendChild(line);
        }

    }

    void sequence(InteractingEntity parent_entity, int local_depth) {
        Element parentEl = create_LL(parent_entity);
        svg.appendChild(parentEl);


        g_depth++;
        if (parent_entity.getInteractions()!=null) {
            for (InteractingEntity child : parent_entity.getInteractions()) {
                local_depth++;
                Element childEl = create_LL(child);
                svg.appendChild(childEl);
                svg.appendChild(connect(parentEl,childEl));
                if (child.getInteractions()!=null)
                    draw(child,local_depth);
            }
        }

//        return doc.toString();
    }

    Element connect(Element origin, Element destination) {
        Element line = doc.createElement("line");
        line.setAttribute("x1",""+(Integer.parseInt(origin.getFirstChild().getAttributes().getNamedItem("x").toString())+(ll_boxWidth/2)));
        int y = Integer.parseInt(origin.getFirstChild().getAttributes().getNamedItem("y").toString())+ll_boxHeight+g_depth*connection_topmargin;
        g_y = y;
        line.setAttribute("y1",""+y);
        line.setAttribute("x2",""+(Integer.parseInt(destination.getFirstChild().getAttributes().getNamedItem("x").toString())+(ll_boxWidth/2)));
        line.setAttribute("y2",""+y);
        line.setAttribute("style","stroke:rgb(0,0,0);stroke-width:2");

        return line;
    }

    Element create_LL(InteractingEntity entity) {
        Element rect = doc.createElement("rect");
        rect.setAttribute("width",""+ll_boxWidth);
        rect.setAttribute("height",""+ll_boxHeight);
        int x = ll_count* ll_boxWidth;
        x+=ll_margin*ll_count; // margin that separates this lifeline from the previous one
        ll_stem_center[ll_count] = x + (ll_boxWidth/2);
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

        ll_count++;

        return group;
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
