/**
 * 
 */
package gov.nist.toolkit.registrymsg.repository;

import java.util.*;

/**
 * Encapsulates {@code <iherad:RetrieveImagingDocumentSetRequest>} 
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RetrieveImageRequestModel {
   
   List<RetrieveImageStudyRequestModel> studyRequests = new ArrayList<>();
   
   List<String> transferSyntaxUIDs = new ArrayList<>();

   /**
    * @return the {@link #studyRequests} value.
    */
   public List <RetrieveImageStudyRequestModel> getStudyRequests() {
      return studyRequests;
   }

   /**
    * @param studyRequests the {@link #studyRequests} to set
    */
   public void setStudyRequests(List <RetrieveImageStudyRequestModel> studyRequests) {
      this.studyRequests = studyRequests;
   }
   
   public void addStudyRequest(RetrieveImageStudyRequestModel sModel) {
      studyRequests.add(sModel);
   }

   /**
    * @return the {@link #transferSyntaxUIDs} value.
    */
   public List <String> getTransferSyntaxUIDs() {
      return transferSyntaxUIDs;
   }

   /**
    * @param transferSyntaxUIDs the {@link #transferSyntaxUIDs} to set
    */
   public void setTransferSyntaxUIDs(List <String> transferSyntaxUIDs) {
      this.transferSyntaxUIDs = transferSyntaxUIDs;
   }
   
   public void addTransferSyntaxUID(String uid) {
      transferSyntaxUIDs.add(uid);
   }
   
   public Set<String> getHomeCommunityIds() {
      Set<String> ids = new HashSet<>();
      for (RetrieveImageStudyRequestModel studyModel : studyRequests) {
         for (RetrieveImageSeriesRequestModel seriesModel : studyModel.getSeriesRequests()) {
            for (RetrieveItemRequestModel documentModel : seriesModel.getDocumentRequests()) {
               ids.add(documentModel.getHomeId());
            }
         }
      }
      return ids;
   }
   
   /**
    * @return Set of all IDS Repository Unique Ids represented in this request
    */
   public Set<String> getIDSRepositoryUniqueIds() {
      Set<String> ids = new HashSet<>();
      for (RetrieveImageStudyRequestModel studyModel : studyRequests) {
         for (RetrieveImageSeriesRequestModel seriesModel : studyModel.getSeriesRequests()) {
            for (RetrieveItemRequestModel documentModel : seriesModel.getDocumentRequests()) {
               ids.add(documentModel.repositoryId);
            }
         }
      }
      return ids;
   }
   
   /**
    * Generates a <b>Copy</b> of this model containing only documents for the
    * passed home community id
    * @param homeCommunityId id to build model for
    * @return model copy
    */
   public RetrieveImageRequestModel getModelForCommunity(String homeCommunityId) {
      RetrieveImageRequestModel homeModel = new RetrieveImageRequestModel();
      for (RetrieveImageStudyRequestModel studyModel : studyRequests) {
         RetrieveImageStudyRequestModel homeStudyModel = new RetrieveImageStudyRequestModel();
         boolean homeStudyModelAdded = false;
         homeStudyModel.setStudyInstanceUID(studyModel.getStudyInstanceUID());
         for (RetrieveImageSeriesRequestModel seriesModel : studyModel.getSeriesRequests()) {
            RetrieveImageSeriesRequestModel homeSeriesModel = new RetrieveImageSeriesRequestModel();
            boolean homeSeriesModelAdded = false;
            homeSeriesModel.setSeriesInstanceUID(seriesModel.getSeriesInstanceUID());
            for (RetrieveItemRequestModel documentModel : seriesModel.getDocumentRequests()) {
               if (documentModel.getHomeId().equals(homeCommunityId)) {
                  if (homeStudyModelAdded == false) {
                     homeModel.addStudyRequest(homeStudyModel);
                     homeStudyModelAdded = true;
                  }
                  if (homeSeriesModelAdded == false) {
                     homeStudyModel.addSeriesRequest(homeSeriesModel);
                     homeSeriesModelAdded = true;
                  }
                  RetrieveItemRequestModel homeDocumentModel = new RetrieveItemRequestModel();
                  homeDocumentModel.setDocumentId(documentModel.getDocumentId());
                  homeDocumentModel.setHomeId(homeCommunityId);
                  homeDocumentModel.setRepositoryId(documentModel.getRepositoryId());
                  homeSeriesModel.addDocumentRequest(homeDocumentModel);
               }
            }
         }
      }
      for (String xferSyntax : transferSyntaxUIDs)
         homeModel.addTransferSyntaxUID(xferSyntax);
      return homeModel;
   }

   /**
    * Generates a <b>Copy</b> of this model containing only documents for the
    * passed repository unique id
    * @param id unique id to build model for
    * @return model copy
    */
   public RetrieveImageRequestModel getModelForRepository(String id) {
      RetrieveImageRequestModel newModel = new RetrieveImageRequestModel();
      for (RetrieveImageStudyRequestModel studyModel : studyRequests) {
         RetrieveImageStudyRequestModel newStudyModel = new RetrieveImageStudyRequestModel();
         boolean newStudyModelAdded = false;
         newStudyModel.setStudyInstanceUID(studyModel.getStudyInstanceUID());
         for (RetrieveImageSeriesRequestModel seriesModel : studyModel.getSeriesRequests()) {
            RetrieveImageSeriesRequestModel newSeriesModel = new RetrieveImageSeriesRequestModel();
            boolean newSeriesModelAdded = false;
            newSeriesModel.setSeriesInstanceUID(seriesModel.getSeriesInstanceUID());
            for (RetrieveItemRequestModel documentModel : seriesModel.getDocumentRequests()) {
               if (documentModel.getRepositoryId().equals(id)) {
                  if (newStudyModelAdded == false) {
                     newModel.addStudyRequest(newStudyModel);
                     newStudyModelAdded = true;
                  }
                  if (newSeriesModelAdded == false) {
                     newStudyModel.addSeriesRequest(newSeriesModel);
                     newSeriesModelAdded = true;
                  }
                  RetrieveItemRequestModel newDocumentModel = new RetrieveItemRequestModel();
                  newDocumentModel.setDocumentId(documentModel.getDocumentId());
                  newDocumentModel.setHomeId(documentModel.getHomeId());
                  newDocumentModel.setRepositoryId(id);
                  newSeriesModel.addDocumentRequest(newDocumentModel);
               }
            }
         }
      }
      for (String xferSyntax : transferSyntaxUIDs)
         newModel.addTransferSyntaxUID(xferSyntax);
      return newModel;
   }

   /**
    * Returns a list of string containing the composite uids (study, series, instance)
    * separated by colons. If the flags are set, the additional codes are included.
    * Empty strings are shown as zero length strings. Null values are replaced
    * with the string "null".
    * @param includeHcid include the home community ids
    * @param includeRuid include the repository unique ids
    * @return list of colon delimited strings
    */
   public List<String> getCompositeUids(boolean includeHcid, boolean includeRuid) {
      List<String> compositeUids = new ArrayList<>();
      for (RetrieveImageStudyRequestModel studyrm : studyRequests) {
         for (RetrieveImageSeriesRequestModel seriesrm : studyrm.getSeriesRequests()) {
            for (RetrieveItemRequestModel itemrm : seriesrm.getDocumentRequests()) {
               String s = n(studyrm.getStudyInstanceUID()) + ":" +
                          n(seriesrm.getSeriesInstanceUID()) + ":" +
                          n(itemrm.getDocumentId());
               if (includeHcid) s += ":" + n(itemrm.getHomeId());
               if (includeRuid) s += ":" + n(itemrm.getRepositoryId());
               compositeUids.add(s);
            }
         }
      }
      return compositeUids;
   }
   private String n(String input) {
      return (input == null) ? "null" : input;
   }

   /**
    * Generates a model from a valid set of String lists. For testing
    * @param compositeUids a list of composite UID string, of the format
    *        study UID, series UID, instance UID, home community ID, repository
    *        unique ID, separated by commas. home community ID and repository ID
    *        may be empty strings or "null". study, series, and instance UIDs
    *        must be present.
    * @param xferSyntaxUids a list or transfer syntax UIDs.
    * @return
    */
   public static RetrieveImageRequestModel buildModel(List<String> compositeUids, List<String> xferSyntaxUids) {
      RetrieveImageRequestModel model = new RetrieveImageRequestModel();
      Collections.sort(compositeUids);
      model.setTransferSyntaxUIDs(xferSyntaxUids);
      RetrieveImageStudyRequestModel studyModel = null;
      RetrieveImageSeriesRequestModel seriesModel = null;

      for (String compositeUid : compositeUids) {
         String[] tokens = compositeUid.split(",");
         String studyUid = tokens[0];
         String seriesUid = tokens[1];
         String instanceUid = tokens[2];
         String hcid = tokens[3].equalsIgnoreCase("null") ? null : tokens[3];
         String repUid = tokens[4].equalsIgnoreCase("null") ? null : tokens[4];

         if (!studyUid.equals(studyModel.getStudyInstanceUID())) {
            if (studyModel == null || !studyModel.getStudyInstanceUID().equals(studyUid)) {
               studyModel = new RetrieveImageStudyRequestModel();
               studyModel.setStudyInstanceUID(studyUid);
               model.addStudyRequest(studyModel);
               seriesModel = null;
            }
            if (seriesModel == null || !seriesModel.getSeriesInstanceUID().equals(seriesUid)) {
               seriesModel = new RetrieveImageSeriesRequestModel();
               seriesModel.setSeriesInstanceUID(seriesUid);
               studyModel.addSeriesRequest(seriesModel);
            }
            RetrieveItemRequestModel document = new RetrieveItemRequestModel();
            document.setDocumentId(instanceUid);
            document.setHomeId(hcid);
            document.setRepositoryId(repUid);
         }
      }
      return model;
   }

} // EO RetrieveImageRequestModel class
