package gov.nist.toolkit.registrymetadata;

import org.apache.axiom.om.OMElement;

/**
 * Created by bill on 6/29/15.
 */
public class MetadataUtil {

    public static String formatObjectIdentity(OMElement ele) {
        Metadata m = new Metadata();
        String id = m.getId(ele);

        if (id != null && !id.equals(""))
            return ele.getLocalName() + "(id=" + id + ")";

        String title = m.getTitleValue(ele);
        if (title != null && !title.equals(""))
            return ele.getLocalName() + "(title=" + title + ")";

        return ele.getLocalName() + "(??)";
    }
}
