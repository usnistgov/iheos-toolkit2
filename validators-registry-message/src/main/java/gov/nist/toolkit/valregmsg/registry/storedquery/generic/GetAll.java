package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.EbRim;
import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.SQCodedTerm;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.*;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 8/13/15.
 */
abstract public class GetAll extends StoredQuery {
    static final Logger logger = Logger.getLogger(GetAll.class);
    /**
     * Method required in subclasses (implementation specific class) to define specific
     * linkage to local database
     * @return matching metadata
     * @throws MetadataException
     * @throws XdsException
     * @throws LoggerException
     */
    abstract protected Metadata runImplementation() throws XdsException;

    /**
     * Basic constructor
     * @param sqs
     * @throws MetadataValidationException
     */
    public GetAll(StoredQuerySupport sqs) throws MetadataValidationException {
        super(sqs);
    }

    /**
     * Implementation of Stored Query specific logic including parsing and validating parameters.
     * @throws XdsInternalException
     * @throws XdsException
     * @throws LoggerException
     * @throws XDSRegistryOutOfResourcesException
     */
    public Metadata runSpecific() throws XdsException, XDSRegistryOutOfResourcesException {
        validateParameters();

        parseParameters();

        Metadata m = runImplementation();

        if (sqs.log_message != null)
            sqs.log_message.addOtherParam("Results structure", m.structure());

        return m;
    }

    public void validateParameters() throws MetadataValidationException {
        //                         param name,                                 required?, multiple?, is string?,   is code?,      support AND/OR                          alternative
        sqs.validate_parm("$patientId",                                         true,      false,     true,         false,           false,                             (String[])null												);
        sqs.validate_parm("$XDSDocumentEntryConfidentialityCode",               false,     true,      true,         true,            true,                              (String[])null												);
        sqs.validate_parm("$XDSDocumentEntryFormatCode",                        false,     true,      true,         true,            false,                              (String[])null												);
        sqs.validate_parm("$XDSFolderStatus",                                   true,      true,      true,         false,           false,                               (String[])null												);
        sqs.validate_parm("$XDSSubmissionSetStatus",                            true,      true,      true,         false,           false,                               (String[])null												);
        sqs.validate_parm("$XDSDocumentEntryStatus",                            true,      true,      true,         false,           false,                               (String[])null												);

        if (sqs.has_validation_errors)
            throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);

    }



    protected String    patient_id;
    protected SQCodedTerm format_codes;
    protected SQCodedTerm conf_codes;
    protected List<String> deStatus;
    protected List<String> folStatus;
    protected List<String> ssStatus;
    protected List<String> deObjectTypes;

    void toBuffer(StringBuffer buf, String name, String arg) {
        if (arg != null && !arg.equals(""))
            buf.append(name).append("=").append(arg).append("\n");
    }

    void toBuffer(StringBuffer buf, String name, List<String> args) {
        if (args != null) {
            buf.append(name).append("=[");
            for (String val : args)
                buf.append(val).append(",");
            buf.append("]\n");
        }
    }

    void toBuffer(StringBuffer buf, String name, SQCodedTerm arg) {
        if (arg != null && !arg.equals(""))
            buf.append(name).append("=").append(arg).append("\n");
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("FindDocuments: [\n");

        toBuffer(buf, "patient_id", patient_id);
        toBuffer(buf, "conf_codes", conf_codes);
        toBuffer(buf, "format_codes", format_codes);
        toBuffer(buf, "DE_status", deStatus);
        toBuffer(buf, "FOL_status", folStatus);
        toBuffer(buf, "SS_status", ssStatus);
        toBuffer(buf, "DE_objectTypes", deObjectTypes);
        buf.append("]\n");

        return buf.toString();
    }

    void parseParameters() throws XdsException {

        patient_id                        = sqs.params.getStringParm("$patientId");
        conf_codes                        = sqs.params.getCodedParm("$XDSDocumentEntryConfidentialityCode");
        format_codes                      = sqs.params.getCodedParm("$XDSDocumentEntryFormatCode");
        deStatus                          = sqs.params.getListParm("$XDSDocumentEntryStatus");
        folStatus                         = sqs.params.getListParm("$XDSFolderStatus");
        ssStatus                          = sqs.params.getListParm("$XDSSubmissionSetStatus");
        deObjectTypes                     = sqs.params.getListParm("$XDSDocumentEntryType");


        String status_ns_prefix = MetadataSupport.status_type_namespace;

        ArrayList<String> new_status = new ArrayList<>();

        for (int i=0; i<deStatus.size(); i++) {
            String stat = deStatus.get(i);
            if ( ! stat.startsWith(status_ns_prefix))
                throw new MetadataValidationException("$XDSDocumentEntryStatus parameter must have namespace prefix " + status_ns_prefix + " found " + stat, EbRim.RegistryObject_attributes);
            new_status.add(stat.replaceFirst(status_ns_prefix, ""));
        }
        deStatus = new_status;

        new_status.clear();
        for (int i=0; i<folStatus.size(); i++) {
            String stat = folStatus.get(i);
            if ( ! stat.startsWith(status_ns_prefix))
                throw new MetadataValidationException("$XDSFolderStatus parameter must have namespace prefix " + status_ns_prefix + " found " + stat, EbRim.RegistryObject_attributes);
            new_status.add(stat.replaceFirst(status_ns_prefix, ""));
        }
        folStatus = new_status;

        new_status.clear();
        for (int i=0; i<ssStatus.size(); i++) {
            String stat = ssStatus.get(i);
            if ( ! stat.startsWith(status_ns_prefix))
                throw new MetadataValidationException("$XDSSubmissionSetStatus parameter must have namespace prefix " + status_ns_prefix + " found " + stat, EbRim.RegistryObject_attributes);
            new_status.add(stat.replaceFirst(status_ns_prefix, ""));
        }
        ssStatus = new_status;

        if (deObjectTypes == null) {
            deObjectTypes = new ArrayList<>();
            deObjectTypes.add(MetadataSupport.XDSDocumentEntry_objectType_uuid);
        }

        if (sqs.log_message != null)
            sqs.log_message.addOtherParam("Some Parameters", toString());
    }
}
