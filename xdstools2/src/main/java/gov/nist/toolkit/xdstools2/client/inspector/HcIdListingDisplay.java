package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HcIdListingDisplay {

    /** Parent tab for this display */
    MetadataInspectorTab tab;
    /** DataModel for this display, could be test or step */
    DataModel data;
    /** Tree element serving as root for this display */
    TreeThing root;

    DataModel nonHcIddata;




    Map<String, ListingDisplay> hcIdListings = new HashMap<String, ListingDisplay>();

    public HcIdListingDisplay(MetadataInspectorTab tab, DataModel data, TreeThing root) {
        this.tab = tab;
        this.data = data;
        this.root = root;

        if (spliceByHcId()) {
            Map<String, ListingDisplay> treeMap = new TreeMap<String, ListingDisplay>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String o1, String o2) {
                            return o2.compareTo(o1);
                        }

                    });

            treeMap.putAll(hcIdListings);

            for (Map.Entry<String, ListingDisplay> entry : treeMap.entrySet()) {

                TreeItem hcIdTreeItem = new TreeItem(new HTML(entry.getKey()));

                ListingDisplay listingDisplay = entry.getValue();

                this.root.addItem(hcIdTreeItem);

                listingDisplay.root = new TreeThing(hcIdTreeItem);
                listingDisplay.listing();
            }
        } else {
            new ListingDisplay(tab, data, root).listing();
        }





    }




    private boolean spliceByHcId() {

        for (SubmissionSet o : data.combinedMetadata.submissionSets) {
            if (o.home!=null && !"".equals(o.home)) {
                String hcId = o.home;

                ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                listingDisplay.data.combinedMetadata.submissionSets.add(o);
            } else {
                nonHcIddata.combinedMetadata.submissionSets.add(o);
            }
        }

        for (Folder o : data.combinedMetadata.folders) {
            if (o.home!=null && !"".equals(o.home)) {
                String hcId = o.home;

                ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                listingDisplay.data.combinedMetadata.folders.add(o);
            } else {
                nonHcIddata.combinedMetadata.folders.add(o);
            }
        }

        for (DocumentEntry o : data.combinedMetadata.docEntries) {
            if (o.home!=null && !"".equals(o.home)) {
                String hcId = o.home;

                ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                listingDisplay.data.combinedMetadata.docEntries.add(o);
            } else {
                nonHcIddata.combinedMetadata.docEntries.add(o);
            }
        }

        for (Association o : data.combinedMetadata.assocs) {
            if (o.home!=null && !"".equals(o.home)) {
                String hcId = o.home;

                ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                listingDisplay.data.combinedMetadata.assocs.add(o);
            } else {
                nonHcIddata.combinedMetadata.assocs.add(o);
            }
        }

        if (data.combinedMetadata.objectRefs.size() > 0) {
                for (ObjectRef o : data.combinedMetadata.objectRefs) {
                    if (o.home!=null && !"".equals(o.home)) {
                        String hcId = o.home;

                        ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                        listingDisplay.data.combinedMetadata.objectRefs.add(o);
                    } else {
                        nonHcIddata.combinedMetadata.objectRefs.add(o);
                    }
                }
        }

        if (data.allDocs != null && !data.allDocs.isEmpty()) {
           for (Document o : data.allDocs)  {
               if (o.homeCommunityId!=null && !"".equals(o.homeCommunityId)) {
                   String hcId = o.homeCommunityId;

                   ListingDisplay listingDisplay = getListingDisplayByHcId(hcId);
                   listingDisplay.data.allDocs.add(o);
               } else {
                   nonHcIddata.allDocs.add(o);
               }
           }
        }

        return hcIdListings.size()>0;
    }



    ListingDisplay getListingDisplayByHcId(String hcId) {
       if (hcIdListings.containsKey(hcId))  {
           return hcIdListings.get(hcId);
       } else {
           return hcIdListings.put(hcId, new ListingDisplay(tab,new DataModel(),root));
       }

    }



}
