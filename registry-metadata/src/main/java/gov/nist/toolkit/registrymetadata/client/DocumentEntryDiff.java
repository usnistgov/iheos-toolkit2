package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Only checks the "easy" attributes for now.
 */
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


        // TODO: To use metadata attribute constants from CodesConfiguration, the classes need to be relocated to a module that is accessible.
        if (dif(a.title, b.title)) {
            differences.add(new Difference("title"));
        }

        if (dif(a.comments, b.comments)) {
            differences.add(new Difference("comments"));
        }

        if (dif(a.lang, b.lang)) {
            differences.add(new Difference("languageCode"));
        }

        if (dif(a.serviceStartTime, b.serviceStartTime)) {
            differences.add(new Difference("serviceStartTime"));
        }

        if (dif(a.serviceStopTime, b.serviceStopTime)) {
            differences.add(new Difference("serviceStopTime"));
        }

        if (dif(a.creationTime, b.creationTime)) {
            differences.add(new Difference("creationTime"));
        }

        if (dif(a.classCode, b.classCode)) {
            differences.add(new Difference("classCode"));
        }

        if (dif(a.confCodes, b.confCodes)) {
            differences.add(new Difference("confidentialityCode"));
        }

        if (dif(a.eventCodeList, b.eventCodeList)) {
            differences.add(new Difference("eventCodeList"));
        }

        if (dif(a.formatCode, b.formatCode)) {
            differences.add(new Difference("formatCode"));
        }

        if (dif(a.hcftc, b.hcftc)) {
            differences.add(new Difference("healthcareFacilityTypeCode"));
        }

        if (dif(a.pracSetCode, b.pracSetCode)) {
            differences.add(new Difference("practiceSettingCode"));
        }

        if (dif(a.typeCode, b.typeCode)) {
            differences.add(new Difference("typeCode"));
        }

        if (difa(a.authors, b.authors)) {
            differences.add(new Difference("Author"));
        }

        // TODO: add more fields to compare.

        return differences;
    }

}