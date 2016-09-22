/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

import java.util.ArrayList;

/**
 * General Detail Reporter. This class allows us to insert report information in the chain.
 */
public class DetailReporter extends Detail {
   

   private ArrayList<String> messages = new ArrayList<>();
   
   
   /**
    * @param reportMessages messages to add
    */
   public void initializeDetail(String... reportMessages) {
	   for (String msg : reportMessages) {
		   messages.add(msg);
	   }
	   desc = "Reporter";

   }

   /* (non-Javadoc)
    * @see edu.wustl.mir.erl.ihe.xdsi.validation.Detail#runDetail()
    */
   @Override
   public void runDetail() {
	   successDetails.addAll(messages);
   }

}
