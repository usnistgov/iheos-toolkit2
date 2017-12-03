package gov.nist.toolkit.actortransaction.client;

public enum OptionType {
    REQUIRED("", "Required"),
    METADATA_UPDATE("mu", "Metadata Update"),
    MULTI_PATIENT_QUERY("mpq", "Multi Patient Query"),
    ON_DEMAND("od", "On Demand"),
    ISR("isr",
            "Integrated Source Repository"),
    XUA("xua",
            "XUA"),
    CAT_FOLDER("catfolder",
            "CAT Folder"),
    CAT_LIFECYCLE("catlifecycle",
            "CAT Lifecycle"),
    AFFINITY_DOMAIN("ad",
            "Affinity Domain"),
    XDS_ON_FHIR("xdsonfhir","XDS on FHIR");




    private String code;
    private String description;

    OptionType() {
        this("","Required");
    }

    OptionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    static public OptionType find(String s) {
        if (s == null || "".equals(s)) return REQUIRED;
        for (OptionType p : values()) {
            if (s.equals(p.code)) return p;
            try {
                if (p == OptionType.valueOf(s)) return p;
            } catch (IllegalArgumentException e) {
                // continue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }

    public boolean equals(OptionType p) {
       return (p.toString().equals(this.toString()));
    }

    public boolean equals(String s) {
        return (this.toString().equals(s));
    }

}
