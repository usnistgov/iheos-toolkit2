package gov.nist.toolkit.desktop.client.framework;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitPlace;

/**
 * Monitors PlaceChangeEvents and History events and keep them in sync.
 * It must know all the different Places used in the application.
 */
@WithTokenizers({ ToolkitPlace.Tokenizer.class})
public interface ToolkitPlaceHistoryMapper extends PlaceHistoryMapper {
}
