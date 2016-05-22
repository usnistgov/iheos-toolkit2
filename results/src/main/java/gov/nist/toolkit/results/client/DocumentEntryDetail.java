package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

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
     *  Registry Site where the Document Entry exists */
    SiteSpec registrySite;
    /**
     *  Patient Id */
    String patientId;
    /**
     * The supply state index. Applies to on-demand documents only.
     */
    int supplyStateIndex = 0;


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

    public SiteSpec getRegistrySite() {
        return registrySite;
    }

    public void setRegistrySite(SiteSpec registrySite) {
        this.registrySite = registrySite;
    }
}
