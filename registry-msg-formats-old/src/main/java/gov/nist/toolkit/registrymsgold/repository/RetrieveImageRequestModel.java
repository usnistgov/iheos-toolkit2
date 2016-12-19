/**
 * 
 */
package gov.nist.toolkit.registrymsgold.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
   
   // XCAI_TODO complete, need to test
   

} // EO RetrieveImageRequestModel class
