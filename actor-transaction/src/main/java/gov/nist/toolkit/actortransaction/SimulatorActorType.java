package gov.nist.toolkit.actortransaction;


// This file must be kept up to date with ActorType.java
/**
 * Simulator actor types that can be specified when creating a Simulator. To pass as a parameter use:
 *
 * SimulatorActorType.REGISTRY
 *
 * for example.
 */
public enum SimulatorActorType {
    /**
     * Document Registry.
     */
    REGISTRY("reg"),
    /**
     * Document Repository.
     */
    REPOSITORY("rep"),
    /**
     * Integrated Repository/Registry.  Not a formal IHE actor definition but a commonly used integration for testing.
     */
    REPOSITORY_REGISTRY("rr"),
    /**
     * Document Recipient.
     */
    DOCUMENT_RECIPIENT("rec"),
    /**
     * Document Source.
     */
    DOCUMENT_SOURCE("xdrsrc"),
    /**
     * Document Consumer.
     */
    DOCUMENT_CONSUMER("cons"),
    /**
     * On-Demand Document Source
     */
    ONDEMAND_DOCUMENT_SOURCE("odds"),
    /**
     * Integrated Source/Repository
     */
    ISR("isr"),
    /**
     * Responding Gateway
     */
    RESPONDING_GATEWAY("rg"),
    /**
     * Initiating Gateway
     */
    INITIATING_GATEWAY("ig");

    String name;  // name that matches ActorType.java

    SimulatorActorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
