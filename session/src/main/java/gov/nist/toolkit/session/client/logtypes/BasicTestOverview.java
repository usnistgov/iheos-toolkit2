package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.util.List;

/**
 *
 */
public interface BasicTestOverview extends IsSerializable {
    TestInstance getTestInstance();
    List<String> getSectionNames();
    BasicSectionOverview getSectionOverview(String sectionName);
}
