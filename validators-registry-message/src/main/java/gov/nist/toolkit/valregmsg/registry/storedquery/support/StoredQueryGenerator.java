package gov.nist.toolkit.valregmsg.registry.storedquery.support;


import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import java.util.Iterator;
import java.util.List;

/**
 * Created by onh2 on 10/27/2015.
 */
public class StoredQueryGenerator {
    static OMFactory factory = OMAbstractFactory.getOMFactory();
    static OMNamespace tagNS = MetadataSupport.ebRIMns3;


    public static OMElement generateQueryFile(String returnType, SqParams query) {

        OMNamespace queryNS = MetadataSupport.ebQns3;
        OMElement root = factory.createOMElement("AdhocQueryRequest",queryNS);

        OMElement responseOption=factory.createOMElement("ResponseOption",queryNS);
        responseOption.addAttribute("returnComposedObjects","true",null);
//        responseOption.addAttribute("returnType","leafClass",null);
        responseOption.addAttribute("returnType",returnType,null);

        OMElement adhocQuery = factory.createOMElement("AdhocQuery",tagNS);
        adhocQuery.addAttribute("id", query.getQueryId(), null);

        for (String key:query.params.keySet()){
            Object codeValue=query.getParm(key);
            if (codeValue instanceof SQCodeOr){
                addParamSlot(adhocQuery,key,codeValue);
            }else if (codeValue instanceof SQCodeAnd){
                SQCodeAnd sqCodeAnd=(SQCodeAnd) codeValue;
                Iterator<SQCodeOr> iterator=sqCodeAnd.codeOrs.iterator();
                while(iterator.hasNext()){
                    SQCodeOr c =iterator.next();
                    addParamSlot(adhocQuery,key,c);
                }
            }else {
                addParamSlot(adhocQuery,key,query.getParm(key));
            }
        }

        root.addChild(responseOption);
        root.addChild(adhocQuery);

        return root;

    }

    static OMElement addParamSlot(OMElement parent,String slotName, Object object){
        OMElement slot = factory.createOMElement("Slot",tagNS);
        slot.addAttribute("name",slotName,null);
        OMElement valuelist=factory.createOMElement("ValueList",tagNS);
        if (object instanceof SQCodeOr){
            SQCodeOr sqCodeOr=(SQCodeOr) object;
            for (SQCodeOr.CodeLet c:sqCodeOr.getCodeValues()) {
                OMElement value=factory.createOMElement("Value", tagNS);
                value.addChild(factory.createOMText("('"+c.toString()+"')"));
                valuelist.addChild(value);
            }
        }else {
            OMElement value = factory.createOMElement("Value", tagNS);
            String stringValue = new String();
            if (object instanceof List) {
                stringValue = "(";
                if (((List) object).get(0) instanceof String) {
                    boolean first = true;
                    for (String s : ((List<String>) object)) {
                        if (!first) {
                            stringValue += ",";
                        }
                        stringValue += "'" + s + "'";
                        first = false;
                    }
                } else if (((List) object).get(0) instanceof Integer) {
                    boolean first = true;
                    // build string value
                    for (Integer i : ((List<Integer>) object)) {
                        if (!first) {
                            stringValue += ",";
                        }
                        stringValue += i;
                        first = false;
                    }
                }
                stringValue += ")";
            } else {
                if (object instanceof String) {
//                    if (((String) model).startsWith("$") && ((String) model).endsWith("$")) {
//                        stringValue = (String) model;
//                    } else {
                        stringValue = "'" + object + "'";
//                    }
                } else if (object instanceof Integer) {
                    stringValue = object.toString();
                }
            }
            value.addChild(factory.createOMText(stringValue));
            valuelist.addChild(value);
        }
        slot.addChild(valuelist);
        parent.addChild(slot);
        return parent;
    }
}
