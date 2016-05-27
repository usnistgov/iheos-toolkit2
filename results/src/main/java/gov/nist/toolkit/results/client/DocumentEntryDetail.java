package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sunil Bhaskarla on 5/16/2016.
 */
public class DocumentEntryDetail implements Serializable, IsSerializable {
    /**
     *  The Document External Identifier */
    String uniqueId;
    /**
     *  The EO object ref
    */
    String id;
    /**
     *  Document Entry type */
    String entryType;
    /**
     Created timestamp
    * */
    String timestamp;
    /**
     *  Test Plan Id */
    TestInstance testInstance;
    /**
     *  Registry Site where the OD Document Entry exists */
    SiteSpec regSiteSpec;
    /**
     * If persisting a snapshot, the repository site
     */
    SiteSpec reposSiteSpec;
    /**
     *  Patient Id */
    String patientId;
    /**
     * The supply state index. Applies to on-demand documents only. This index maps to the content bundle section.
     */
    int supplyStateIndex = 0;

    /**
     * Persisted snapshot. There can be only one snapshot for an ODDE at any given time.
     */
    DocumentEntryDetail snapshot;

    /**
     * Content bundle sections
     */
    List<String> contentBundleSections;


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int getSupplyStateIndex() {
        return supplyStateIndex;
    }

    public void setSupplyStateIndex(int supplyStateIndex) {
        this.supplyStateIndex = supplyStateIndex;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public SiteSpec getRegSiteSpec() {
        return regSiteSpec;
    }

    public void setRegSiteSpec(SiteSpec regSiteSpec) {
        this.regSiteSpec = regSiteSpec;
    }


    public DocumentEntryDetail getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DocumentEntryDetail snapshot) {
        this.snapshot = snapshot;
    }

    public List<String> getContentBundleSections() {
        return contentBundleSections;
    }

    public void setContentBundleSections(List<String> contentBundleSections) {
        this.contentBundleSections = contentBundleSections;
    }

    public SiteSpec getReposSiteSpec() {
        return reposSiteSpec;
    }

    public void setReposSiteSpec(SiteSpec reposSiteSpec) {
        this.reposSiteSpec = reposSiteSpec;
    }
}
