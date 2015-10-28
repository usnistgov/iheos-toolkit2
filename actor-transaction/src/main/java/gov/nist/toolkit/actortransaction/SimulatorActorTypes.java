package gov.nist.toolkit.actortransaction;


// This file must be kept up to date with ActorType.java
/**
 * Simulator actor types that can be specified when creating a Simulator.
 */
public enum SimulatorActorTypes {
    /**
     * Document Registry.  As a String parameter use "reg" or "Document Registry".
     */
    REGISTRY,
    /**
     * Document Repository.  As a String parameter use "rep" or "Document Repository".
     */
    REPOSITORY,
    /**
     * Integrated Repository/Registry.  Not a formal IHE actor definition but a commonly used integration for testing. As a String parameter use "rr" or "Document Repository/Registry".
     */
    REPOSITORY_REGISTRY,
    /**
     * Document Recipient.  As a String parameter use "rec" or "Document Recipient".
     */
    DOCUMENT_RECIPIENT
}
