package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Only checks the "easy" attributes for now.
 */
public class DocumentEntryDiff extends MetadataDiffBase implements IsSerializable, Compare {
    /**
     * Exclude metadata attributes which are not handled by current implementation of metadata update
     */
    boolean updateMode = false;

    public DocumentEntryDiff() {
    }

    public DocumentEntryDiff(boolean updateMode) {
        this.updateMode = updateMode;
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
        if (!updateMode) {
            if (dif(a.objectType, b.objectType)) {
                differences.add(new Difference("objectType"));
            }
        }

        if (dif(a.title, b.title)) {
            differences.add(new Difference("title"));
        }

        if (dif(a.comments, b.comments)) {
            differences.add(new Difference("comments"));
        }

        if (!updateMode) {
            if (dif(a.id, b.id)) {
                differences.add(new Difference("id"));
            }
        }

        if (!updateMode) {
            if (dif(a.lid, b.lid)) {
                differences.add(new Difference("lid"));
            }
        }

        if (!updateMode) {
            if (dif(a.version, b.version)) {
                differences.add(new Difference("version"));
            }
        }

        if (!updateMode) {
            if (dif(a.uniqueId, b.uniqueId)) {
                differences.add(new Difference("uniqueId"));
            }
        }

         if (!updateMode) {
             if (dif(a.patientId, b.patientId)) {
                 differences.add(new Difference("patientId"));
             }
         }

        if (!updateMode) {
            if (dif(a.status, b.status)) {
                differences.add(new Difference("availabilityStatus"));
            }
        }

        if (!updateMode) {
            if (dif(a.home, b.home)) {
                differences.add(new Difference("homeCommunityId"));
            }
        }

        if (!updateMode) {
            if (dif(a.mimeType, b.mimeType)) {
                differences.add(new Difference("mimeType"));
            }
        }

        if (!updateMode) {
            if (dif(a.hash, b.hash)) {
                differences.add(new Difference("hash"));
            }
        }

        if (!updateMode) {
            if (dif(a.size, b.size)) {
                differences.add(new Difference("size"));
            }
        }

        if (!updateMode) {
            if (dif(a.repositoryUniqueId, b.repositoryUniqueId)) {
                differences.add(new Difference("repositoryUniqueId"));
            }
        }

        if (dif(a.lang, b.lang)) {
            differences.add(new Difference("lang"));
        }

        if (dif(a.legalAuth, b.legalAuth)) {
            differences.add(new Difference("legalAuthenticator"));
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

        if (!updateMode) {
            if (dif(a.sourcePatientId, b.sourcePatientId)) {
                differences.add(new Difference("sourcePatientId"));
            }
        }

        if (dif(a.sourcePatientInfo, b.sourcePatientInfo)) {
            differences.add(new Difference("sourcePatientInfo"));
        }

        if (dif(a.classCode, b.classCode)) {
            differences.add(new Difference("classCode"));
        }

        if (dif(a.confCodes, b.confCodes)) {
            differences.add(new Difference("confCodes"));
        }

        if (dif(a.eventCodeList, b.eventCodeList)) {
            differences.add(new Difference("eventCodeList"));
        }

        if (dif(a.formatCode, b.formatCode)) {
            differences.add(new Difference("formatCode"));
        }

        if (dif(a.hcftc, b.hcftc)) {
            differences.add(new Difference("healthcareFacilityType"));
        }

        if (dif(a.pracSetCode, b.pracSetCode)) {
            differences.add(new Difference("practiceSetting"));
        }

        if (dif(a.typeCode, b.typeCode)) {
            differences.add(new Difference("typeCode"));
        }

        if (difa(a.authors, b.authors)) {
            differences.add(new Difference("author"));
        }


        // TODO how to compare extra metadata?
        // TODO referenceIdList when it is available to compare. Update the tooltip on highlightDifferences checkbox in DataTable.


        return differences;
    }

}
