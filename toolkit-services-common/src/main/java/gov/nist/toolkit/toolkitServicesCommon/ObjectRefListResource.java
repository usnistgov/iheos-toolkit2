package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Not for Public Use
 */
@XmlRootElement
public class ObjectRefListResource implements ObjectRefList {
    List<String> objectRefs = new ArrayList<>();

    @Override
    public List<String> getObjectRefs() {
        return objectRefs;
    }

    public void setObjectRefs(List<String> objectRefs) {
        this.objectRefs = objectRefs;
    }
}
