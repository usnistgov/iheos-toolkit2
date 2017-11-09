package gov.nist.toolkit.xdstools2.client.inspector.mvp.widgets;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ActivityItem;
import org.vectomatic.dom.svg.OMSVGGElement;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.OMSVGTSpanElement;
import org.vectomatic.dom.svg.OMSVGTextElement;
import org.vectomatic.dom.svg.OMText;

import java.util.ArrayList;
import java.util.List;

class ActivityBox {
    GridLayout gridLayout;
    final int l_r_margin = 4; // Left|<-margin-| Box |-margin->|Right
    final int t_b_margin = 20;

    int width;
    int height;

    int horizontalCenter;
    int verticalCenter;

    String id;
    String name;
    boolean root;

    static final int MAX_BOX_DISPLAY_NAME = 14;
    private static final int HIDE_TOOLTIP_ON_MOUSEOUT = -1;
    static final int LINE_HEIGHT = 13;

    OMSVGGElement boxGroupEl; // box svg group element
    private ToolTip tooltip = new ToolTip();

    public ActivityBox(GridLayout gridLayout, ActivityItem activityItem) {
        this.gridLayout = gridLayout;
        width = gridLayout.getGridUnitSize() - l_r_margin * 2;
        height = gridLayout.getGridUnitSize() - t_b_margin * 2;

        create_Box(activityItem);
    }


    void create_Box(ActivityItem activityItem) {
       name = activityItem.getTransaction();
       // TODO: does activityItem really need an Id? ActivityBox is the only place it should be.
       id = activityItem.getId();

        OMSVGRectElement rect = gridLayout.getSvgDoc().createSVGRectElement();
        rect.setAttribute("width", ""+width);
        rect.setAttribute("height", ""+height);

        int x = gridLayout.getGridUnitSize()*gridLayout.getRow()+l_r_margin;
        int y = gridLayout.getGridUnitSize()*gridLayout.getColumn()+t_b_margin;
        rect.setAttribute("x",""+x);
        rect.setAttribute("y",""+y);

        horizontalCenter = x + (width/2);
        verticalCenter = y + (height/2);

        String boxRgb = "rgb(255,255,255);";
        rect.setAttribute("style","fill:"+boxRgb+";stroke-width:2;stroke:rgb(0,0,0)" );

        OMSVGTextElement text = gridLayout.getSvgDoc().createSVGTextElement();
        text.setAttribute("x",""+horizontalCenter);
        text.setAttribute("y",""+verticalCenter);
        text.setAttribute("dy","3");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size","10");
        text.setAttribute("text-anchor","middle");

        String shortName = getShortName(name, MAX_BOX_DISPLAY_NAME);
        OMText textValue = gridLayout.getSvgDoc().createTextNode(shortName);
        text.appendChild(textValue);

        OMSVGGElement group = gridLayout.getSvgDoc().createSVGGElement();

        List<String> boxDetail = new ArrayList<>();
        boxDetail.add(activityItem.getOutput().timestamp);
        boxDetail.add(activityItem.getTransaction());
//        boxDetail.add(activityItem.getId());

        addTooltip(group,boxDetail, HIDE_TOOLTIP_ON_MOUSEOUT);

        group.appendChild(rect);

        List<String> box_label = new ArrayList<>();
        box_label.add(activityItem.getOutput().testInstance.getId());
        if (activityItem.getOutput().d)
        box_label.add()
        group.appendChild(multiLineLabel(horizontalCenter,verticalCenter,box_label.toArray(new String[0]),9, MAX_BOX_DISPLAY_NAME));

        boxGroupEl = group;
    }

    // **************************************
    // Need to refactor common methods below.
    // **************************************

    private String getShortName(String name, int truncateTo) {
        if (name!=null && name.length()> truncateTo) {
            return name.substring(0, truncateTo-3) + "...";
        }
        else
            return ""+name;
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
                tooltip.hide();
            }
        });
    }

    private void showTooltip(MouseEvent<? extends EventHandler> e, List<String> text, int milliseconds) {
        tooltip.show(e.getClientX() + 20, e.getClientY() + 30, text, milliseconds);
    }

    OMSVGTextElement multiLineLabel(int x, int y, String[] lines, int fontSize, int maxLineLen) {
        OMSVGTextElement text = getOmsvgTextElement(x, y, fontSize);

        for (String line : lines) {
            OMSVGTSpanElement line1 = getOmsvgtSpanElement(x,  getShortName(line,maxLineLen), ((lines.length>1)?LINE_HEIGHT:0));
            text.appendChild(line1);
        }

        return text;
    }

    private OMSVGTSpanElement getOmsvgtSpanElement(int x, String string, int lineHeight) {
        OMSVGTSpanElement line = gridLayout.getSvgDoc().createSVGTSpanElement();
        OMText line2Node = gridLayout.getSvgDoc().createTextNode(getShortName(string, MAX_BOX_DISPLAY_NAME));
        line.setAttribute("x",""+x);
        line.setAttribute("dy",""+ lineHeight);
        line.appendChild(line2Node);
        return line;
    }

    private OMSVGTextElement getOmsvgTextElement(int x, int y, int fontSize) {
        OMSVGTextElement text = gridLayout.getSvgDoc().createSVGTextElement();
        text.setAttribute("x",""+x);
        text.setAttribute("y",""+y); // Shift text up the connecting line, 3 for top of the line
        text.setAttribute("text-anchor","middle");
        text.setAttribute("font-family","Verdana");
        text.setAttribute("font-size",""+fontSize);
        return text;
    }
    // Need to refactor common methods.
    // Do not add methods below.
}

