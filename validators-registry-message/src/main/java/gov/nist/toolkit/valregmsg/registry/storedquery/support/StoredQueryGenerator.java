package gov.nist.toolkit.valregmsg.registry.storedquery.support;


import gov.nist.toolkit.registrysupport.MetadataSupport;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Created by onh2 on 10/27/2015.
 */
public class StoredQueryGenerator {
    public static OMElement generateQueryFile(SqParams query) {
        OMFactory factory = OMAbstractFactory.getOMFactory();

        OMNamespace queryNS = MetadataSupport.ebQns3;
        OMElement root = factory.createOMElement("AdhocQueryRequest",queryNS);

        OMElement responseOption=factory.createOMElement("ResponseOption",queryNS);
        responseOption.addAttribute("returnComposedObjects","true",null);
        responseOption.addAttribute("returnType","leafClass",null);

        OMNamespace tagNS = MetadataSupport.ebRIMns3;
        OMElement adhocQuery = factory.createOMElement("AdhocQuery",tagNS);
        adhocQuery.addAttribute("id", query.getQueryId(), null);

        for (String key:query.params.keySet()){
            OMElement slot = factory.createOMElement("Slot",tagNS);
            slot.addAttribute("name",key,null);
            OMElement valuelist=factory.createOMElement("ValueList",tagNS);
            OMElement value=factory.createOMElement("Value",tagNS);
            value.addChild(factory.createOMText(query.getStringParm(key)));
            valuelist.addChild(value);
            slot.addChild(valuelist);
            adhocQuery.addChild(slot);
        }

        root.addChild(responseOption);
        root.addChild(adhocQuery);

        return root;

    }

}
