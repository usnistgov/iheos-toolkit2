package gov.nist.toolkit.xdstools2.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by onh2 on 3/16/2016.
 */
public interface DiagramResources extends ClientBundle {
    DiagramResources INSTANCE = GWT.create(DiagramResources.class);

    // ----- Load icons -----//
    @Source("diagrams/ig_diagram.png")
    ImageResource getInitiatingGatewayDiagram();
}
