package gov.nist.toolkit.newValidationSystem

/**
 *
 */
interface Assertion {
    List<String> ptos;
    Class<?> getAssertionId();
    List<String> getMacroAssertions();
    String getText();
    boolean isOptional();
    boolean execute();
}