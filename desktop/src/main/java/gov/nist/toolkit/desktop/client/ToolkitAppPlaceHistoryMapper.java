package gov.nist.toolkit.desktop.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import gov.nist.toolkit.desktop.client.home.WelcomePlace;
import gov.nist.toolkit.desktop.client.tools.toy.Toy;

/**
 * This is where you index all the Places that participate in Activities
 * and Places, the GWT architecture.  Technically, it is the parser (tokenizer)
 * for the Places that is indexed here.
 */
@WithTokenizers({WelcomePlace.Tokenizer.class, Toy.Tokenizer.class})
public interface ToolkitAppPlaceHistoryMapper extends PlaceHistoryMapper {
}
