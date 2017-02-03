/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.nio.file.Path;

import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.lang3.StringUtils;

/**
 * Encapsulates information about an various types of "actors", including DICOM
 * Application Entities and IDC, IDS, IG and RG actors.
 */
public class AEBean {

   private String aeTitle;
   private String host;
   private Integer port;
   private Integer wadoPort;
   private String wadoPath;
   private String wadoURL;

   private String repositoryUniqueId;
   private String homeCommunityId;

   private String xdsSiteName;
   private String xdsFullId;
   private String xdsEnvironment;
   private String xdsUser;
   private String xdsActorType;
   private String wsdlURL;

   /**
    * @return the {@link #aeTitle} value.
    */
   public String getAeTitle() {
      return aeTitle;
   }

   /**
    * @param aeTitle the {@link #aeTitle} to set
    */
   public void setAeTitle(String aeTitle) {
      this.aeTitle = aeTitle;
   }

   /**
    * @return the {@link #host} value.
    */
   public String getHost() {
      return host;
   }

   /**
    * @param host the {@link #host} to set
    */
   public void setHost(String host) {
      this.host = host;
   }

   /**
    * @return the {@link #port} value.
    */
   public Integer getPort() {
      return port;
   }

   /**
    * @param port the {@link #port} to set
    */
   public void setPort(Integer port) {
      this.port = port;
   }

   /**
    * @return the {@link #wadoPort} value.
    */
   public Integer getWadoPort() {
      return wadoPort;
   }

   /**
    * @param wadoPort the {@link #wadoPort} to set
    */
   public void setWadoPort(Integer wadoPort) {
      this.wadoPort = wadoPort;
   }

   /**
    * @return the {@link #wadoPath} value.
    */
   public String getWadoPath() {
      return wadoPath;
   }

   /**
    * @param wadoPath the {@link #wadoPath} to set
    */
   public void setWadoPath(String wadoPath) {
      this.wadoPath = wadoPath;
   }

   /**
    * @return the {@link #wadoURL} value.
    */
   public String getWadoURL() {
      return wadoURL;
   }

   /**
    * @param wadoURL the {@link #wadoURL} to set
    */
   public void setWadoURL(String wadoURL) {
      this.wadoURL = wadoURL;
   }

   /**
    * @return the {@link #repositoryUniqueId} value.
    */
   public String getRepositoryUniqueId() {
      return repositoryUniqueId;
   }

   /**
    * @param repositoryUniqueId the {@link #repositoryUniqueId} to set
    */
   public void setRepositoryUniqueId(String repositoryUniqueId) {
      this.repositoryUniqueId = repositoryUniqueId;
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

   /**
    * @return the {@link #xdsSiteName} value.
    */
   public String getXdsSiteName() {
      return xdsSiteName;
   }

   /**
    * @param xdsSiteName the {@link #xdsSiteName} to set
    */
   public void setXdsSiteName(String xdsSiteName) {
      this.xdsSiteName = xdsSiteName;
   }
   
   

   /**
    * @return the {@link #xdsFullId} value.
    */
   public String getXdsFullId() {
      if (StringUtils.isNotBlank(xdsFullId)) return xdsFullId;
      return xdsUser + "__" + xdsSiteName;
   }

   /**
    * @param xdsFullId the {@link #xdsFullId} to set
    */
   public void setXdsFullId(String xdsFullId) {
      this.xdsFullId = xdsFullId;
   }

   /**
    * @return the {@link #xdsEnvironment} value.
    */
   public String getXdsEnvironment() {
      return xdsEnvironment;
   }

   /**
    * @param xdsEnvironment the {@link #xdsEnvironment} to set
    */
   public void setXdsEnvironment(String xdsEnvironment) {
      this.xdsEnvironment = xdsEnvironment;
   }

   /**
    * @return the {@link #xdsUser} value.
    */
   public String getXdsUser() {
      return xdsUser;
   }

   /**
    * @param xdsUser the {@link #xdsUser} to set
    */
   public void setXdsUser(String xdsUser) {
      this.xdsUser = xdsUser;
   }

   /**
    * @return the {@link #xdsActorType} value.
    */
   public String getXdsActorType() {
      return xdsActorType;
   }

   /**
    * @param xdsActorType the {@link #xdsActorType} to set
    */
   public void setXdsActorType(String xdsActorType) {
      this.xdsActorType = xdsActorType;
   }

   /**
    * @return the {@link #wsdlURL} value.
    */
   public String getWsdlURL() {
      return wsdlURL;
   }

   /**
    * @param wsdlURL the {@link #wsdlURL} to set
    */
   public void setWsdlURL(String wsdlURL) {
      this.wsdlURL = wsdlURL;
   }

   /**
    * create bean using configuration .ini file.
    * 
    * @param configFile name:
    * <ul>
    * <li/>Simple name is assumed to be in {@link Utility#getRunDirectoryPath()}
    * .
    * <li/>may also be absolute path or path relative to runDirectory.
    * <li/>.ini file extension added if not already present.
    * </ul>
    * @return AEBean for this .ini
    */
   public static AEBean loadFromConfigurationFile(String configFile) {
      try {
         if (!configFile.endsWith(".ini")) configFile += ".ini";
         Path configPfn = Utility.getRunDirectoryPath().resolve(configFile);
         HierarchicalINIConfiguration ini = new HierarchicalINIConfiguration(configPfn.toString());
         AEBean bean = new AEBean();
         bean.setAeTitle(ini.getString("AETitle", null));
         bean.setHost(ini.getString("host", "localhost"));
         bean.setPort(ini.getInt("port", 0));
         bean.setWadoPort(ini.getInteger("wadoPort", null));
         bean.setWadoPath(ini.getString("wadoPath", null));
         bean.setWadoURL(ini.getString("wadoURL", null));
         bean.setRepositoryUniqueId(ini.getString("repositoryUniqueId", null));
         bean.setHomeCommunityId(ini.getString("homeCommunityId", null));
         bean.setXdsSiteName(ini.getString("xdsSiteName", null));
         bean.setXdsFullId(ini.getString("xdsFullId", null));
         bean.setXdsEnvironment(ini.getString("xdsEnvironment", null));
         bean.setXdsUser(ini.getString("xdsUser", null));
         bean.setXdsActorType(ini.getString("xdsActorType", null));
         bean.setWsdlURL(ini.getString("wsdlURL", null));
         return bean;
      } catch (Exception e) {
         Utility.getLog().error(Utility.getEM(e));
         System.exit(1);
      }
      return null;
   }
}
