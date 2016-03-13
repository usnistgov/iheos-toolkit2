/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * DICOM Image set bean, encapsulating request and responce data
 */
public class DcmImageSet {

   private static Logger log;
   
   private List<Study> studies = new ArrayList<Study>();
   private List<String> transferSyntaxUIDs = new ArrayList<>();
   
   public DcmImageSet() {
      
   }
   
   public class Study {
      
      private String studyUID;
      private List<Series> serieses;
      
      public Study(String studyUID) {
         this.studyUID = studyUID;
         serieses = new ArrayList<>();
      }

      /**
       * @return the {@link #studyUID} value.
       */
      public String getStudyUID() {
         return studyUID;
      }

      /**
       * @param studyUID the {@link #studyUID} to set
       */
      public void setStudyUID(String studyUID) {
         this.studyUID = studyUID;
      }

      /**
       * @return the {@link #serieses} value.
       */
      public List <Series> getSerieses() {
         return serieses;
      }

      /**
       * @param serieses the {@link #serieses} to set
       */
      public void setSerieses(List <Series> serieses) {
         this.serieses = serieses;
      }
      
      
   }
   
   public class Series {
      
      private String seriesUID;
      private List<Document> documents;
      
      public Series(String seriesUID) {
         this.seriesUID = seriesUID;
         documents = new ArrayList<>();
      }

      /**
       * @return the {@link #seriesUID} value.
       */
      public String getSeriesUID() {
         return seriesUID;
      }

      /**
       * @param seriesUID the {@link #seriesUID} to set
       */
      public void setSeriesUID(String seriesUID) {
         this.seriesUID = seriesUID;
      }

      /**
       * @return the {@link #documents} value.
       */
      public List <Document> getDocuments() {
         return documents;
      }

      /**
       * @param documents the {@link #documents} to set
       */
      public void setDocuments(List <Document> documents) {
         this.documents = documents;
      }
      
      
   }
   
   public class Document {
   
   private String repositoryUID;
   private String documentUID;
   private String instanceUID;
   private String homeCommunityId;
   
   public Document(String repositoryUID, String documentUID, String instanceUID, 
      String homeCommunityId) {
      this.repositoryUID = repositoryUID;
      this.documentUID = documentUID;
      this.instanceUID = instanceUID;
      this.homeCommunityId = homeCommunityId;
   }

   /**
    * @return the {@link #repositoryUID} value.
    */
   public String getRepositoryUID() {
      return repositoryUID;
   }

   /**
    * @param repositoryUID the {@link #repositoryUID} to set
    */
   public void setRepositoryUID(String repositoryUID) {
      this.repositoryUID = repositoryUID;
   }

   /**
    * @return the {@link #documentUID} value.
    */
   public String getDocumentUID() {
      return documentUID;
   }

   /**
    * @param documentUID the {@link #documentUID} to set
    */
   public void setDocumentUID(String documentUID) {
      this.documentUID = documentUID;
   }

   /**
    * @return the {@link #instanceUID} value.
    */
   public String getInstanceUID() {
      return instanceUID;
   }

   /**
    * @param instanceUID the {@link #instanceUID} to set
    */
   public void setInstanceUID(String instanceUID) {
      this.instanceUID = instanceUID;
   }

   /**
    * @return the {@link #homeCommunityId} value.
    */
   public String getHomeCommunityId() {
      return homeCommunityId;
   }

   /**
    * @param homeCommunityId the {@link #homeCommunityId} to set
    */
   public void setHomeCommunityId(String homeCommunityId) {
      this.homeCommunityId = homeCommunityId;
   }
}
}