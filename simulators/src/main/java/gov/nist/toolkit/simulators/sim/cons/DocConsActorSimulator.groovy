package gov.nist.toolkit.simulators.sim.cons
import gov.nist.toolkit.actorfactory.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.actortransaction.client.TransactionType
import gov.nist.toolkit.registrysupport.MetadataSupport
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.soap.axis2.Soap
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

    String template = '''
<query:AdhocQueryRequest xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
                         xmlns="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
                         xmlns:rs="urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0">
    <query:ResponseOption returnComposedObjects="true" returnType="returnTypeValue"/>
    <AdhocQuery>
    </AdhocQuery>
</query:AdhocQueryRequest>
    ''';


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

    OMElement buildQuery(String queryId, QueryParameters parameters, boolean returnLeafClass) {

        String text = template.replaceFirst('returnTypeValue', (returnLeafClass) ? 'LeafClass' : 'ObjectRef')
        OMElement query = Util.parse_xml(text)
        OMElement adhocQuery = XmlUtil.childrenWithLocalName(query, 'AdhocQuery').get(0)
        assert adhocQuery

        adhocQuery.addAttribute('id', queryId, null)

        parameters.parameterNames.each { paramName ->
            OMElement slot = XmlUtil.createElement('Slot', MetadataSupport.ebRIMns3)
            adhocQuery.addChild(slot)

            slot.addAttribute("name", paramName, null)

            OMElement valueList = XmlUtil.createElement('ValueList', MetadataSupport.ebRIMns3)
            slot.addChild(valueList)

            parameters.getValues(paramName).each { value ->
                OMElement valueEle = XmlUtil.createElement('Value', MetadataSupport.ebRIMns3)
                valueList.addChild(valueEle)
                valueEle.setText("'" + value + "'")
            }
        }

        return query;
    }

    public void addSoapHeaderElement(OMElement ele) { extraSoapHeaderElements.add(ele); }

}
