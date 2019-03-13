package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;


/**
 *
 */
public class RegistryDbInspector extends Place {
    private String paramString;
    private MetadataCollection metadataCollection;
    private SiteSpec siteSpec;

    public RegistryDbInspector(MetadataCollection mc, SiteSpec siteSpec) {
        super();
        GWT.log("Build Registry Browser Place");
        this.metadataCollection = mc;
        this.siteSpec = siteSpec;
    }

    public RegistryDbInspector(String paramString) {
        this.paramString = paramString;
    }

    public static class Tokenizer implements PlaceTokenizer<RegistryDbInspector> {
        @Override
        public RegistryDbInspector getPlace(String paramString) {
            return new RegistryDbInspector(paramString);
        }

        @Override
        public String getToken(RegistryDbInspector place) {
            return place.paramString;
        }
    }
    // This is necessary!
    // The GWT place controller uses this to see if the newly requested place already exists.
    // If it exists it reuses it. Toolkit doesn't work this way. We use Places to represent
    // a tool in a tab and we want to be able to have multiple copies of a tool/tab running
    // at the same time. The easy way to accomplish this is to break the idea
    // of Place equality.  (Vote for equal rights for Places!)
    @Override
    public boolean equals(Object o) {
        return false;
    }

    public String getName() {
        return paramString;
    }

    public MetadataCollection getMetadataCollection() {
        return metadataCollection;
    }

    public SiteSpec getSiteSpec() {
        return siteSpec;
    }
}
