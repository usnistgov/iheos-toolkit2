package gov.nist.toolkit.configDatatypes.server;


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
     * Responding Gateway - with ODDS and registry
     */
    ON_DEMAND_RESPONDING_GATEWAY("odrg"),
    /**
     * Responding Imaging Gateway
     */
    RESPONDING_IMAGING_GATEWAY("rig"),
    /**
     * Image Document Source
     */
    IMAGE_DOCUMENT_SOURCE("ids"),
    /**
     * Image Document Source
     */
    IMAGE_DOCUMENT_CONSUMER("idc"),
    /**
     * Initiating Gateway
     */
    INITIATING_GATEWAY("ig"),
    /**
     * Initiating Gateway
     */
    INITIATING_IMAGING_GATEWAY("iig"),

    FHIR_SERVER("fhir"),

    SIM_PROXY("simproxy"),

    XDS_on_FHIR_Recipient("xdsonfhir"),

    MHD_DOC_RECIPIENT("mhddocrec");

    String name;  // name that matches ActorType.java

    SimulatorActorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
