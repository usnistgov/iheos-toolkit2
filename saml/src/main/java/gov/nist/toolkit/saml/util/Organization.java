package gov.nist.toolkit.saml.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
 	
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml2.metadata.provider.ChainingMetadataProvider;
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.ParserPool;
/**
 * @author Srinivasarao.Eadara
 *
 */
public class Organization implements Serializable
 	{
 	    private static final long serialVersionUID = 37094603463569L;
 	    private static final int HTTP_METADATA_REQUEST_TIMEOUT = 5000;
	 	   
 	    private Log _logger;
	   
	    private String _sID;
 	    private byte[] _baSourceID;
 	    private String _sFriendlyName;
	 	    private String _sMetadataFile;
		    private String _sMetadataURL;
	 	    private int _iMetadataTimeout;
	 	   
		    /**
	 	     * Creates an organization model.
	 	     *
		     * @param sID The id of the organization
	 	     * @param baSourceID the SourceID of the organization
	 	     * @param sFriendlyName the organization friendly name
	 	     * @param sMetadataFile The location of the metadata file or NULL if none
	 	     * @param sMetadataURL The url of the metadata or NULL if none
	 	     * @param iMetadataTimeout The timeout to be used in connecting the the url
	 	     * metadata or -1 when default must be used
	 	     * @throws OAException if invalid data supplied
	 	     */
	 	    public Organization(String sID, byte[] baSourceID, String sFriendlyName,
	 	        String sMetadataFile, String sMetadataURL,
	 	        int iMetadataTimeout) throws Exception{
	 	        _logger = LogFactory.getLog(Organization.class);
	 	       
	 	        _sID = sID;
	 	        _baSourceID = baSourceID;
	 	        _sFriendlyName = sFriendlyName;
	 	        _sMetadataFile = sMetadataFile;
	 	        if (_sMetadataFile != null){
	 	            File fMetadata = new File(_sMetadataFile);
	 	            if (!fMetadata.exists()){
	 	                StringBuffer sbError = new StringBuffer("Supplied metadata file for organization '" );
	 	                sbError.append(_sID);
	 	                sbError.append("' doesn't exist: ");
	 	                sbError.append(_sMetadataFile);
	 	                _logger.error(sbError.toString());
	 	                throw new Exception("Supplied metadata file for organization doesn't exit");
	 	            }
	 	        }
	 	       
	 	        _sMetadataURL = sMetadataURL;
	 	        if (_sMetadataURL != null){
	 	            try{
	 	                new URL(_sMetadataURL);
	 	            }catch (MalformedURLException e){
	 	                StringBuffer sbError = new StringBuffer("Invalid metadata URL supplied for organization '" );
	 	                sbError.append(_sID);
	 	                sbError.append("': ");
	 	                sbError.append(_sMetadataURL);
	 	                _logger.error(sbError.toString(), e);
	 	                throw new Exception("Invalid metadata URL supplied for organization");
	 	            }
	 	        }
	 	       
	 	        _iMetadataTimeout = iMetadataTimeout;
	 	        if (_iMetadataTimeout < 0){
	 	            _iMetadataTimeout = HTTP_METADATA_REQUEST_TIMEOUT;
	 	           
	 	            StringBuffer sbDebug = new StringBuffer("Supplied HTTP metadata timeout for organization '" );
	 	            sbDebug.append(_sID);
	 	            sbDebug.append("' is smaller then zero, using default: ");
	 	            sbDebug.append(_iMetadataTimeout);
	 	            _logger.debug(sbDebug.toString());
	 	        }
	 	    }
	 	       
	 	    /**
	 	     * Returns the organization id.
	 	     * @return The organization id
	 	     */
	 	    public String getID(){
	 	        return _sID;
	 	    }
	 	   
	 	    /**
	 	     * Returns the SourceID of the organization.
	 	     * @return the source id
	 	     */
	 	    public byte[] getSourceID(){
	 	        return _baSourceID;
	 	    }
	 	   
	 	    /**
	 	     * Returns the organization friendly name of the organization.
	 	     * @return the friendly name
	 	     */
	 	    public String getFriendlyName(){
	 	        return _sFriendlyName;
	 	    }
	 	
	 	    /**
	 	     * The {@link java.lang.Object#hashCode()} of the ID.
	 	     * @see java.lang.Object#hashCode()
	 	     */
	 	    public int hashCode(){
	 	        return _sID.hashCode();
	 	    }
	 	   
	 	    /**
	 	     * Returns <code>ID.equals(other.ID)</code>.
	 	     * @see java.lang.Object#equals(java.lang.Object)
		     */
	 	    public boolean equals(Object other){
	 	        if(!(other instanceof Organization))
	 	            return false;       
	 	        return _sID.equals(((Organization)other)._sID);
	 	    }
	 	   
	 	    /**
	 	     * @see java.lang.Object#toString()
	 	     */
	 	    public String toString(){
	 	        StringBuffer sbInfo = new StringBuffer(_sFriendlyName);
	 	        sbInfo.append(" (");
	 	        sbInfo.append(_sID);
	 	        sbInfo.append(")");   
	 	        return sbInfo.toString();
	 	    }
	 	 
	 	    /**
	 	     * Returns a chaining metadata provider with the metadata of the organization.
	 	     * <br>
	 	     * The provider contains the file and url metadata of the organization if
	 	     * available and creates the metadata provider everytime this method is
	 	     * called.
	 	     *
	 	     * @return The MetadataProvider (ChainingMetadataProvider) with the metadata
	 	     * for this organization or NULL when no metadata is available.
	 	     * @throws OAException If metadata is invalid or could not be accessed
	 	     */
	 	    public MetadataProvider getMetadataProvider() throws Exception{
	 	        ChainingMetadataProvider chainingMetadataProvider = null;
	 	       
	 	        try{
	 	            BasicParserPool parserPool = new BasicParserPool();
	 	            parserPool.setNamespaceAware(true);
	 	           
	 	            List<MetadataProvider> listMetadataProviders =
	 	                new Vector<MetadataProvider>();
	 	           
	 	            MetadataProvider mpFile = createFileMetadataProvider(_sMetadataFile,
	 	                parserPool);
	 	            if (mpFile != null)
	 	                listMetadataProviders.add(mpFile);
	 	           
	 	            MetadataProvider mpHttp = createHTTPMetadataProvider(_sMetadataURL,
	 	                _iMetadataTimeout, parserPool);
	 	            if (mpHttp != null)
	 	                listMetadataProviders.add(mpHttp);
	 	           
	 	            if (!listMetadataProviders.isEmpty()){
	 	                chainingMetadataProvider = new ChainingMetadataProvider();
	 	                chainingMetadataProvider.setProviders(listMetadataProviders);
	 	            }
	 	        }
	 	        catch (Exception e){
	 	            _logger.fatal("Internal error while creating metadata providers", e);
	 	            throw new Exception("Internal error while creating metadata providers");
	 	        }
	 	        return chainingMetadataProvider;
	 	    }
	 	
	 	    private HTTPMetadataProvider createHTTPMetadataProvider(String sMetadataURL,
	 	        int iMetadataTimeout, ParserPool parserPool)
	 	        throws Exception{
	 	        HTTPMetadataProvider urlProvider = null;
	 	       
	 	        if (sMetadataURL == null){
		            return null;
	 	        }
	 	         
	 	        URL urlTarget = null;
	 	        try{
	 	            urlTarget = new URL(sMetadataURL);
	 	        }catch (MalformedURLException e){
	 	            _logger.error(
	 	                "Invalid 'url' item in 'http' section found in configuration: "
	 	                + sMetadataURL, e);
	 	            throw new Exception( "Invalid 'url' item in 'http' section found in configuration: "
		 	                + sMetadataURL);
	 	        }
	 	       
	 	        try{
	 	            urlTarget.openConnection().connect();
	 	        }catch (IOException e){
	 	            _logger.warn(
	 	                "Could not connect to metadata url: " + sMetadataURL, e);
	 	        }
	 	       
	 	        try{
	 	            urlProvider = new HTTPMetadataProvider(sMetadataURL,
	 	                iMetadataTimeout);
	 	            urlProvider.setParserPool(parserPool);
	 	            urlProvider.initialize();
	 	        }catch (MetadataProviderException e){
	 	            StringBuffer sbDebug = new StringBuffer();
	 	            sbDebug.append("No metadata available at configured URL '");
	 	            sbDebug.append(sMetadataURL);
	 	            sbDebug.append("': Disabling http metadata for this requestor");
	 	            _logger.warn(sbDebug.toString(), e);
	 	           
	 	            urlProvider = null;
	 	        }
	 	
	 	        return urlProvider;
	 	    }
	 	   
	 	    private FilesystemMetadataProvider createFileMetadataProvider(
	 	        String sMetadataFile, ParserPool parserPool)
	 	        throws Exception{
	 	        FilesystemMetadataProvider fileProvider = null;
	 	        if (sMetadataFile == null){
	 	            return null;
	 	        }
	 	
	 	        File fMetadata = new File(sMetadataFile);
	 	        if (!fMetadata.exists()){
	 	            _logger.error("Configured metadata 'file' doesn't exist: "
	 	                + sMetadataFile);
	 	            throw new Exception("Configured metadata 'file' doesn't exist: "
		 	                + sMetadataFile);
	 	        }
	 	       
	 	        try{
	 	            fileProvider = new FilesystemMetadataProvider(fMetadata);
	 	        }catch (MetadataProviderException e){
	 	            _logger.error("No metadata available in configured file: "
	 	                + sMetadataFile, e);
	 	            throw new Exception("No metadata available in configured file: "
		 	                + sMetadataFile, e);
	 	        }
	 	       
	 	        fileProvider.setParserPool(parserPool);
	 	        try{
	 	            fileProvider.initialize();
	 	        }catch (MetadataProviderException e){
	 	            _logger.error("No metadata available in configured file: "
	 	                + sMetadataFile, e);
	 	            throw new Exception("No metadata available in configured file: "
		 	                + sMetadataFile, e);
	 	        }
	 	        return fileProvider;
	 	    }
	 	}
