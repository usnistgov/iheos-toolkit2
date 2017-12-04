package gov.nist.toolkit.xdstools2.client.inspector.mvp.widgets;

import org.vectomatic.dom.svg.OMSVGDocument;

public interface GridLayout {

   int getGridUnitSize();
   OMSVGDocument getSvgDoc();

   int getRow();
   int getColumn();

   int getDiagramHeight();
   int getDiagramWidth();
}
