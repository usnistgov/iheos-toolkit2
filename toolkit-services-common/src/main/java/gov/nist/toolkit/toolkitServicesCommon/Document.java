package gov.nist.toolkit.toolkitServicesCommon;

/**
 * Created by bill on 2/15/16.
 */
public interface Document {
    String getMimeType();

    byte[] getContents();
}
