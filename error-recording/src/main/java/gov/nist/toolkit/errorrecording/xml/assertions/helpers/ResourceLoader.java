package gov.nist.toolkit.errorrecording.xml.assertions.helpers;

import java.io.InputStream;

/**
 * Created by diane on 10/3/2016.
 */
public class ResourceLoader {
    private static ResourceLoader instance = null;

    protected ResourceLoader(){}

    public static ResourceLoader getResourceLoader(){
        if (instance == null) {
            instance = new ResourceLoader();
        }
       return instance;
    }


    public InputStream getToolkitAssertions() {
        return getClass().getResourceAsStream("/assertions/Toolkit_assertions.csv");
    }

    public InputStream getFileAsInputStream(String fileLocation){
        return getClass().getResourceAsStream(fileLocation);
    }


}
