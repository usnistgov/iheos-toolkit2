package gov.nist.toolkit.newValidationSystem

/**
 *
 */
interface AssertionGroup {
    List<String> getMacroAssertions();
    List<Class> getInherits();
    List<Class> getExcepts();
    boolean execute();
}