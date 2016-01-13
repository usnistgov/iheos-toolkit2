package gov.nist.toolkit.toolkitServicesCommon.resource;

import gov.nist.toolkit.toolkitServicesCommon.RefList;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Not for Public Use
 */
@XmlRootElement
public class RefListResource implements RefList {
    List<String> refs = new ArrayList<String>();

    public List<String> getRefs() {
        return refs;
    }

    public void setRefs(List<String> refs) {
        this.refs = refs;
    }

    public void addRef(String ref) { refs.add(ref); }
}
