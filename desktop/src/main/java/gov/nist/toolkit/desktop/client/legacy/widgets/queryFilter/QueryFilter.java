package gov.nist.toolkit.desktop.client.legacy.widgets.queryFilter;

import java.util.List;
import java.util.Map;

/**
 * Created by bill on 9/1/15.
 */
public interface QueryFilter {

    /**
     * Adds the values selected by the user to a list of accepted values for the document search
     */
    void addToCodeSpec(Map<String, List<String>> codeSpec, String codeType);
}
