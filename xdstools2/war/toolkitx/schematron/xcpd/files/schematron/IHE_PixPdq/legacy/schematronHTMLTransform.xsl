<?xml version="1.0" encoding="utf-8" standalone="yes"?>
<axsl:stylesheet xmlns:axsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" xmlns:cda="urn:hl7-org:v3" xmlns:sdtc="urn:hl7-org:sdtc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0" cda:dummy-for-xmlns="" sdtc:dummy-for-xmlns="" xsi:dummy-for-xmlns="">
   <axsl:output method="html"/>
   <axsl:template match="*|@*" mode="schematron-get-full-path">
      <axsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <axsl:text>/</axsl:text>
      <axsl:if test="count(. | ../@*) = count(../@*)">@</axsl:if>
      <axsl:value-of select="name()"/>
      <axsl:text>[</axsl:text>
      <axsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
      <axsl:text>]</axsl:text>
   </axsl:template>
   <axsl:template match="/">
      <html>
         <style>
         a:link    { color: black}
         a:visited { color: gray}
         a:active  { color: #FF0088}
         h3        { background-color:black; color:white;
                     font-family:Arial Black; font-size:12pt; }
         h3.linked { background-color:black; color:white;
                     font-family:Arial Black; font-size:12pt; }
      </style>
         <h2 title="Schematron contact-information is at the end of                   this page">
            <font color="#FF0080">Schematron</font> Report
      </h2>
         <h1 title=" ">HITSP_C32</h1>
         <div class="errors">
            <ul>
               <h3/>
               <axsl:apply-templates select="/" mode="M7"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M8"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M9"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M10"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M11"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M12"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M13"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M14"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M15"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M16"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M17"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M18"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M19"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M20"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M21"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M22"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M23"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M24"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M25"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M26"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M27"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M28"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M29"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M30"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M31"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M32"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M33"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M34"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M35"/>
               <h3/>
               <axsl:apply-templates select="/" mode="M36"/>
            </ul>
         </div>
         <hr color="#FF0080"/>
         <p>
            <font size="2">Schematron Report by David Carlisle.
      <a href="http://www.ascc.net/xml/resource/schematron/schematron.html" title="Link to the home page of the Schematron,                  a tree-pattern schema language">
                  <font color="#FF0080">The Schematron</font>
               </a> by
      <a href="mailto:ricko@gate.sinica.edu.tw" title="Email to Rick Jelliffe (pronounced RIK JELIF)">Rick Jelliffe</a>,
      <a href="http://www.sinica.edu.tw" title="Link to home page of Academia Sinica">Academia Sinica Computing Centre</a>.
      </font>
         </p>
      </html>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="4000" mode="M7">
      <axsl:choose>
         <axsl:when test="self::cda:ClinicalDocument"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall be an HL7 CDA Clinical Document.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.10.20.1&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 document shall carry the template identifier for the ASTM/HL7 CCD document (2.16.840.1.113883.10.20.1) from which it is derived.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="count(./cda:recordTarget/cda:patientRole/cda:patient)=1"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain Patient Information for exactly one patient. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.4&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain at least one Healthcare Provider module (2.16.840.1.113883.3.88.11.32.4). See HITSP/C32 Table 4.2.3.1-1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.10.20.1.9&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a CCD Payers section (2.16.840.1.113883.10.20.1.9) that contains a summary of all Insurance Provider information. If no payment sources are provided, the reason shall be provided as free text in the narrative block (e.g. Not Insured, Payer Unknown, Meidcare Pending, et cetera) of the CCD Payors section. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.5.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.10.20.1.2&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a CCD Alerts section (2.16.840.1.113883.10.20.1.2) that contains a summary of all allergy and drug sensitivity information. At a minimum this section shall contain a summary listing of currently active and any relevant historical allergies and adverse reactions. The lack of any such information shall be asserted in the narrative block of the CCD Alerts section. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.6.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.10.20.1.11&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a CCD Problems section (2.16.840.1.113883.10.20.1.11) that contains a summary of all relevant clinical problems. At a minimum this summary may be limited to a brief list of serious major medical conditions that should always be disclosed even in many ancillary service department settings. The lack of any such information shall be asserted in the narrative block of the CCD Problems section. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.7.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.10.20.1.8&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a CCD Medications section (2.16.840.1.113883.10.20.1.8) that contains a summary of all current medications and pertinent medical history. At a minimum the currently active medications should be listed. If no medications are known then that fact shall be reported in the narrative block of the CCD Medications section. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.8.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument" priority="3999" mode="M7">
      <axsl:choose>
         <axsl:when test="cda:effectiveTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Document Timestamp data element See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Patient Information data element. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person ID data element for the Patient Role. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:addr"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person Address data element for the Patient Role. Multiple addresses are possible to identify temporary addresses, vacation home addresses, work addresses, etc. Exactly one address for a patient should have a use attribute with a value set to HP (home permanent). Others may be set to HV (vacation) or WP (work place), etc. See Table 4.2.3.1.1-2 and Section 4.2.3.1.1.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person Phone/Email/URL data element for the Patient Role. Multiple telecom instances are used to record multiple telephone numbers, email addresses, etc. The Use code on telecom is used to indicate the following: HP (home phone), HV (vacation home phone), WP (work phone), MC (mobile phone), etc. Telephone numbers shall be represented in international form, e.g. +1-ddd-ddd-dddd;ext=dddd for U.S. numbers. Hyphens and parentheses are ignored. Email addresses shall use the mailto: URL scheme from RFC-2368. See Table 4.2.3.1.1-2 and Section 4.2.3.1.1.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:patient"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Personal Information data element for the Patient Role. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:patient/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person Name data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:patient/cda:administrativeGenderCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person Gender data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:recordTarget/cda:patientRole/cda:patient/cda:birthTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Person Date of Birth data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient" priority="3998" mode="M7">
      <axsl:choose>
         <axsl:when test="cda:name/cda:family"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: HITSP/C32 shall contain a Patient Family name part. See Section 4.2.3.1.1.1 rule C32-[2].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="count(cda:name[@use=&#34;L&#34;])=0 or count(cda:name[@use=&#34;L&#34;])=1"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: At most one C32 Patient Name shall have a use element set to legal (L). See Section 4.2.3.1.1.1 rule C32-[6].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:addr" priority="3997" mode="M7">
      <axsl:choose>
         <axsl:when test="cda:country | cda:state | cda:city | cda:streetAddressLine | cda:postalcode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: Each HITSP/C32 Patient address part shall be identified using the streetAddressLine, city, state, postalCode and country tags. See Section 4.2.3.1.1.2 rule C32-[12].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="count(cda:streetAddressLine) &lt; 5"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: Each HITSP/C32 Patient address shall contain no more than 4 streetAddressLine elements. See Section 4.2.3.1.1.2 rule C32-[14].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:country) or cda:country[string-length()=2]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: Each HITSP/C32 Patient Country address part shall be recorded using ISO-3166-1 2-character codes. See Section 4.2.3.1.1.2 rule C32-[22].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="3996" mode="M7">
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:administrativeGenderCode" priority="3995" mode="M7">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.5.1&#34;) or @displayName or cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Patient Gender shall be coded using the HL7 AdministrativeGenderCode code system (2.16.840.1.113883.5.1). See Section 4.2.3.1.1.4 rule C32-[32].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:maritalStatusCode" priority="3994" mode="M7">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.3.88.6.1633.5.2.2&#34;) or @displayName or cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Marital Status shall be coded using the ASTM E1633 code system (2.16.840.1.113883.3.88.6.1633.5.2.2). See Section 4.2.3.1.1.4 rule C32-[33].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:raceCode" priority="3993" mode="M7">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.6.238&#34;) or @displayName or cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Race shall be coded using the CDC Race and Ethnicity code system (2.16.840.1.113883.6.238). See Section 4.2.3.1.1.4 rule C32-[34].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:ethnicGroupCode" priority="3992" mode="M7">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.6.238&#34;) or @displayName or cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Ethnicity Group shall be coded using the CDC Race and Ethnicity code system (2.16.840.1.113883.6.238). See Section 4.2.3.1.1.4 rule C32-[36].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:religiousAffiliationcode" priority="3991" mode="M7">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.5.1076&#34;) or @displayName or cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Religius Affiliation shall be coded using the HL7 Religious Affiliation code system (2.16.840.1.113883.5.1076). See Section 4.2.3.1.1.4 rule C32-[36].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[self::cda:author[1]]" priority="3990" mode="M7">
      <axsl:choose>
         <axsl:when test="cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Section or Entry First Author shall contain a C32 Author Time element. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedAuthor//cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Section or Entry first Author shall contain a C32 Author Name element. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[self::cda:externalDocument][parent::cda:reference]" priority="3989" mode="M7">
      <axsl:choose>
         <axsl:when test="cda:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Section or Entry External Reference shall contain a C32 Reference document ID element. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="*[self::cda:informant]" priority="3988" mode="M7">
      <axsl:choose>
         <axsl:when test="//cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Section or Entry Informant shall contain a C32 Information source Name element. The Name may be under assignedEntity/assignedPerson or it may be under assignedEntity/representedOrganization, or both. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M7"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M7"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="4000" mode="M8">
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.5&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should contain at least one CCD Policy Activity entry (2.16.840.1.113883.3.88.11.32.5) represented as a CCD Policy Activity entry (2.16.840.1.113883.10.20.1.26) under a CCD Coverage Activity entry (2.16.840.1.113883.10.20.1.20) in a CCD Payors section (2.16.840.1.113883.10.20.1.9). The lack of any such information shall be asserted in the narrative block of the Payors section. See HITSP/C32 Table 4.2.3.1-1 and Table 4.2.1.3.1.5-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.6&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should contain at least one HITSP/C32 Allergy and Drug Sensitivity module (2.16.840.1.113883.3.88.11.32.6) represented as a CCD Problem Act event (2.16.840.1.113883.10.20.1.27) in a CCD Alerts section (2.16.840.1.113883.10.20.1.2). The lack of any such information shall be asserted in the narrative block of the CCD Alerts section. See HITSP/C32 Table 4.2.3.1-1 and Table 4.2.3.1.6-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.7&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should contain at least one HITSP/C32 Conditions Problem Entry (2.16.840.1.113883.3.88.11.32.7) represented as a CCD Problem Act (2.16.840.1.113883.10.20.1.27) in a CCD Problems section (2.16.840.1.113883.10.20.1.11). The lack of any such information should be asserted in the narrative block of the CCD Problems section. See HITSP/C32 Table 4.2.3.1-1 and Table 4.2.3.1.7-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test=".//cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.8&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should contain at least one HITSP/C32 Medications Administration Information element (2.16.840.1.113883.3.88.11.32.8) represented as a CCD Medication Activity (2.16.840.1.113883.10.20.1.24) entry under a CCD Medications section (2.16.840.1.113883.10.20.1.8). The lack of any such information should be asserted in the narrative block of the CCD Medications section. See HITSP/C32 Table 4.2.3.1-1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="./cda:recordTarget/cda:patientRole/cda:patient/cda:languageCommunication/cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.2&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should include C32 Language Spoken information (2.16.840.1.113883.3.88.11.32.2) when known for the patient. Multiple language spoken modules are permitted. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="./cda:participant[@typeCode = &#34;IND&#34;]/cda:templateId[@root = &#34;2.16.840.1.113883.3.88.11.32.3&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should include C32 Support information (2.16.840.1.113883.3.88.11.32.3) for family, relatives, caregivers, and contacts for healthcare decisions related to the patient. See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.3<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:participant[@typeCode=&#34;IND&#34;]" priority="3999" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.3&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a participation element with the typeCode attribute set to "IND", then that participation element should include the HITSP/C32 template ID for Support (2.16.840.1.113883.3.88.11.32.3). See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.3<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:guardian" priority="3998" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.3&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a Guardian element for a patient, then that guardian element should include the HITSP/C32 template ID for Support (2.16.840.1.113883.3.88.11.32.3). See HITSP/C32 Table 4.2.3.1-1 and Section 4.2.3.1.3<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.26&#34;]]                  [ancestor::cda:act/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.20&#34;]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.9&#34;]" priority="3997" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Policy Activity act (2.16.840.1.113883.10.20.1.26) under a CCD Coverage Activity act (2.16.840.1.113883.10.20.1.20) under a CCD Payors section (2.16.840.1.113883.10.20.1.9), then that CCD Policy Activity should include the HITSP/C32 template ID for Insurance Payment Provider (2.16.840.1.113883.3.88.11.32.5). See HITSP/C32 Section 4.2.3.1.5 and Section 4.2.3.1.5.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.27&#34;]]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.2&#34;]" priority="3996" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Problem act (2.16.840.1.113883.10.20.1.27) under a CCD Alerts section (2.16.840.1.113883.10.20.1.2), then that CCD Problem act should include the HITSP/C32 template ID for Allergy and Drug Sensitivities Module (2.16.840.1.113883.3.88.11.32.6). See HITSP/C32 Section 4.2.3.1.6 and Section 4.2.3.1.6.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.27&#34;]]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.11&#34;]" priority="3995" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Problem act (2.16.840.1.113883.10.20.1.27) under a CCD Problems section (2.16.840.1.113883.10.20.1.11), then that CCD Problem act should include the HITSP/C32 template ID for Conditions Module (2.16.840.1.113883.3.88.11.32.7). See HITSP/C32 Section 4.2.3.1.7 and Section 4.2.3.1.7.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.8&#34;]" priority="3994" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Medication Activity act (2.16.840.1.113883.10.20.1.24) under a CCD Medications section (2.16.840.1.113883.10.20.1.8), then that CCD Medication Activity act (i.e. substanceAdministration) should include the HITSP/C32 template ID for Medications Administration Information (2.16.840.1.113883.3.88.11.32.8). See HITSP/C32 Section 4.2.3.1.8 Table 4.2.3.1.8-2 and Section 4.2.3.1.8.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:observation]                  [parent::cda:entryRelationship/@typeCode=&#34;SUBJ&#34;]                  [parent::*[parent::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]]]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.8&#34;]" priority="3993" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a cda:observation under an entryRelationship of type (SUBJ) under a CCD Medication Activity act (2.16.840.1.113883.10.20.1.24) under a CCD Medications section (2.16.840.1.113883.10.20.1.8), then that cda:observation should include the HITSP/C32 template ID for Type of Medication (2.16.840.1.113883.3.88.11.32.10). See HITSP/C32 Section 4.2.3.1.8 Table 4.2.3.1.8-2 and Section 4.2.3.1.8.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:supply[@moodCode=&#34;RQO&#34;]]                  [parent::cda:entryRelationship/@typeCode=&#34;REFR&#34;]                  [parent::*[parent::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]]]                  [ancestor::cda:section/cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.8&#34;]" priority="3992" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a cda:supply in request (RQO) mood under an entryRelationship of type (REFR) under a CCD Medication Activity act (2.16.840.1.113883.10.20.1.24) under a CCD Medications section (2.16.840.1.113883.10.20.1.8), then that cda:supply should include the HITSP/C32 template ID for Order Information (2.16.840.1.113883.3.88.11.32.11). See HITSP/C32 Section 4.2.3.1.8 Table 4.2.3.1.8-2 and Section 4.2.3.1.8.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@code=&#34;2.16.840.1.113883.10.20.1.40&#34;]" priority="3991" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.12&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Comment entry (2.16.840.1.113883.10.20.1.40), then that entry should include the HITSP/C32 template ID for the Comments Module (2.16.840.1.113883.3.88.11.32.12). See HITSP/C32 Section 4.2.3.1.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@code=&#34;2.16.840.1.113883.10.20.1.17&#34;]" priority="3990" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.13&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 document contains a CCD Advance Directives observation (2.16.840.1.113883.10.20.1.17), then that observation should include the HITSP/C32 template ID for Advance Directives (2.16.840.1.113883.3.88.11.32.13). See HITSP/C32 Section 4.2.3.1.12<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient" priority="3989" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:maritalStatusCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 should contain a Marital Status data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name" priority="3988" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:given"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 Patient Name should contain one or more patient Given name parts. The first name should be in the first given instance. The middle name or initial, if any, should be in the second given instance. See Section 4.2.3.1.1.1 rules C32-[2], C32-[3], C32-[4].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:given or cda:prefix or cda:suffix or cda:delimiter"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: HITSP/C32 Patient Name should contain other name parts. Name parts within a name shall be ordered in proper display order. See Section 4.2.3.1.1.1 rules C32-[2], C32-[5].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="../cda:name[@use=&#34;L&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: The legal name parts of the patient should be identified with the use attribute set to L. See Section 4.2.3.1.1.1 rule C32-[6].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:family[@qualifier=&#34;BR&#34;] or cda:family[@qualifier=&#34;AD&#34;]or cda:given[@qualifier=&#34;AD&#34;]or cda:given[@qualifier=&#34;AD&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: Family or given Patient Name parts that are birth or adoption parts should be qualified as such by setting a qualifier attribute to BR (birth name) or AD (adoption name). See Section 4.2.3.1.1.1 rules C32-[9].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:given[@qualifier=&#34;CL&#34;] or cda:family[@qualifier=&#34;CL&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: Patient Name parts that are given or family name parts should be qualified as preferred names by setting a qualifier attribute to CL (patient preferred name). See Section 4.2.3.1.1.1 rules C32-[10].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:suffix | cda:prefix) or cda:suffix[@qualifier=&#34;AC&#34;] or cda:prefix[@qualifier=&#34;AC&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: Patient Name parts that are prefix or suffix parts should be qualified as academic title names by setting a qualifier attribute to AC (academic title). See Section 4.2.3.1.1.1 rules C32-[11].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="3987" mode="M8">
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="3986" mode="M8">
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:maritalStatusCode" priority="3985" mode="M8">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.3.88.6.1633.5.2.2&#34;)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Marital Status should be coded using a code from the ASTM E1633 code system (2.16.840.1.113883.3.88.6.1633.5.2.2). CHECK code list. See Section 4.2.3.1.1.4 rule C32-[33].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:raceCode" priority="3984" mode="M8">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.6.238&#34;)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Race should be coded using the CDC Race and Ethnicity code system (2.16.840.1.113883.6.238). CHECK code list. See Section 4.2.3.1.1.4 rule C32-[34].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:ethnicGroupCode" priority="3983" mode="M8">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.6.238&#34;)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Ethnicity Group should be coded using the CDC Race and Ethnicity code system (2.16.840.1.113883.6.238). CHECK code list. See Section 4.2.3.1.1.4 rule C32-[36].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:religiousAffiliationcode" priority="3982" mode="M8">
      <axsl:choose>
         <axsl:when test="(@code and @codeSystem=&#34;2.16.840.1.113883.5.1076&#34;)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Religius Affiliation should be coded using the HL7 Religious Affiliation code system (2.16.840.1.113883.5.1076). CHECK code list. See Section 4.2.3.1.1.4 rule C32-[36].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="*[self::cda:author][not(self::cda:author[1])]" priority="3981" mode="M8">
      <axsl:choose>
         <axsl:when test="cda:assignedAuthor/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Section or Entry Secondary Author should contain a C32 Author Name element. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M8"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M8"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="4000" mode="M9">
      <axsl:choose>
         <axsl:when test="//cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.12&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 document may contain C32 Comments modules (2.16.840.1.113883.3.88.11.32.12) represented as CCD Comments entries (2.16.840.1.113883.10.20.1.40). See Table 4.2.3.1-1 and Section 4.2.3.1.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="//cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.13&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 document may contain C32 Advance Directives modules (2.16.840.1.113883.3.88.11.32.13) represented as CCD Advance Directive observations (2.16.840.1.113883.10.20.1.17). See Table 4.2.3.1-1 and Section 4.2.3.1.12.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="//cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.11&#34;]//cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]//*[@code=&#34;77386006&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 document may contain a C32 Pregnancy module represented as a SNOMED "Patient currently pregnant" observation (code=77386006), or its negation, under a C32 Condition module (2.16.840.1.113883.3.88.11.32.7) in a CCD Problems section (2.16.840.1.113883.10.20.1.11). See Table 4.2.3.1-1 and Section 4.2.3.1.9 (possible typo in observation/code/@code in that 773860066 doesn't seem to exist!)<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient" priority="3999" mode="M9">
      <axsl:choose>
         <axsl:when test="cda:religiousAffiliationCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: HITSP/C32 may contain a Religious Affiliation data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:raceCode or sdtc:raceCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: HITSP/C32 may contain a Race data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:ethnicityCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: HITSP/C32 may contain an Ethnicity data element for the Patient. See Table 4.2.3.1.1-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="/cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name" priority="3998" mode="M9">
      <axsl:choose>
         <axsl:when test="count(../cda:name) = 1"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: More than one Patient Name may exist to properly retain birth names, maiden names, and aliases. See Section 4.2.3.1.1.1 rule C32-[7].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(../cda:name[2]) or ../cda:name[@use=&#34;P&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: An alias or former name for a patient may be identified with the use attribute set to P. See Section 4.2.3.1.1.1 rule C32-[8].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="3997" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]" priority="3996" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:maritalStatusCode" priority="3995" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:raceCode" priority="3994" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:ethnicGroupCode" priority="3993" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.1&#34;]/cda:recordTarget/cda:patientRole/cda:patient/cda:religiousAffiliationcode" priority="3992" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[self::cda:author][not(self::cda:author[1])]" priority="3991" mode="M9">
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="*[self::cda:externalDocument][parent::cda:reference]" priority="3990" mode="M9">
      <axsl:choose>
         <axsl:when test="cda:text/cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Section or Entry External Reference may contain a C32 Reference Document URL element. See Section 4.2.3.1.10 table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M9"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M9"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.2&#34;]" priority="4000" mode="M10">
      <axsl:choose>
         <axsl:when test="self::cda:languageCommunication"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Language Spoken templateId shall be contained in a CDA languageCommunication element.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="parent::cda:patient"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Patient Language of communication shall appear in a languageCommunication element appearing beneath the patient element. See section 4.2.3.1.2.1 rule C32-[39].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:languageCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Patient Language of communication element shall contain a languageCode element set to a code for the language of communication. See section 4.2.3.1.2.1 rule C32-[40]. NOTE: Sign language shall be treated as a separate language, e.g. sgn-US.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:modeCode) or cda:modeCode[@codeSystem=&#34;2.16.840.1.113883.5.60&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: If the Patient Language element conatins a modeCode element to express types of language expression, then that code shall come from the HL7 LanguageAbilityMode code system (2.16.840.1.113883.5.60). See section 4.2.3.1.2.1 rule C32-[44].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M10"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M10"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.2&#34;]" priority="4000" mode="M11">
      <axsl:choose>
         <axsl:when test="cda:preferenceInd"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: The Language Spoken module should have a PreferenceInd element (boolean) to indicate patient preference for that language. See section 4.2.3.1.2.1 rule C32-[41].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="count(../cda:languageCommunication) &gt; 1 and count(../cda:languageCommunication/cda:preferenceInd)=1"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: The patient Language Spoken element may mark mutiple languages as preferred. See section 4.2.3.1.2.1 rule C32-[42].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:modeCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: To indicate only a specific mode of communication (e.g written, verbal, signed, etc.), a modeCode element may be included. See section 4.2.3.1.2.1 rule C32-[43].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:modeCode[@codeSystem=&#34;2.16.840.1.113883.5.60&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: The Patient Language element may contain a modeCode element to express types of language expression. The code shall come from the HL7 LanguageAbilityMode code system (2.16.840.1.113883.5.60), which specifies the following codes: ESGN (expressed signed), ESP (expressed spoken), EWR (expressed written), RSGN (received signed), RSP (received spoken), RWR (received written). See section 4.2.3.1.2.1 rule C32-[44].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:proficiencyLevelCode)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: The proficiencyLevelcode element should not be used as it is considered too subjective. See section 4.2.3.1.2.1 rule C32-[45].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:languageCode[@code=&#34;sgn-US&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: The Patient Language element may indicate that sign language is spoken by treating it as a separate language code, e.g. sgn-US. See section 4.2.3.1.2.1 rule C32-[40].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M11"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M11"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.3&#34;]" priority="4000" mode="M12">
      <axsl:choose>
         <axsl:when test="(parent::cda:patient and self::cda:guardian[@classCode =&#34;GUARD&#34;])               or (parent::cda:ClinicalDocument and self::cda:participant)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Support templateId shall be contained in a CDA patient guardian or ClinicalDocument participant element.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M12"/>
   </axsl:template>
   <axsl:template match="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:guardian" priority="3999" mode="M12">
      <axsl:choose>
         <axsl:when test="cda:guardianPerson/cda:name/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Patient Guardian element shall contain a non-empty Guardian Person Name element. See Table 4.2.3.1.3-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="self::cda:guardian[@classCode=&#34;GUARD&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The C32 Contact Type element shall be expressed as GUARD in the classCode of the Guardian. See Section 4.2.3.1.3 rule C31-[49].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code) or cda:code[@codeSystem=&#34;2.16.840.1.113883.5.111&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Contact Relationship should be recorded in the code element beneath the Guardian element. If the code is present, the code value shall be drawn from the HL7 PersonalRelationshipRoleType value set (2.16.840.1.113883.1.11.19563) drawn from the HL7 RoleCode code system (2.16.840.1.113883.5.111). There are 72 possible codes in the value set (e.g. GRMTH, STPDAU, etc). CHECK list. See Section 4.2.3.1.3.2 rule C31-[51].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M12"/>
   </axsl:template>
   <axsl:template match="cda:ClinicalDocument/cda:participant/cda:associatedEntity" priority="3998" mode="M12">
      <axsl:choose>
         <axsl:when test="../cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Support Participant data element shall contain a Date element. See Table 4.2.3.1.3-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:associatedPerson/cda:name/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Support Participant Contact element shall contain a non-empty C32 Contact Name element. See Table 4.2.3.1.3-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="self::cda:associatedEntity[@classCode=&#34;AGNT&#34; or @classCode=&#34;CAREGIVER&#34;                   or @classCode=&#34;ECON&#34; or @classCode=&#34;NOK&#34; or @classCode=&#34;PRS&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The C32 Contact Type element shall be expressed in the classCode of the Contact role and shall be from the following list: AGNT (authorized to act on behalf of the patient), CAREGIVER (care at home), ECON (emergency contact), NOK (next of kin), PRS (personal). Guardian contacts are reported under the Patient element. See Section 4.2.3.1.3 rule C31-[49].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code) or cda:code[@codeSystem=&#34;2.16.840.1.113883.5.111&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Contact Relationship should be recorded in the code element beneath the Participant Contact element. If the code is present, the code value shall be drawn from the HL7 PersonalRelationshipRoleType value set (2.16.840.1.113883.1.11.19563) drawn from the HL7 RoleCode code system (2.16.840.1.113883.5.111). There are 72 possible codes in the value set (e.g. GRMTH, STPDAU, etc.). See Section 4.2.3.1.3.2 rule C31-[51].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M12"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M12"/>
   <axsl:template match="cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:guardian" priority="4000" mode="M13">
      <axsl:choose>
         <axsl:when test="cda:guardianPerson/cda:name[@use] and cda:guardianPerson/cda:name/child::*[@qualifier]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: The HITSP/C32 Patient Guardian element shall contain a non-empty Guardian Name element. The Guardian Name may contain a use attribute and all of the Name parts may contain a qualifier attribute. Use and qualifier attributes may be set with the same values as specified for the Patient Name element. See Table 4.2.3.1.3-2 and Section 4.2.3.1.1.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="parent::cda:patient[count(child::guardian) &lt; 2]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>NOTE: A patient may have more than one Guardian. See Section 4.2.3.1.3 rule C31-[48].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Contact Relationship should be recorded in the code element beneath the Guardian element. If the code is present, the code value shall be drawn from the HL7 PersonalRelationshipRoleType value set (2.16.840.1.113883.1.11.19563) drawn from the HL7 RoleCode code system (2.16.840.1.113883.5.111). There are 72 possible codes in the value set (e.g. GRMTH, STPDAU, etc). See Section 4.2.3.1.3.2 rule C31-[50].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M13"/>
   </axsl:template>
   <axsl:template match="cda:ClinicalDocument/cda:participant/cda:associatedEntity" priority="3999" mode="M13">
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Contact Relationship should be recorded in the code element beneath the C32 Contact element. If the code is present, the code value shall be drawn from the HL7 PersonalRelationshipRoleType value set (2.16.840.1.113883.1.11.19563) drawn from the HL7 RoleCode code system (2.16.840.1.113883.5.111). There are 72 possible codes in the value set (e.g. GRMTH, STPDAU, etc.). See Section 4.2.3.1.3.2 rule C31-[50].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M13"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M13"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.4&#34;]" priority="4000" mode="M14">
      <axsl:choose>
         <axsl:when test="ancestor::cda:documentationOf              and parent::cda:serviceEvent              and self::cda:performer[@typeCode=&#34;PRF&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Healthcare Provider data element (2.16.840.1.113883.3.88.11.32.4) shall be represented as a cda:performer element under a cda:serviceEvent under a cda:documentationOf element. See Table 4.2.3.1.4-2 and Section 4.2.3.1.4.1<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Provider data element (i.e. CDA performer) shall contain a Date Range data element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Provider data element (i.e. CDA performer) shall contain a Provider Entity data element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:functionCode)                or cda:functionCode[@codeSystem=&#34;2.16.840.1.113883.12.443&#34; and (@code=&#34;CP&#34; or @code=&#34;PP&#34; or @code=&#34;RP&#34;)]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Provider Role Coded data element (i.e. CDA functionCode), if present, shall be coded as Consulting Provider (CP), Primary Care Provider (PP) or Referring Provider (RP), a limited subset taken from the HL7 v2 Provider Role code system (2.16.840.1.113883.12.443). See rule C32-[52]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:assignedEntity/cda:code)                or (cda:assignedEntity/cda:code[@codeSystem=&#34;2.16.840.1.113883.6.101&#34;]                   and cda:assignedEntity/cda:code[substring(@code,3,10)=&#34;0000000X&#34;]                  and not(cda:assignedEntity/cda:code[substring(@code,1,2)=&#34;17&#34;])                  and not(cda:assignedEntity/cda:code[substring(@code,1,2)=&#34;19&#34;])                  and not(cda:assignedEntity/cda:code[substring(@code,1,2)=&#34;24&#34;])                  and not(cda:assignedEntity/cda:code[substring(@code,1,2)=&#34;27&#34;])                  and not(cda:assignedEntity/cda:code[substring(@code,1,2)=&#34;34&#34;]))"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Provider Type data element, if present, shall be one of 23 selected top-level values (format dd0000000X) taken from the NUCC ProviderCodes code system (2.16.840.1.113883.6.101). See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M14"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M14"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.4&#34;]" priority="4000" mode="M15">
      <axsl:choose>
         <axsl:when test="cda:functionCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider data element should contain a Provider Role Coded (i.e. CDA functionCode) data element. If present, it shall be coded as Consulting Provider (CP), Primary Care Provider (PP) or Referring Provider (RP), a limited subset taken from the HL7 v2 Provider Role code system (2.16.840.1.113883.12.443). See Table 4.2.3.1.4-2 and Section 4.2.3.1.4.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:functionCode/cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider data element should contain a Provider Role Free Text data element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a Provider Type element. If present, the Provide Type code shall be one of 23 selected top-level values (format dd0000000X) taken from the NUCC ProviderCodes code system (2.16.840.1.113883.6.101). See Table 4.2.3.1.4-2 and Section 4.2.3.1.4.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:addr/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a non-empty Provider Address element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a CDA telecom element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:assignedPerson/cda:name/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a non-empty Provider Name element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:representedOrganization/cda:name/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a non-empty Providers Organization Name element. See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/sdtc:patient/sdtc:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Provider Entity data element should contain a Providers Patient ID element (i.e. sdtc:patient/sdtc:id). See Table 4.2.3.1.4-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M15"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M15"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]" priority="4000" mode="M16">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.9&#34;]              and ancestor::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.20&#34;]              and self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.26&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Insurance Payment Providers data element shall be represented as a CCD Policy Activity act (2.16.840.1.113883.10.20.1.26) under a CCD Coverage Activity act (2.16.840.1.113883.10.20.1.20) under a CCD Payors section (2.16.840.1.113883.10.20.1.9). If no insurance payment sources are known, the reason shall be provided as free text in the narrative block (e.g. Not Insured, Payer Unknown, Medicare Pending, et cetera) of the CCD Payers section. See HITSP/C32 Section 4.2.3.1.5 and Section 4.2.3.1.5.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:performer/cda:assignedEntity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Payment Providers data element shall contain a C32 Payer element. See Table 4.2.3.1.5-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:performer/cda:assignedEntity/cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Payment Providers data element shall contain a C32 Financial Responsibility Party Type element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.11 rule C32-[75].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:performer/cda:assignedEntity/cda:code[@code and @codeSystem=&#34;2.16.840.1.113883.5.110&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Financial Responsibility Party Type element shall have a code attribute that contains a value from the HL7 RoleClassRelationshipFormal vocabulary (2.16.840.1.113883.5.110). See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.11 rule C32-[76].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code[@code=&#34;PP&#34;]) or cda:performer/cda:assignedEntity/cda:code[@code=&#34;GUAR&#34; or @code=&#34;PAT&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: If the C32 Health Insurance Type of the encompassing C32 Payment Provider (i.e. cda:code) is PP, then the C32 Financial Responsibility Party Type code attribute shall be set to GUAR or PAT to indicate a Guarantor or self-paying patient, respectively. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.11 rule C32-[77].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code) or cda:code[@code=&#34;PP&#34;] or cda:performer/cda:assignedEntity/cda:code[@code=&#34;PAYOR&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: If the C32 Health Insurance Type of the encompassing C32 Payment Provider (i.e. cda:code) is anything other than PP, then the C32 Financial Responsibility Party Type code attribute shall be set to PAYOR. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.11 rule C32-[77].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M16"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:participant[@typeCode=&#34;COV&#34;]/cda:participantRole[@classCode=&#34;PAT&#34;]" priority="3999" mode="M16">
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscribers Patient data element shall contain a C32 Relationship to Subscriber element (i.e. cda:code). See Table 4.2.3.1.5-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code[@codeSystem=&#34;2.16.840.1.113883.5.111&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscribers Patient data element shall contain a C32 Relationship to Subscriber element (i.e. cda:code) with the code attribute taken from the HL7 CoverageRoleType vocabulary (2.16.840.1.113883.5.111). CHECK list. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.8.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:playingEntity/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscribers Patient data element shall contain a C32 Patient Name element. If this element is empty, then the name shall be assumed equal to the patient name recorded in cda:recordTarget. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.9.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:playingEntity/sdtc:birthTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscribers Patient data element shall contain a C32 Patient Date of Birth element (sdtc:birthTime). If this element is empty, then the date of birth shall be assumed equal to the patient date of birth recorded in cda:recordTarget. NOTE: The sdtc:birthTime represents an extension to HL7 CDA Release 2.0. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.10.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M16"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:participant[@typeCode=&#34;HLD&#34;]/cda:participantRole" priority="3998" mode="M16">
      <axsl:choose>
         <axsl:when test="cda:id/@root and cda:id/@extension"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscriber Information data element shall contain a C32 Subscriber ID element with both root and extension attributes. The root attribute (OID or GUID) identifies the assigning authority of the extension attribute. The extension attribute is the subscriber identification number. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.13.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:addr/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscriber Information data element shall contain a non-empty C32 addr element. The address should follow the C32 address format. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:playingEntity/cda:name/*"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscriber Information data element shall contain a non-empty C32 Subscriber Name element. See Table 4.2.3.1.5-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:playingEntity/sdtc:birthTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Subscriber Information data element shall contain a C32 Subscriber Date of Birth element (sdtc:birthTime). NOTE: The sdtc:birthTime represents an extension to HL7 CDA Release 2.0. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.14.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M16"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M16"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]" priority="4000" mode="M17">
      <axsl:choose>
         <axsl:when test="cda:code[@codeSystem=&#34;2.16.840.1.113883.6.255.1336&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider element should contain a Health Insurance Type element (cda:code). The code attribute value shall come from the X12 vocabulary (2.16.840.1.113883.6.255.1336) for Insurance Type Code (X12 Data Element 1336), as restricted by the X12N 271 Transaction. CHECK list. The value "PP" shall be used to indicate self-pay or payment by a guarantor. Any other value indicates a Financial Responsibility Party Type of PAYOR. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.3 rules C32-[58], C32-[59] and C32-[60].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M17"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:performer/cda:assignedEntity[cda:code/@code=&#34;PAYOR&#34;]" priority="3999" mode="M17">
      <axsl:choose>
         <axsl:when test="../../cda:id/@extension and cda:id/@root"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Payment Provider that is of type PAYOR may contain a Group Number element (cda:id) element. The cda:id should have both root and extension attributes. The root attribute (OID or GUID) identifies the assigning authority for the extension. The extension attribute identifies the group or contract number of the Payment Provider element. See Table 4.2.3.1.5-2 and all five rules of Section 4.2.3.1.5.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan Insurance Information Source ID (cda:id) in its Payer element. The information source identifier (cda:id) corresponds to the RxBIN and RxPCN fields found on pharmacy benefit cards. The id root attribute should be the OID for RxBIN (2.16.840.1.113883.3.88.3.1) and the extension attribute should be the numeric extension to RxBIN for RxPCN. The OID for RxPCN is RxBIN plus the numeric extension. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.4.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:addr"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan Insurance Information Source Address element in its Payer element. The address should follow the C32 address format. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan Insurance Information Source Phone/Email/URL element in its Payer element. This element should follow the C32 format for telecommunications information. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:representedOrganization/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan Insurance Information Source Name in its Payer Represented Organization element. See Table 4.2.3.1.5-2..<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="../../cda:participant[@typeCode=&#34;HLD&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Subscriber Information element. If this element is not present, then the subscriber may be assumed to be the patient recorded in cda:recordTarget. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.12.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(../../cda:participant[@typeCode=&#34;HLD&#34;]) or ../../cda:participant[@typeCode=&#34;COV&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 Payment Provider that is of type PAYOR has a Subscriber Information data element, then the Payment Provider element should contain a Patient Information element. If this element is not present, then the Patient may be assumed to be the patient recorded in cda:recordTarget. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.5.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="../../cda:entryRelationship[@typeCode=&#34;REFR&#34;]/cda:act[@classCode=&#34;ACT&#34; and @moodCode=&#34;DEF&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.15.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="../../cda:entryRelationship[@typeCode=&#34;REFR&#34;]/cda:act[@classCode=&#34;ACT&#34; and @moodCode=&#34;DEF&#34;]/cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type PAYOR should contain a Health Plan Name element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M17"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:performer[cda:assignedEntity/cda:code/@code=&#34;GUAR&#34;]" priority="3998" mode="M17">
      <axsl:choose>
         <axsl:when test="cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type GUAR should contain a C32 Effective Date of Financial Responsibility element in its Guarantor Information element. See Table 4.2.3.1.5-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:addr"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type GUAR should contain a C32 Financial Responsibility Party Address element in its Guarantor Information element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type GUAR should contain a C32 Financial Responsibility Party telecom element in its Guarantor Information element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:assignedPerson/cda:name | cda:assignedEntity/cda:representedOrganization/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Payment Provider that is of type GUAR should contain a C32 Financial Responsibility Party Name element in its Guarantor Information element. See Table 4.2.3.1.5-2 and Section 4.2.3.1.1.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M17"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:participant[@typeCode=&#34;COV&#34;]/cda:participantRole[@classCode=&#34;PAT&#34;]" priority="3997" mode="M17">
      <axsl:choose>
         <axsl:when test="../cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Patient Information element should contain a Health Plan Coverage Dates element. The low attribute indicates the start date and the high attribute, if present, indicates the stop date. See Table 4.2.3.1.5-2 and Section 4.2.3.1.5.6<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M17"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]/cda:participant[@typeCode=&#34;HLD&#34;]/cda:participantRole" priority="3996" mode="M17">
      <axsl:choose>
         <axsl:when test="cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Subscriber Information element should contain a Subscriber Phone/email/URL element. See Table 4.2.3.1.5-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M17"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M17"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]" priority="4000" mode="M18">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.2&#34;]              and self::cda:act [cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.27&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP C32 Allergy and Drug Sensitivities module (2.16.840.1.113883.3.88.11.32.6) shall be represented as a CCD Problem act (2.16.840.1.113883.10.20.1.27) under a CCD Alerts section (2.16.840.1.113883.10.20.1.2). See HITSP/C32 Section 4.2.3.1.6 and Figure 4.2-20.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M18"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]" priority="3999" mode="M18">
      <axsl:choose>
         <axsl:when test="cda:code[@codeSystem=&#34;2.16.840.1.113883.6.96&#34;]               and cda:code[@code=&#34;420134006&#34; or @code=&#34;418038007&#34; or @code=&#34;419511003&#34; or @code=&#34;418471000&#34; or @code=&#34;419199007&#34;                  or @code=&#34;416098002&#34; or @code=&#34;414285001&#34; or @code=&#34;59037007&#34; or @code=&#34;235719002&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Adverse Event Entry data element shall contain an Adverse Event Type code element with code set to a limited subset of SNOMED CT (2.16.840.1.113883.6.96) terms as presented in HITSP/C32 Table 4.2.3.1.6.1-1. See Table 4.2.3.1.6-2 and Section 4.2.3.1.6.1 rule C32-[88]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M18"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:participant[@typeCode=&#34;CSM&#34;]/cda:participantRole[@classCode=&#34;MANU&#34;]/cda:playingEntity[@classCode=&#34;MMAT&#34;]" priority="3998" mode="M18">
      <axsl:choose>
         <axsl:when test="cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Product data element shall contain a Product Free-Text element to name or describe the product causing the reaction. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code) or cda:code[@codeSystem and @code]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Product Coded data element shall be coded to UNII for Food and substance allergies, or RxNorm when to medications, or NDF-RT when to classes of medications. CHECK codes! See Section 4.2.3.1.6.2 rule C32-[89]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M18"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:entryRelationship[@typeCode=&#34;MFST&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]" priority="3997" mode="M18">
      <axsl:choose>
         <axsl:when test="not(cda:value) or cda:value[@codeSystem=&#34;2.16.840.1.113883.6.96&#34; and @code]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Reaction Coded data element shall have its value coded using the VA/KP Problem List Subset of SNOMED CT (2.16.840.1.113883.6.96) and shall be terms that descend from the clinical finding (404684003) concept. CHECK list of codes. See Section 4.2.3.1.6.3 rule C32-[90].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M18"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:entryRelationship[@typeCode=&#34;MFST&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.55&#34;]" priority="3996" mode="M18">
      <axsl:choose>
         <axsl:when test="not(cda:value)                   or cda:value[@codeSystem=&#34;2.16.840.1.113883.6.96&#34;                    and (@code=&#34;255604002&#34; or @code=&#34;371923003&#34; or @code=&#34;6736007&#34; or @code=&#34;371924009&#34; or @code=&#34;24484000&#34; or @code=&#34;399166001&#34;)]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Reaction Severity data element shall have its value coded to SNOMED CT (2.16.840.1.113883.6.96) terms that descend from the severities (272141005) concept. HITSP/C32 Table 4.2.3.1.6.4-1 lists the SNOMED codes for: mild, mild to moderate, moderate, moderate to severe, severe, and fatal. See Section 4.2.3.1.6.4 rule C32-[91].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M18"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M18"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]" priority="4000" mode="M19">
      <axsl:choose>
         <axsl:when test="cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.27&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateID/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Allergies and Sensitivities module (2.16.840.1.113883.3.88.11.32.6) should contain a subject (SUBJ) entryRelationship that contains a C32 Adverse Event Entry represented as a CCD Alert Observation (2.16.840.1.113883.10.20.1.18). See Section 4.2.3.1.6 and Table 4.2.3.1.6-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M19"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]" priority="3999" mode="M19">
      <axsl:choose>
         <axsl:when test="cda:effectiveTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Adverse Event Entry data element should contain a C32 Adverse Event Date element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CSM&#34;]/cda:participantRole[@classCode=&#34;MANU&#34;]/cda:playingEntity[@classCode=&#34;MMAT&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Adverse Event Entry data element should contain a C32 Product data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M19"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:participant[@typeCode=&#34;CSM&#34;]/cda:participantRole[@classCode=&#34;MANU&#34;]/cda:playingEntity[@classCode=&#34;MMAT&#34;]" priority="3998" mode="M19">
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Product data element should contain a Product Coded data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;MFST&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Product data element may contain a Reaction data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M19"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:entryRelationship[@typeCode=&#34;MFST&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]" priority="3997" mode="M19">
      <axsl:choose>
         <axsl:when test="cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Reaction data element should contain a Reaction Free-Text data element to describe the reaction. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Reaction data element should contain a Reaction Coded data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.55&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Reaction data element should contain a Reaction Severity data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M19"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.18&#34;]/cda:entryRelationship[@typeCode=&#34;MFST&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[templateId/@root=&#34;2.16.840.1.113883.10.20.1.55&#34;]" priority="3996" mode="M19">
      <axsl:choose>
         <axsl:when test="cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Reaction Severity data element should contain a Severity Free-Text data element to describe the severity. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Reaction Severity data element should contain a Severity Coded data element. See Table 4.2.3.1.6-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M19"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M19"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]" priority="4000" mode="M20">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.11&#34;]               and self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.27&#34;] "/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSPC32 Conditions module (2.16.840.1.113883.3.88.11.32.7) shall be represented as a CCD Problem act (2.16.840.1.113883.10.20.1.27) under a CCD Problems section (2.16.840.1.113883.10.20.1.11). See HITSP/C32 Section 4.2.3.1.7 and Section 4.2.3.1.7.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.28&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSPC32 Conditions module (2.16.840.1.113883.3.88.11.32.7) represented as a CCD Problem Act (2.16.840.1.113883.10.20.1.27) shall contain a subject (SUBJ) entryRelationship with target a HITSP/C32 Problem Entry data element represented as a CCD Problem Observation (2.16.840.1.113883.10.20.1.28). See Table 4.2.3.1.7-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M20"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.28&#34;]" priority="3999" mode="M20">
      <axsl:choose>
         <axsl:when test="cda:text/cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Problem Entry data element element shall contain a free text element to record the C32 Problem Name. The Problem Name element shall contain a reference element whose value attribute points to narrative text in the parent section containing the name of the problem. See Table 4.2.3.1.7-2 and Section 4.2.3.1.7.3<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M20"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.28&#34;]/cda:code" priority="3998" mode="M20">
      <axsl:choose>
         <axsl:when test="@codeSystem=&#34;2.16.840.1.113883.6.96&#34; "/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Problem Type shall contain a codeSystem attribute that identifies the SNOMED CT codeSystem (2.16.840.1.113883.6.96). See Section 4.2.3.1.7.2 rule C32-[92]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="@code=&#34;404684003&#34; or @code=&#34;418799008&#34; or @code=&#34;55607006&#34; or @code=&#34;409586006&#34; or @code=&#34;64572001&#34; or @code=&#34;282291009&#34; or @code=&#34;248536006&#34;"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Problem Type shall shall contain a code that identifies the SNOMED CT code for one of the following seven conditions: Finding (404684003), Symptom (418799008), Problem (55607006), Complaint (409586006), Condition (64572001), Diagnosis (282291009, Functional limitation (248536006). See Section 4.2.3.1.7.2 rule C32-[93] and Table 4.2.3.1.7.2-1<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M20"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.28&#34;]/cda:value" priority="3997" mode="M20">
      <axsl:choose>
         <axsl:when test="@xsi:type=&#34;CD&#34; and @codeSystem=&#34;2.16.840.1.113883.6.96&#34; and @code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Problem Code shall be recorded in a cda:value element and coded using the VA/KP Problem List Subset of SNOMED CT (2.16.840.1.113883.6.96), and shall use SNOMED terms that descend from the clinical finding (404684003) concept. CHECK the problem list subset! See Section 4.2.3.1.7.4 rule C32-[94]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="@xsi:type=&#34;CD&#34;"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Problem Code shall be recorded in the cda:value element using an HL7 CD data type. See Section 4.2.3.1.7.4 rule C32-[95]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M20"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:performer" priority="3996" mode="M20">
      <axsl:choose>
         <axsl:when test="true()"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Treating Provider data element shall be recorded in a cda:performer element under the C32 Conditions module. See Section 4.2.3.1.7.5 rule C32-[96]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:assignedEntity/cda:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Treating Provider data element shall contain a cda:assignedEntity/cda:id element to identify the treating provider. This identifier shall be the identifier of one of the providers listed in the C32 Providers module. See Section 4.2.3.1.7.5 rule C32-[98] and Section 4.2.3.1.4.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="concat(cda:assignedEntity/cda:id/@root, @extension) = concat(/cda:ClinicalDocument/cda:documentationOf/cda:serviceEvent/cda:performer/cda:assignedEntity/cda:id/@root, @extension)"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Treating Provider data element shall contain a cda:assignedEntity/cda:id element to identify the treating provider. This identifier shall be the identifier of one of the providers listed in the C32 Providers module. See Section 4.2.3.1.7.5 rule C32-[98] and Section 4.2.3.1.4.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M20"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M20"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]" priority="4000" mode="M21">
      <axsl:choose>
         <axsl:when test="cda:performer"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A C32 Conditions module may contain a CDA performer element to record the optional C32 Treating Provider element. This identifier shall be the identifier of one of the providers listed in the C32 Providers module. See Table 4.2.3.1.7-2 and Section 4.2.3.1.7.5<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M21"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.28&#34;]" priority="3999" mode="M21">
      <axsl:choose>
         <axsl:when test="cda:effectiveTime/cda:low"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Problem Entry data element should contain a Problem Date element. The onset date shall be recorded in the low element of the effective time. The resolution date shall be recorded in the high element of the effective time. The absence of a high element indicates that the problem is not yet resolved. If the problem is known to be resolved, but the date is unknown, then the high element shall contain a nullFlavor attribute set to UNK. See Table 4.2.3.1.7-2 and Section 4.2.3.1.7.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code/@codeSystem"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Problem Entry data element should contain a Problem Type element with the codeSystem set to SNOMED CT and with the code attribute set to one of seven specified values. See Table 4.2.3.1.7-2 and Section 4.2.3.1.7.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A C32 Problem Entry data element element may contain a cda:value element to record the optional C32 Problem Code element. See Table 4.2.3.1.7-2 and Section 4.2.3.1.7.4.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M21"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]/cda:performer" priority="3998" mode="M21">
      <axsl:choose>
         <axsl:when test="cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A C32 Treating Provider data element may contain a cda:time element to record the time over which this provider treated the condition. See Section 4.2.3.1.7.5 rule C32-[97]<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M21"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M21"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]" priority="4000" mode="M22">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.8&#34;]            and self::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The HITSP/C32 Medications - Administration Information data element (2.16.840.1.113883.3.88.11.32.8) shall be represented as a CCD Medication Activity substanceAdministration act (2.16.840.1.113883.10.20.1.24) under a CCD Medications section (2.16.840.1.113883.10.20.1.8). See HITSP/C32 Section 4.2.3.1.8 Table 4.2.3.1.8-2 and Section 4.2.3.1.8.1.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:consumable/cda:manufacturedProduct[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) shall contain a C32 Medication Information element (2.16.840.1.113883.3.88.11.32.9) represented as a CCD Manufactured Product (2.16.840.1.113883.10.20.1.53). See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.9. Note: Figure 4.2-32 should have both template ids under manufacturedProduct instead of under cda:consumable to be consistent with Table 4.2.3.1.8-2 and with CCD CONF-356.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:text) or cda:text/cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Free Text Sig data element shall contain a cda:reference element whose value attribute points to the narrative portion of the CCD section. See Section 4.2.3.1.8.1 rule C32-[99].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:routeCode) or cda:routeCode[@codeSystem=&#34;2.16.840.1.113883.3.26.1.1&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Route of Administration element shall have a value drawn from the FDA route of administration code system (2.16.840.1.113883.3.26.1.1). See Table Section 4.2.3.1.8.4 rule C32-[109].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:doseQuantity) or cda:doseQuantity/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Dose Quantity element shall have a CDA value attribute. The unit attrbute may be present when needed. If present it shall be coded using the Unified Code for Units of Measure (UCUM). See Table Section 4.2.3.1.8.5 rules C32-[110] and C32-[111]. Also see rule C32-[112] for how to represent doeses described in tablets, capsules, etc.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:approachSiteCode) or cda:approachSiteCode[@codeSystem=&#34;2.16.840.1.113883.6.96&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Approach Site Code shall be from the SNOMED CT code system (2.16.840.1.113883.6.96) with a value drawn from the Anatomical Structure (91723000) hierarchy. See Section 4.2.3.1.8.6 rule C32-[113] and C32-[114].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:administrationUnitCode) or cda:administrationUnitCode[@codeSystem=&#34;2.16.840.1.113883.3.26.1.1&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Product Form unit code shall be from the FDA Dosage Form vocabulary (2.16.840.1.113883.3.26.1.1). See Section 4.2.3.1.8.7 rule C32-[115].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code) or cda:code/@originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Delivery Method element shall be described in the cda:originalText attribute of the cda:code element. See Section 4.2.3.1.8.8 rule C32-[116] and C32-[117].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M22"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:entryRelationship/cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.43&#34;]/cda:text" priority="3999" mode="M22">
      <axsl:choose>
         <axsl:when test="cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Fulfillment Instructions data element shall contain a cda:reference element whose value attribute points to the narrative text that contains the instructions. See Section 4.2.3.1.8.19 rule C32-[147].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M22"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M22"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]" priority="4000" mode="M23">
      <axsl:choose>
         <axsl:when test="not(cda:effectiveTime) or cda:effectiveTime[1]/cda:high"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: The HITSP/C32 Indicate Medication Stoped element, if known, shall be recorded in the high element of the first effectiveTime element of the Administration Information element. See Section 4.2.3.1.8.2 rule C32-[100].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship/cda:observation[@cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) should contain a C32 Type of Medication observation (2.16.840.1.113883.3.88.11.32.10) to classify the Medication as prescription or over-the-counter. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;REFR&#34;]/cda:supply[@moodCode=&#34;RQO&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) should contain a C32 Order Information supply element (2.16.840.1.113883.3.88.11.32.11) in RQO mood. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;REFR&#34;]/cda:observation[@cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.47&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) should contain a CCD Representation of Status (2.16.840.1.113883.10.20.1.47) observation. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.12.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M23"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:consumable/cda:manufacturedProduct[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.53&#34;]" priority="3999" mode="M23">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) contains a CCD manufacturedProduct element (2.16.840.1.113883.10.20.1.53), then that product element should also contain the C32 templateId for Medication Information (2.16.840.1.113883.3.88.11.32.9). See Section 4.2.3.1.8.9.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M23"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]/cda:observation" priority="3998" mode="M23">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) contains a SUBJ observation, then that observation should contain the C32 templateId for Type of Medication (2.16.840.1.113883.3.88.11.32.10). See Section 4.2.3.1.8.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M23"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:entryRelationship[@typeCode=&#34;REFR&#34;]/cda:supply[@moodCode=&#34;RQO&#34;]" priority="3997" mode="M23">
      <axsl:choose>
         <axsl:when test="cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: If a HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) contains a C32 Order Information element, then that element should contain the C32 templateId for Order Information (2.16.840.1.113883.3.88.11.32.11). See Section 4.2.3.1.8.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M23"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:entryRelationship/cda:supply[@moodCode=&#34;EVN&#34;]" priority="3996" mode="M23">
      <axsl:choose>
         <axsl:when test="cda:id"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Fulfillment History data element should contain a Prescription Number element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.20.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:quantity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Fulfillment History data element should contain a Quantity Dispensed element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.22.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M23"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M23"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]" priority="4000" mode="M24">
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;RSON&#34;]/cda:observation"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a CCD Reason Indicator whoce target represents the reason for the condition. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.13.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship/cda:act[@cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.49&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a CCD Patient Instructions (2.16.840.1.113883.10.20.1.49) act. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.14.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship[@typeCode=&#34;CAUS&#34;]/cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.54&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a CCD Reaction (2.16.840.1.113883.10.20.1.54) observation. See Table 4.2.3.1.8-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CSM&#34;]/cda:participantRole[cda:code/@code=&#34;412307009&#34; and cda:code/@codeSystem=&#34;2.16.840.1.113883.6.96&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a C32 Vehicle participant. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.15.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:precondition/cda:criteria"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a C32 Dose Indicator precondition. See Table 4.2.3.1.8-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Free Text Sig element. See Table 4.2.3.1.8-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime[1]/cda:high"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain an Indicate Medication Stopped element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime[2]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain Administration Timing, Frequency, Interval, and Duration data elements. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.3.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:routeCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Route element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.4.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:doseQuantity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Dose element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.5.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:approachSiteCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Site element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.6.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Dose Restriction element. See Table 4.2.3.1.8-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:administrationUnitCode"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Product Form element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.7.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Delivery Method element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.8.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship/cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.43&#34;]/cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Fulfillment Instructions element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.19.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:entryRelationship/cda:supply[@moodCode=&#34;EVN&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Administration Information data element (2.16.840.1.113883.3.88.11.32.8) may contain a Fulfillment History element. See Table 4.2.3.1.8-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M24"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]/cda:entryRelationship/cda:supply[@moodCode=&#34;EVN&#34;]" priority="3999" mode="M24">
      <axsl:choose>
         <axsl:when test="cda:performer/cda:assignedEntity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Fulfillment History data element may contain a Provider element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.21.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:performer/cda:assignedEntity/cda:addr"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Fulfillment History data element may contain a Provider Location element. See Table 4.2.3.1.8-2 and Section 4.2.3.1.1.2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Fulfillment History data element may contain a Dispense Date element. See Table 4.2.3.1.8-2<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M24"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M24"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]" priority="4000" mode="M25">
      <axsl:choose>
         <axsl:when test="ancestor::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]              and self::cda:manufacturedProduct[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.53&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Medication Information data element (2.16.840.1.113883.3.88.11.32.9) shall be represented as a CCD Manufactured Product (2.16.840.1.113883.10.20.1.53) under a CCD Medication Activity (2.16.840.1.113883.10.20.1.24). See Section 4.2.3.1.8.9 and CCD rule CONF-356. Note The example in Figure 4.2-32 is inconsistent with Table 4.2.1.3.1.8-2 9, which defines Medication Information as a manufacturedProduct, and with CCD CONF-356; both template ids should be placed under manufacturedProduct rather than under the consumable element.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M25"/>
   </axsl:template>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]/cda:manufacturedProduct/cda:manufacturedMaterial/cda:code" priority="3999" mode="M25">
      <axsl:choose>
         <axsl:when test="@codeSystem=&#34;2.16.840.1.113883.6.88&#34; or cda:translation[@codeSystem=&#34;2.16.840.1.113883.6.88&#34;]               or @codeSystem=&#34;2.16.840.1.113883.6.69&#34; or cda:translation[@codeSystem=&#34;2.16.840.1.113883.6.69&#34;]               or @codeSystem=&#34;2.16.840.1.113883.4.209&#34;               or @codeSystem=&#34;2.16.840.1.113883.4.9&#34;               or (not(@code) and not(@codeSystem)) "/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The product name or brand name shall be coded using code system RxNorm (2.16.840.1.113883.6.88) or NDC (2.16.840.1.113883.6.69). The code shall appear in the code attribute of the code or translation element. When only the class of a drug is known (e.g. Beta Blocker or Sulfa Drug), it shall be coded using NDF-RT (2.16.840.1.113883.4.209). FDA Unique Ingredient Identifier codes (UNII) may be used when there are no suitable codes in the other vocabularies to identify the medication. If the code for a generic product is unknown, the code and codeSystem attributes may be omitted. See Section 4.2.3.1.8.9 and rules C32-[118] through C32-[121].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The product (generic) name shall appear in the originalText element beneath the code element. See Section 4.2.3.1.8.9 and rule C32-[122].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:translation) or cda:translation/@code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The code for the specific brand of a product, if known, shall appear in a translation element under the code element. See Section 4.2.3.1.8.9 and rule C32-[123].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M25"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M25"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]/cda:manufacturedMaterial" priority="4000" mode="M26">
      <axsl:choose>
         <axsl:when test="cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: The brand name of a Medication, if known, shall appear in the name element of the manufacturedMaterial. See Section 4.2.3.1.8.9 and rule C32-[124].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M26"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M26"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]" priority="4000" mode="M27">
      <axsl:choose>
         <axsl:when test="cda:manufacturerOrganization"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Medication Information data element may contain information about the manufacturer organization in a cda:manufacturerOrganization element. See Table 4.2.1.3.8-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M27"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M27"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]" priority="4000" mode="M28">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.8&#34;]              and parent::cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]              and self::cda:observation"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Type of Medication data element (2.16.840.1.113883.3.88.11.32.10) shall be represented as an observation entry under an entryRelationship of type subject (SUBJ) in a CCD Medications section (2.16.840.1.113883.10.20.1.8). See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="ancestor::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]                or ancestor::cda:supply[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.34&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Type of Medication data element (2.16.840.1.113883.3.88.11.32.10) shall be contained in a CCD substanceAdministration entry (2.16.840.1.113883.10.20.1.24) or in a CCD supply entry (2.16.840.1.113883.10.20.1.34). See Section 4.2.3.1.8.11 rule C32-[125].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Type of Medication data element shall have a code element that represents the kind of medication actually or intended to be administered or supplied. See Section 4.2.3.1.8.11 rule C32-[128].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code[@codeSystem=&#34;2.16.840.1.113883.6.96&#34; and (@code=&#34;329505003&#34; or @code=&#34;73639000&#34;)]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Type of Medication data element shall have a code element with a code attribute taken from SNOMED CT (2.16.840.1.113883.6.96) and with the code restricted to Over the counter products (329505003) or prescription drug (73639000). See Section 4.2.3.1.8.11 rule C32-[129].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M28"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M28"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]" priority="4000" mode="M29">
      <axsl:apply-templates mode="M29"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M29"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]" priority="4000" mode="M30">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@code=&#34;2.16.840.1.113883.10.20.1.8&#34;]              and parent::cda:entryRelationship[@typeCode=&#34;REFR&#34;]              and self::cda:supply[@moodCode=&#34;RQO&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) shall be represented as a CCD supply entry, in RQO mood, under a refers to (REFR) entryRelationship in a CCD Medications section (2.16.840.1.113883.10.20.1.8). It may be recorded as part of the fufillment history or as part of the administration information. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="ancestor::cda:substanceAdministration[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.24&#34;]                or ancestor::cda:supply[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.34&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) shall be contained in a CCD substanceAdministration entry (2.16.840.1.113883.10.20.1.24) or in a CCD supply entry (2.16.840.1.113883.10.20.1.34). See Section 4.2.3.1.8.16.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M30"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M30"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]" priority="4000" mode="M31">
      <axsl:choose>
         <axsl:when test="cda:quantity"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) should contain a C32 Quantity Ordered element to report the quantity contained in a single order. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.18.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M31"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M31"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]" priority="4000" mode="M32">
      <axsl:choose>
         <axsl:when test="cda:repeatNumber"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) may contain a C32 Fills element to report the number of times the order may be filled. See Table 4.2.3.1.8-2 and Section 4.2.3.1.8.17.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:author/cda:assignedEntity/cda:assignedPerson/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) may contain a C32 Ordering Provider element to report the name of the author of the order. See Table 4.2.3.1.8-2 and Section 4.2.3.1.10.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:author/cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Note: A HITSP/C32 Order Information data element (2.16.840.1.113883.3.88.11.32.11) may contain a CDA Author element with a Date and Time to record the date and time of the order creation. See Table 4.2.3.1.8-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M32"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M32"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.12&#34;]" priority="4000" mode="M33">
      <axsl:choose>
         <axsl:when test="self::cda:act[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.40&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Comments Module (2.16.840.1.113883.3.88.11.32.12) shall be represented as a CCD Comment act (2.16.840.1.113883.10.20.1.40). See Section 4.2.3.1.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.5&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.6&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.7&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.8&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.9&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.10&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.11&#34;]               or ../../cda:templateId[@root=&#34;2.16.840.1.113883.3.88.11.32.13&#34;]               or ../../cda:observation[cda:code/@code=&#34;773860066&#34; and cda:code/@codeSystem=&#34;2.16.840.1.113883.6.96&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Comments Module (2.16.840.1.113883.3.88.11.32.12) shall be included only in a HITSP/C32 content module defined in one of the following sections: CCD Payers, CCD Alerts, CCD Problems, CCD Medications, CCD Advance Directives, or under a HITSP/C32 Pregnancy Module. See Section 4.2.3.1.11.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="ancestor-or-self::*/cda:author[1]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Comments module shall have an author, either defined directly in the comment or as the first author of a parent element. See Section 4.2.3.1.11 Table 4.2.3.1.11-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:text"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Comments module shall contain a C32 Free Text Comment data element. See Section 4.2.3.1.11 Table 4.2.3.1.11-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="parent::cda:entryRelationship"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: Comments shall be included in entries using an entryRelationship element. See Section 4.2.3.1.11 rule C32-[160].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="parent::cda:entryRelationship[@typeCode=&#34;SUBJ&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The typeCode attribute of the entryRelationship shall be SUBJ. See Section 4.2.3.1.11 rule C32-[161].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="parent::cda:entryRelationship[@inversionInd=&#34;true&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The inversionInd attribute of the entryRelationship shall be true. See Section 4.2.3.1.11 rule C32-[162].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:text/cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The Free Text Comment data element shall contain a reference element whose value attribute points to the text of the comment in the narrative portion of the parent CCD section. See Section 4.2.3.1.11 rule C32-[163].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="ancestor-or-self::*/cda:author[1]/cda:assignedAuthor/cda:assignedPerson/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The name of the C32 Author shall be provided in the name element of the assignedPerson under the assignedAuthor. See Section 4.2.3.1.11 rule C32-[164] and Section 4.2.3.1.10.1 rule C32-[158].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="ancestor-or-self::*/cda:author[1]/cda:time"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The C32 Author data element shall contain an Author Time element. See Section 4.2.3.1.11 rule C32-[164] and Section 4.2.3.1.10.1 Table 4.2.3.1.10-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M33"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M33"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.12&#34;]" priority="4000" mode="M34">
      <axsl:apply-templates mode="M34"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M34"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.13&#34;]" priority="4000" mode="M35">
      <axsl:choose>
         <axsl:when test="ancestor::cda:section[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.1)&#34;]              and self::cda:observation[cda:templateId/@root=&#34;2.16.840.1.113883.10.20.1.17&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Advance Directive data element (2.16.840.1.113883.3.88.11.32.13) shall be represented as a CCD Advance Directive Observation (2.16.840.1.113883.10.20.1.17) under a CCD Advance Directives section (2.16.840.1.113883.10.20.1.1) . See Section 4.2.3.1.12.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code/cda:originalText"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Advance Directive shall contain an Advance Directive Free Text Type element. See Table 4.2.3.1.12-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Advance Directive shall contain an Effective Date element. See Table 4.2.3.1.12-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant/cda:participantRole"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A HITSP/C32 Advance Directive shall contain a Custodian of the Document element. See Table 4.2.3.1.12-2.<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="not(cda:code/@code) or cda:code[@code and @codeSystem=&#34;2.16.840.1.113883.6.96&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Advance Directive with Coded Type shall contain an advance directive code from from the advance directive subset of SNOMED CT (2.16.840.1.113883.6.96). CHECK list of values. See Section 4.2.3.1.12.1 rule C32-[165].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:code/cda:originalText/cda:reference/@value"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The human readable description of the type of advance directive shall appear in the narrative text of the parent section and shall be pointed to by the value attribute of the reference element inside the originalText element of the code element. See Section 4.2.3.1.12.2 Rule C32-[167].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime/cda:low"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The starting time of the C32 Advance Directive shall be recorded in the low element of the Effective Date. See Section 4.2.3.1.12.3 Rule C32-[168].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime/cda:low[@value or @nullValue=&#34;UNK&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: If the starting time of an Advance Directive is unknown, then the low element of its Effective Date shall have a nullFlavor attribute set to UNK. See Section 4.2.3.1.12.3 Rule C32-[169].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime/cda:high"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: The endingtime of the C32 Advance Directive shall be recorded in the high element of the Effective Date. See Section 4.2.3.1.12.3 Rule C32-[170].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:effectiveTime/cda:high[@value or @nullValue=&#34;UNK&#34; or @nullValue=&#34;NA&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: If the ending time of an Advance Directive is unknown, then the high element of its Effective Date shall have a nullFlavor attribute set to UNK. If the Advance Directive does not have a specified ending time, then the high element of its Effective Date shall have a nullFlavor attribute set to NA. See Section 4.2.3.1.12.3 Rule C32-[171] and C32-[172].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant/cda:participantRole"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: Information required to obtain a copy of a C32 Advance Directive shall be recorded in the Custodian of the Document data element. See Section 4.2.3.1.12.4 Rule C32-[173].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CST&#34;]/cda:participantRole"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Custodian of the Document data element shall have participant typeCode set to CST. See Section 4.2.3.1.12.4 Rule C32-[174].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CST&#34;]/cda:participantRole[@classCode=&#34;AGNT&#34;]"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Custodian of the Document data element shall have classCode set to AGNT. See Section 4.2.3.1.12.4 Rule C32-[175].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant/cda:participantRole/cda:playingEntity/cda:name"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Error: A C32 Custodian of the Document data element shall contain a playingEntity element and the name of the agent who can provide a copy of the advance directive shall be recorded in the name element. See Section 4.2.3.1.12.4 Rule C32-[178].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M35"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M35"/>
   <axsl:template match="*[cda:templateId/@root=&#34;2.16.840.1.113883.3.88.11.32.13&#34;]" priority="4000" mode="M36">
      <axsl:choose>
         <axsl:when test="cda:code/@code"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A HITSP/C32 Advance Directive should contain a C32 Advance Directive Coded Type with an explicit code. See Section 4.2.3.1.12.1 rule C32-[166].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CST&#34;]/cda:participantRole[@classCode=&#34;AGNT&#34;]/cda:addr"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Custodian of the Document data element should contain an addr element with address information. See Section 4.2.3.1.12.4 Rule C32-[176].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:choose>
         <axsl:when test="cda:participant[@typeCode=&#34;CST&#34;]/cda:participantRole[@classCode=&#34;AGNT&#34;]/cda:telecom"/>
         <axsl:otherwise>
            <li>
               <a href="schematron-out.html#{generate-id(.)}" target="out" title="Link to where this pattern was expected">
                  <i/>Warning: A C32 Custodian of the Document data element should contain a telecom element with the telephone number or other electronic communications address for the agent. See Section 4.2.3.1.12.4 Rule C32-[177].<b/>
               </a>
            </li>
         </axsl:otherwise>
      </axsl:choose>
      <axsl:apply-templates mode="M36"/>
   </axsl:template>
   <axsl:template match="text()" priority="-1" mode="M36"/>
   <axsl:template match="text()" priority="-1"/>
</axsl:stylesheet>