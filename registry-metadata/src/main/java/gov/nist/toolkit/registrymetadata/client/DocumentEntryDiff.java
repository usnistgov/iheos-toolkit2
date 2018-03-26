package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class DocumentEntryDiff extends MetadataDiffBase implements IsSerializable, Compare {

    public DocumentEntryDiff() {
    }

    @Override
    public String getComparisonObjectType() {
        return "DocumentEntry";
    }

    public List<Difference> compare(MetadataObject left, MetadataObject right) {
        List<Difference> differences = new ArrayList<>();

        if (!(left instanceof DocumentEntry) || !(right instanceof DocumentEntry)) {
            throw new ToolkitRuntimeException("Incompatible comparision object types");
        }

        DocumentEntry a = (DocumentEntry) left;
        DocumentEntry b = (DocumentEntry) right;


        if (dif(a.title, b.title)) {
            differences.add(new Difference("title"));
        }

        return differences;
    }

}
