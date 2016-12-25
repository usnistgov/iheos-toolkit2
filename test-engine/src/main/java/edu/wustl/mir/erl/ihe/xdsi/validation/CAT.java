/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.validation;

/**
 * Result categories. Used to group validation results for reporting.
 */
public enum CAT { 
   /**
    * Expected result was found.
    */
   SUCCESS, 
   /**
    * A result was found which is not being tested, but which may be in error
    * or "not what you want". May also relate to something expected, but not
    * found.
    */
   WARNING, 
   /**
    * Expected result was missing or incorrect.
    */
   ERROR, 
   /**
    * Message which was generated but is not (or cannot be) determined to be in
    * SUCCESS, WARNING, or ERROR categories.
    */
   UNCAT, 
   /**
    * A message result or lack of result which was detected but will be ignored.
    * This is for programmers only; the tester will not see these.
    */
   SILENT,
   /**
    * Informational messages give detailed logging information but are not errors or warnings
    */
   INFO;
   
   /**
    * Get CAT which matches name, ignoring case, or null
    * @param name of CAT
    * @return CAT for name
    */
   public static CAT forThis(String name) {
      CAT[] cats = CAT.values();
      for (CAT cat : cats) {
         if (cat.name().equalsIgnoreCase(name)) return cat;
      }
      return null;
   }
};
