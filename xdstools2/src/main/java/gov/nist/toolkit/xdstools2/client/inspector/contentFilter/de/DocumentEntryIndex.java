package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentEntryIndex {

    public static Map<DocumentEntryIndexField, Map<IndexFieldValue, List<DocumentEntry>>> indexMap(List<DocumentEntry> deList) {
        final Map<DocumentEntryIndexField, Map<IndexFieldValue, List<DocumentEntry>>> fieldMap = new HashMap<>();
        for (DocumentEntry de : deList) {
            indexField(fieldMap, de, DocumentEntryIndexField.STATUS, de.status);
        }
        return fieldMap;
    }


    private static void indexField(Map<DocumentEntryIndexField, Map<IndexFieldValue, List<DocumentEntry>>> fieldMap, DocumentEntry de, DocumentEntryIndexField indexField, String valueStr) {
        IndexFieldValue value = new IndexFieldValue(valueStr);
        if (isNotNullValue(value)) {
            if (!fieldMap.containsKey(indexField)) {
                fieldMap.put(indexField, new HashMap<IndexFieldValue, List<DocumentEntry>>());
            }
            Map<IndexFieldValue,List<DocumentEntry>> vMap = fieldMap.get(indexField);
            if (!vMap.containsKey(value)) {
                vMap.put(value, new ArrayList<DocumentEntry>());
            }
            vMap.get(value).add(de);
        }
    }


    static boolean isNotNullValue(IndexFieldValue ifv) {
        return isNotNullString(ifv.toString());
    }

    static boolean isNotNullString(String str) {
        return (str!=null && !"".equals(str));
    }
}
