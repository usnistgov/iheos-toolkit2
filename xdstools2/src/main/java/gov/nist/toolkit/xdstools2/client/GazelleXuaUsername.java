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
    secondpurposeofuseDOTREQUESTOID("secondpurposeofuse.REQUESTOID");

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
