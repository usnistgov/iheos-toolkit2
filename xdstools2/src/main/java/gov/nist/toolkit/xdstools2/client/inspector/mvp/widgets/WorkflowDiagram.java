package gov.nist.toolkit.xdstools2.client.inspector.mvp.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ActivityItem;
import org.vectomatic.dom.svg.OMSVGDocument;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

public class WorkflowDiagram extends Composite implements GridLayout {

    static final int gridUnitSize = 90;

    ActivityItem activityItem;

    private int diagramHeight = 0;
    private int diagramWidth = 0;

    private int row = 0;
    private int column = 0;

    /**
     * The main SVG document
     */
    private final OMSVGDocument doc = OMSVGParser.createDocument();
    /**
     * The main root element
     */
    private OMSVGSVGElement svgsvgElement =  doc.createSVGSVGElement();


    @Override
    public int getGridUnitSize() {
        return gridUnitSize;
    }

    @Override
    public OMSVGDocument getSvgDoc() {
        return doc;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getDiagramHeight() {
        return (getColumn()+1) * getGridUnitSize();
    }

    @Override
    public int getDiagramWidth() {
        return (getRow()+1) * getGridUnitSize();
    }

    public WorkflowDiagram() {
    }


    public WorkflowDiagram(ActivityItem activityItem) {
        this.activityItem = activityItem;

        FlowPanel container = new FlowPanel();
        container.getElement().appendChild(svgsvgElement.getElement());

        draw();

        initWidget(container);
    }


    void draw() {

        calcMargins();

        sequence(activityItem, null);

        legend();

        setDiagramArea();
    }

    /**
     * Puts together sequence of activities in a visual form (using SVG).
     */
    private void sequence(ActivityItem activityItem, ActivityBox parentBox) {

        if (parentBox == null) {
            parentBox = new ActivityBox(this, activityItem);
            svgsvgElement.appendChild(parentBox.boxGroupEl);
        }

        if (activityItem.getActionItems()!=null) {
            column++;
           for (ActivityItem action : activityItem.getActionItems()) {
//               if (!activityItem.getTransaction().equals(action.getTransaction())) {
               row++;
//               }
               ActivityBox actionBox = new ActivityBox(this, action);
               svgsvgElement.appendChild(actionBox.boxGroupEl);
               if (action.getActionItems()!=null) {
                  sequence(action, actionBox);
               }
           }
        }

    }

    private void legend() {

    }

    private void calcMargins() {
    }

    private void setDiagramArea() {

        svgsvgElement.setAttribute("xmlns","http://www.w3.org/2000/svg");
        svgsvgElement.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
        svgsvgElement.setAttribute("height",""+ getDiagramHeight());
        svgsvgElement.setAttribute("width",""+ getDiagramWidth());
        svgsvgElement.setNodeValue("Sorry, your browser does not seem to support inline SVG.");
    }




}
