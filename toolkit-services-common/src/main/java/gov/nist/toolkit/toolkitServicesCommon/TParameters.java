/**
 * 
 */
package gov.nist.toolkit.toolkitServicesCommon;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;

/**
 * BeanParam POJO for transaction RS.
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project
 * <a href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class TParameters {
   
   @MatrixParam("site")
   @DefaultValue("IDS")
   private String siteName;
   
   @MatrixParam("session")
   @DefaultValue("API")
   private String testSession;
   
   @MatrixParam("env")
   @DefaultValue("default")
   private String environmentName;

}
