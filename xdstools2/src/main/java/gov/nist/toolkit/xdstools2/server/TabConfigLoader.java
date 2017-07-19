package gov.nist.toolkit.xdstools2.server;

import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdstools2.client.tabs.conformanceTest.TabConfig;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by skb1 on 7/18/2017.
 */
public class TabConfigLoader {
    private static final Logger logger = Logger.getLogger(TabConfigLoader.class);
//    private static final ConcurrentHashMap<String,ToolTabConfig> confToolTabMap = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    private TabConfigLoader() {}

    static public void init(ConcurrentHashMap<String,TabConfig> toolTabMap, File toolTabFile) throws Exception {
        if (!initialized) {
            synchronized (TabConfigLoader.class) {
                initialized = true;

                OMElement tabsEl = Util.parse_xml(toolTabFile);

                if (tabsEl !=null) {
                    String parentLabel = tabsEl.getAttributeValue(new QName("label"));
                    TabConfig parent = new TabConfig(parentLabel);
                   toolTabMap.put(parentLabel,xform(tabsEl, parent));
                }
            }
        }
    }

    static private TabConfig xform(OMElement parentEl, TabConfig parent) {
        if (parent==null)  return null;

        if (parentEl!=null && "tabs".equals(parentEl.getLocalName())) {
            Iterator tabsIt = parentEl.getChildElements();
            if (tabsIt!=null) {
                while (tabsIt.hasNext()) {
                   OMElement tabEl = (OMElement)tabsIt.next();
                   String label = tabEl.getAttributeValue(new QName("label"));
                   String type = tabEl.getAttributeValue(new QName("type"));
                   String tcCode = tabEl.getAttributeValue(new QName("tcCode"));
                   String externalStartStr = tabEl.getAttributeValue(new QName("externalStart"));
                   Boolean externalStart = null;


                   if (externalStartStr!=null) {
                       externalStart = Boolean.valueOf(externalStartStr);
                   }

                   TabConfig tabConfig = new TabConfig(label,type,tcCode);
                    if (externalStart!=null) {
                        tabConfig.setExternalStart(externalStart);
                    }

                   parent.getChildTabConfigs().add(tabConfig);

                   Iterator tabIt = tabEl.getChildElements();
                   if (tabIt!=null && tabIt.hasNext()) {
                       OMElement subGroupEl = (OMElement)tabIt.next();
                       String subGroupLn = subGroupEl.getLocalName();
                       if ("tabs".equals(subGroupLn)) {
                           String subGroupLabel = subGroupEl.getAttributeValue(new QName("label"));
                           TabConfig subGroupTab = new TabConfig(subGroupLabel);
                           tabConfig.getChildTabConfigs().add(subGroupTab);
                            xform(subGroupEl,subGroupTab);
                       }
                   }


                }
            }
        }
        return parent;
    }



}
