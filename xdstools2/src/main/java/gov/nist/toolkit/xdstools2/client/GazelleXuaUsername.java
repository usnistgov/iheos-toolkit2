package gov.nist.toolkit.xdstools2.client;

/**
 * Created by skb1 on 10/21/2016.
 */
public enum GazelleXuaUsername {
    valid,
    notyetvalid,
    expired,
    unsigned,
    invalidsignature,
    missingkeyinfo,
    missingkeyvalue,
    missingrsakeyvalue,
    missingrsakeymodulus,
    missingrsakeyexponent,
    invalidversion,
    missingversion,
    invalidid,
    missingid,
    missingsubjectconfirmation,
    missingsubjectconfirmationmethod,
    missingsubject,
    missingsubjectnameid,
    missingissuer,
    missingissuerformat,
    invalidissueremailformat,
    invalidissuerx509format,
    invalidissuerwindowsdomainformat,
    missingissueinstant,
    invalidissueinstant,
    invalidrsapublickeymodulus,
    invalidrsapublickeyexponent,
    invalidsubjectnameidformat,
    invalidx509certificate,
    lateissueinstant,
    missingsubjectconfdata,
    missingsubjectconfirmationkeyinfo,
    missingsubjectconfrsapublickeyexponent,
    invalidsubjectconfrsapublickeymodulus,
    invalidsubjectconfrsapublickeyexponent,
    unknownaudience,
    invalidauthncontext,
    secondauthncontext,
    secondrole,
    secondpurposeofuse,
    withauthzconsent,

    secondpurposeofuseDOTCOVERAGE("secondpurposeofuse.COVERAGE"),
    secondpurposeofuseDOTOPERATIONS("secondpurposeofuse.OPERATIONS"),
    secondpurposeofuseDOTPAYMENT("secondpurposeofuse.PAYMENT"),
    secondpurposeofuseDOTPUBLICHEALTH("secondpurposeofuse.PUBLICHEALTH"),
    secondpurposeofuseDOTREQUEST("secondpurposeofuse.REQUEST"),
    secondpurposeofuseDOTTREATMENT("secondpurposeofuse.TREATMENT"),
    secondpurposeofuseDOTLEGACYTREATMENT("secondpurposeofuse.LEGACYTREATMENT"),
    secondpurposeofuseDOTREASSURANCE("secondpurposeofuse.REASSURANCE"),
    secondpurposeofuseDOTTREATMENTOID("secondpurposeofuse.TREATMENTOID"),
    secondpurposeofuseDOTCOVERAGEOID("secondpurposeofuse.COVERAGEOID"),
    secondpurposeofuseDOTOPERATIONSOID("secondpurposeofuse.OPERATIONSOID"),
    secondpurposeofuseDOTPAYMENTOID("secondpurposeofuse.PAYMENTOID"),
    secondpurposeofuseDOTPUBLICHEALTHOID("secondpurposeofuse.PUBLICHEALTHOID"),
    secondpurposeofuseDOTREQUESTOID("secondpurposeofuse.REQUESTOID"),

    // Add entries for RCE 1.1 codes
    secondpurposeofuseDOTTTRTMNT("secondpurposeofuse.T-TRTMNT"),
    secondpurposeofuseDOTTPYMNT("secondpurposeofuse.T-PYMNT"),
    secondpurposeofuseDOTTHCO("secondpurposeofuse.T-HCO"),
    secondpurposeofuseDOTTPH("secondpurposeofuse.T-PH"),
    secondpurposeofuseDOTTIAS("secondpurposeofuse.T-IAS"),
    secondpurposeofuseDOTTGOVDTRM("secondpurposeofuse.T-GOVDTRM"),

    // Add entries for RCE 2.0 codes
    // The only difference between the 1.1 and 2.0 codes is for T-IAS.
    // In the 1.1 specification, two attributes for csp and validated_attributes are added.
    // In the 2.0 specification, an id_token is add (and csp and validated_attributes are omitted).
    // The other 5 codes (T-TREAT, ...) are included for completeness.

    secondpurposeofuseDOTTTRTMNT20("secondpurposeofuse.T-TRTMNT-20"),
    secondpurposeofuseDOTTPYMNT20("secondpurposeofuse.T-PYMNT-20"),
    secondpurposeofuseDOTTHCO20("secondpurposeofuse.T-HCO-20"),
    secondpurposeofuseDOTTPH20("secondpurposeofuse.T-PH-20"),
    secondpurposeofuseDOTTIAS20("secondpurposeofuse.T-IAS-20"),
    secondpurposeofuseDOTTGOVDTRM20("secondpurposeofuse.T-GOVDTRM-20"),

    // Add entries for ACP 1.0 testing.
    // These track test patients because we need to include a patient ID
    // that is specific to that test patient.

    secondpurposeofuseDOTErnser("secondpurposeofuse.ACP-Ernser"),
    secondpurposeofuseDOTSimonis("secondpurposeofuse.ACP-Simonis"),
    secondpurposeofuseDOTOrn("secondpurposeofuse.ACP-Orn"),
    secondpurposeofuseDOTWest("secondpurposeofuse.ACP-West"),
    secondpurposeofuseDOTQuigley("secondpurposeofuse.ACP-Quigley"),
    secondpurposeofuseDOTPredovic("secondpurposeofuse.ACP-Predovic");

    private String username;

    GazelleXuaUsername() {
        this.username = this.name();
    }

    GazelleXuaUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return this.username;
    }
}
