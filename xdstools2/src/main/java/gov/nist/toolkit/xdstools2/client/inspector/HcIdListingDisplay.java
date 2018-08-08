package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.results.client.StepResult;

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

    public HcIdListingDisplay(MetadataInspectorTab tab, DataModel data, TreeThing root, StepResult stepResult) {
        this.tab = tab;
        this.data = data;
        this.root = root;

        if (spliceByHcId()) {
            Map<String, ListingDisplay> treeMap = new TreeMap<>(hcIdListings);

            for (Map.Entry<String, ListingDisplay> entry : treeMap.entrySet()) {

                TreeItem hcIdTreeItem = new TreeItem(new HTML(entry.getKey()));

                ListingDisplay listingDisplay = entry.getValue();

                this.root.addItem(hcIdTreeItem);

                listingDisplay.root = new TreeThing(hcIdTreeItem);
                listingDisplay.listing();



                if (data.enableActions && stepResult.toBeRetrieved.size() > 0) {

                    StepResult hcIdStepResult = stepResult.clone();

                    for (ObjectRef o : stepResult.toBeRetrieved) {
                       if (!o.home.equals(entry.getKey()))  {
                            hcIdStepResult.toBeRetrieved.remove(o);
                       }
                    }
                    ObjectRefs ors = hcIdStepResult.nextNObjectRefs(10);

                    if (ors.objectRefs.size()>0) {
//                        GWT.log("In HcIdListing listingDisplay.data.siteSpec is " + listingDisplay.data.siteSpec);
                        TreeItem getNextItem = new TreeItem(HyperlinkFactory.getDocuments(tab, stepResult, ors, "Action: Get Full Metadata for next " + ors.objectRefs.size(), false, listingDisplay.data.siteSpec));
                        hcIdTreeItem.addItem(getNextItem);
                    }
                }
            }
        } else {
            new ListingDisplay(tab, data, root,  null).listing();
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
           DataModel dm = ListingDisplay.newDataModel(data.results);
           dm.siteSpec = data.siteSpec;

           ListingDisplay listingDisplay = new ListingDisplay(tab,dm,root, null);
           hcIdListings.put(hcId, listingDisplay);
           return listingDisplay;
       }

    }



}
