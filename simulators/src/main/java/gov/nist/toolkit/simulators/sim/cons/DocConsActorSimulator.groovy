package gov.nist.toolkit.simulators.sim.cons

import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.registrymsg.repository.RetrieveItemRequestModel
import gov.nist.toolkit.registrymsg.repository.RetrieveRequestModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel
import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.testengine.engine.RetrieveB
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.utilities.xml.XmlUtil
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger
/**
 *
 */
@TypeChecked
public class DocConsActorSimulator extends BaseDsActorSimulator  {
    static final Logger logger = Logger.getLogger(DocConsActorSimulator.class);
    List<OMElement> extraSoapHeaderElements = new ArrayList<>();
    boolean tls;

    boolean getTls() {
        return tls
    }

    void setTls(boolean tls) {
        this.tls = tls
    }
/**
     * This would be used if this were a server sim.  It is useless as a client sim
     * @param transactionType transaction code
     * @param mvc MessageValidatorEngine - execution engine for validators and simulators
     * @param validation name of special validation to be run. Allows simulators to be extended
     * to perform test motivated validations
     * @return
     * @throws IOException
     */
    @Override
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        return false;
    }

    @Override
    public void init() {}

    public DocConsActorSimulator() {}

    private String retrieveTemplate = '''
<RetrieveDocumentSetRequest xmlns="urn:ihe:iti:xds-b:2007"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="urn:ihe:iti:xds-b:2007 file:/Users/bill/ihe/Frameworks/ITI-4/XDS.b/schema/IHE/XDS.b_DocumentRepository.xsd">
</RetrieveDocumentSetRequest>
'''

    private String queryTemplate = '''
<query:AdhocQueryRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
                         xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
                         xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0">
    <query:ResponseOption returnComposedObjects="true" returnType="returnTypeValue"/>
    <AdhocQuery>
    </AdhocQuery>
</query:AdhocQueryRequest>
    ''';

    public RetrievedDocumentsModel retrieve(SimulatorConfig config, RetrieveRequestModel request) throws Exception {
        OMElement retrieveRequest = buildRetrieve(request)

        String endpoint =
                config.get(
                        (tls) ? SimulatorProperties.retrieveTlsEndpoint : SimulatorProperties.retrieveEndpoint
                ).asString();


        Soap soap = new Soap();
        for (OMElement ele : extraSoapHeaderElements) {
            soap.addHeader(ele);
        }
        OMElement result =  soap.soapCall(retrieveRequest,
                endpoint,
                true, // mtom
                true, // addressing
                true, // SOAP1.2
                MetadataSupport.Retrieve_action,
                MetadataSupport.Retrieve_response_action);

        RetrieveB retb = new RetrieveB(null);
        Map<String, RetrievedDocumentModel> docMap = retb.parse_rep_response(result).getMap();

        return new RetrievedDocumentsModel(docMap)
    }

    public OMElement query(SimulatorConfig config, String queryId, QueryParameters parameters, boolean returnLeafClass, boolean isTls) {
        assert config
        assert queryId
        assert parameters

        OMElement query = buildQuery(queryId, parameters, returnLeafClass)

        String endpoint =
                config.get(
                        (isTls) ? SimulatorProperties.storedQueryTlsEndpoint : SimulatorProperties.storedQueryEndpoint
                ).asString();


        Soap soap = new Soap();
        for (OMElement ele : extraSoapHeaderElements) {
            soap.addHeader(ele);
        }
        return soap.soapCall(query,
                endpoint,
                false, // mtom
                true, // addressing
                true, // SOAP1.2
                MetadataSupport.SQ_action,
                MetadataSupport.SQ_response_action);
    }

    OMElement buildRetrieve(RetrieveRequestModel model) {
        OMElement request = Util.parse_xml(retrieveTemplate)
        OMElement x
        model.models.each { RetrieveItemRequestModel item ->
            OMElement r = XmlUtil.createElement('DocumentRequest', MetadataSupport.xdsB)
            if (item.homeId) {
                x = XmlUtil.createElement('HomeCommunityId', MetadataSupport.xdsB)
                x.setText(item.homeId)
                r.addChild(x)
            }
            x = XmlUtil.createElement('RepositoryUniqueId', MetadataSupport.xdsB)
            x.setText(item.repositoryId)
            r.addChild(x)
            x = XmlUtil.createElement('DocumentUniqueId', MetadataSupport.xdsB)
            x.setText(item.documentId)
            r.addChild(x)
            request.addChild(r)
        }
        return request
    }

    OMElement buildQuery(String queryId, QueryParameters parameters, boolean returnLeafClass) {

        String text = queryTemplate.replaceFirst('returnTypeValue', (returnLeafClass) ? 'LeafClass' : 'ObjectRef')
        OMElement query = Util.parse_xml(text)
        OMElement adhocQuery = XmlUtil.childrenWithLocalName(query, 'AdhocQuery').get(0)
        assert adhocQuery

        adhocQuery.addAttribute('id', queryId, null)

        parameters.parameterNames.each { String paramName ->
            OMElement slot = XmlUtil.createElement('Slot', MetadataSupport.ebRIMns3)
            adhocQuery.addChild(slot)

            slot.addAttribute("name", paramName, null)

            OMElement valueList = XmlUtil.createElement('ValueList', MetadataSupport.ebRIMns3)
            slot.addChild(valueList)

            parameters.getValues(paramName).each { value ->
                OMElement valueEle = XmlUtil.createElement('Value', MetadataSupport.ebRIMns3)
                valueList.addChild(valueEle)
                StringBuffer formattedValue = new StringBuffer();
                boolean multiValue = MultiValueParameters.supportsMultipleValues(paramName);
                if (multiValue) formattedValue.append('(')
                formattedValue.append("'")
                formattedValue.append(value)
                formattedValue.append("'")
                if (multiValue) formattedValue.append(')')

                valueEle.setText(formattedValue.toString())
            }
        }

        return query;
    }

    public void addSoapHeaderElement(OMElement ele) { extraSoapHeaderElements.add(ele); }

}
