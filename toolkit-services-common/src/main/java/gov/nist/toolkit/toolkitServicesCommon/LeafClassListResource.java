package gov.nist.toolkit.toolkitServicesCommon;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@XmlRootElement
public class LeafClassListResource implements LeafClassList {
    List<String> leafClasses = new ArrayList<>();

    @Override
    public List<String> getLeafClasses() {
        return leafClasses;
    }

    public void setLeafClasses(List<String> leafClasses) { this.leafClasses = leafClasses; }
}
