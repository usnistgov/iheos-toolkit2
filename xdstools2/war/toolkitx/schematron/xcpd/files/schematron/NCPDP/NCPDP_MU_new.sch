<?xml version="1.0" encoding="UTF-8"?>
<!-- Schematron written for conformance statements according to the Prescriber/Pharmacist Interface Script Statndard version 8.1 January 2008 -->
<!-- These rules are defined for Meaningful Use -->

<!-- schema xmlns="http://purl.oclc.org/dsdl/schematron" -->
<schema xmlns="http://www.ascc.net/xml/schematron">
    
    <title>Meaningful Use Rules for NCPDP version 8.1 and 10.6</title>
    
    <ns prefix="script" uri="http://www.ncpdp.org/schema/SCRIPT"/>
    
    <phase id="errors">
        <active pattern="NEWRX_UIH-Errors"></active>
        <active pattern="NEWRX_UIB-Errors"></active>
        <active pattern="NEWRX_DRU-Errors"></active>
		<active pattern="NEWRX-DRU-Errors-12-13-2011"></active>
    </phase>
    
    <phase id="notes">
        <active pattern="NEWRX_UIB-Notes"></active>
        <active pattern="NEWRX_PVD_Prescriber-Notes"></active>
        <active pattern="NEWRX_PVD_Pharmacy-Notes"></active>
        <active pattern="NEWRX_PTT-Notes"></active>
        <active pattern="NEWRX_DRU-Notes"></active>
    </phase>
    
    
    <!-- MeaningFul Use Test Procedure rules for UIH - Interactive Message Header -->
    <pattern  name="NEWRX_UIH-Errors" id="NEWRX_UIH-Errors">
        <rule context="script:Message">
            <assert test="@version='008' or @version='010'">ERROR: The Message/@version shall contain the value 008.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </assert>
            <assert test="@release='001' or @release='006'">ERROR: The Message/@release shall contain the value 001.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </assert>            
        </rule>
    </pattern>
    
    <!-- MeaningFul Use Test Procedure rules for UIB - Interactive Interchange Control Header -->
    <pattern name="NEWRX_UIB-Errors" id="NEWRX_UIB-Errors">
        <rule context="script:Message/script:Header/script:From">
            <assert test="@Qualifier='D'or @Qualifier='C'">ERROR: The Message/Header/From@Qualifier shall contain value D or C.
             See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </assert>
        </rule>
        <rule context="script:Message/script:Header/script:To">
            <assert test="@Qualifier='P'">ERROR: The Message/Header/To@Qualifier shall contain the value P.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </assert>
        </rule>
    </pattern>
    
   <pattern name="NEWRX_UIB-Notes" id="NEWRX_UIB-Notes">
    <rule context="script:Message/script:Header">      
        <report test="script:MessageID">
            NOTE: The MessageID shall be populated with a transaction control reference number provided by the sender.
            See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
        </report>
        <report test="script:To">
            NOTE: The To: element shall contain an identifier representing the phamacy.   
            See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.            
        </report>   
         <report test="script:From">
            NOTE: The From element shall contain an identifier representing the prescriber.   
            See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.            
        </report>    
        
    </rule>
   </pattern>
    
    <!-- MeaningFul Use Test Procedure rules for PVD - Prescriber Field-level Conformance Statement -->
    <pattern name="NEWRX_PVD_Prescriber-Notes" id="NEWRX_PVD_Prescriber-Notes">
        <rule context="script:Message/script:Body/script:NewRx/script:Prescriber">
            <report test="script:Identification">NOTE: The /Prescriber/Identification shall be populated with a reference number representing the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:LastName">NOTE: The /Prescriber/LastName shall contain the last name of the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:Address/script:AddressLine1">NOTE: The /Prescriber/Address/AddressLine1 shall contain the street number and name associated with the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:Address/script:City">NOTE: The /Prescriber/Address/City shall contain the city name associated with the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:Address/script:State">NOTE: The /Prescriber/Address/State shall contain the State name associated with the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:Address/script:City/script:ZipCode">NOTE: The /Prescriber/Address/City/ZipCode shall contain the zip code associated with the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:PhoneNumbers/script:Phone/script:Number">NOTE: The /Prescriber/PhoneNumbers/Phone/Number shall contain a contact number for the prescriber.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
            <report test="script:PhoneNumbers/script:Phone/script:Qualifier">NOTE: The /Prescriber/PhoneNumbers/Phone/Qualifier shall be populated with appropriate code 
                list qualifier value based on type of communication.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 5: NEWRX/PVD-Prescriber Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
    
    <!-- MeaningFul Use Test Procedure rules for PVD - Pharmacy Field-level Conformance Statement -->
    <pattern name="NEWRX_PVD_Pharmacy-Notes" id="NEWRX_PVD_Pharmacy-Notes">
        <rule context="script:Message/script:Body/script:NewRx/script:Pharmacy">
            <report test="script:Identification">NOTE: The /Pharmacy/Identification shall be populated with a reference number representing the pharmacy.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 6: NEWRX/PVD-Pharmacy Field-level Conformance Statements.
            </report>
            <report test="script:StoreName">NOTE: The /Pharmacy/StoreName shall be populated with the name of the pharmacy.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 6: NEWRX/PVD-Pharmacy Field-level Conformance Statements.
            </report>
            <report test="script:PhoneNumbers/script:Phone/script:Number">NOTE: The /Pharmacy/PhoneNumbers/Phone/Number shall contain a contact number for the pharmacy.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 6: NEWRX/PVD-Pharmacy Field-level Conformance Statements.
            </report>
            <report test="script:PhoneNumbers/script:Phone/script:Qualifier">NOTE: The /Pharmacy/PhoneNumbers/Phone/Qualifier shall be populated with appropriate code 
                list qualifier value based on type of communication number provided in 090-1016-01-3148.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 6: NEWRX/PVD-Pharmacy Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
   
    <!-- MeaningFul Use Test Procedure rules for PTT - Patient Field-level Conformance Statement -->
    <pattern name="NEWRX_PTT-Notes" id="NEWRX_PTT-Notes">
        <rule context="script:Message/script:Body/script:NewRx/script:Patient">
            <report test="script:Name/script:LastName">NOTE: The /Patient/Name/LastName shall contain the last name of the patient.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 7: NEWRX/PTT-Patient Field-level Conformance Statements.
            </report>
            <report test="script:Name/script:FirstName">NOTE: The /Patient/Name/FirstName shall contain the first name of the patient.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 7: NEWRX/PTT-Patient Field-level Conformance Statements.
            </report>
            <report test="script:Gender">NOTE: The /Patient/Gender shall contain a value appropriate for patient.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 7: NEWRX/PTT-Patient Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
    
    <!-- MeaningFul Use Test Procedure rules for DRU - Patient Field-level Conformance Statement -->
    <pattern name="NEWRX_DRU-Notes" id="NEWRX_DRU-Notes">
        <rule context="script:Message/script:Body/script:NewRx/script:MedicationPrescribed">
            <report test="script:DrugDescription">NOTE: The /MedicationPrescribed/DrugDescription shall contain the full drug name, strength and form.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
              <report test="script:Quantity">NOTE: The /MedicationPrescribed/Quantity shall contain the prescribed quantity.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
            <report test="script:Quantity/script:Qualifier">NOTE: The /MedicationPrescribed/Quantity/Qualifier shall contain the appropriate
                units of measure quantity qualifer for the prescribed quantity.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
            <report test="script:Directions">NOTE: The /MedicationPrescribed/Directions shall contain the SIG instructions as written on the prescription.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
            <!-- Need to write a NOTE about the WrittenDate. The conformance statement doesn't line up. -->
            <report test="script:Refills/script:Qualtity">NOTE: The /MedicationPrescribed/Refills/Quantity shall contain the appropriate
                value based on the number of refills identified in the prescription.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
    
    <pattern name="NEWRX_DRU-Errors" id="NEWRX_DRU-Errors">
        <rule context="script:Message/script:Body/script:NewRx/script:MedicationPrescribed">
            <assert test="script:Quantity/script:CodeListQualifier = 38">ERROR: The /MedicationPrescribed/Quantity/CodeListQualifier shall contain a value = 38.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <assert test="script:DrugCoded/script:DrugDBCode">ERROR: The /MedicationPrescribed/DrugCoded/DrugDBCode shall be present and contain the appropriate medications vocabulary value 
                for prescribed medications as determined by the medications source vocabulary implemented in the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <assert test="script:DrugCoded/script:DrugDBCodeQualifier">ERROR: The /MedicationPrescribed/DrugCoded/DrugDBCodeQualifier shall contain the appropriate
                coded responsible organization identifier for the medications source vocabulary implemented within the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <!-- this is what I have now but the R isn't really a constraint. -->
            <assert test="script:Refills/script:Qualifier = R">ERROR: The /MedicationPrescribed/Refills/Qualifier shall contain a value = R.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
        </rule>
    </pattern>
	
	
    <pattern name="NEWRX-DRU-Errors-12-13-2011" id="NEWRX-DRU-Errors-12-13-2011">	
		<rule context="/script:Message/script:Body/script:NewRx/script:MedicationPrescribed"> 
		<assert test="not(script:DrugCoded/script:ProductCode)
			or
			script:DrugCoded/script:ProductCodeQualifier">
			ERROR: If /MedicationPrescribed/DrugCoded/ProductCode is present, then /MedicationPrescribed/DrugCoded/ProductCodeQualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugCoded/script:DrugDBCode)
			or
			script:DrugCoded/script:DrugDBCodeQualifier">
			ERROR: If /MedicationPrescribed/DrugCoded/DrugDBCode is present, then /MedicationPrescribed/DrugCoded/DrugDBCodeQualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugCoded/script:FormCode)
			or
			script:DrugCoded/script:FormSourceCode">
			ERROR: If /MedicationPrescribed/DrugCoded/FormCode is present, then /MedicationPrescribed/DrugCoded/FormSourceCode must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugCoded/script:FormSourceCode)
			or
			script:DrugCoded/script:FormSourceCode = 'AA'">
			ERROR: If /MedicationPrescribed/DrugCoded/FormSourceCode is present, its value should be AA.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugCoded/script:StrengthCode)
			or
			script:DrugCoded/script:StrengthSourceCode">
			ERROR: If /MedicationPrescribed/DrugCoded/StrengthCode is present, then /MedicationPrescribed/DrugCoded/StrengthSourceCode must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugCoded/script:StrengthSourceCode)
			or
			script:DrugCoded/script:StrengthSourceCode = 'AB'">
			ERROR: If /MedicationPrescribed/DrugCoded/StrengthSourceCode is present, its value should be AB.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:MedicationPrescribed/script:DrugCoded/script:DEASchedule)
			or
			not((script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C48672) 
				or (script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C48675) 
				or (script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C48676) 
				or (script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C48677)
				or (script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C48679)
				or (script:MedicationPrescribed/script:DrugCoded/script:DEASchedule = C38046)
				) ">
			ERROR: If /MedicationPrescribed/DrugCoded/DEASchedule is present, its value should be C48672 or C48675 or C48676 or C48677 or C48679 or C38046.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Quantity/script:CodeListQualifier)
			or
			script:Quantity/script:CodeListQualifier = 38">
			ERROR: If /MedicationPrescribed/Quantity/CodeListQualifier is present, its value should be 38.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Quantity/script:PotencyUnitCode)
			or
			script:Quantity/script:UnitSourceCode">
			ERROR: If /MedicationPrescribed/Quantity/PotencyUnitCode is present, then /MedicationPrescribed/Quantity/UnitSourceCode must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Quantity/script:UnitSourceCode)
			or
			script:Quantity/script:UnitSourceCode = 'AC'">
			ERROR: If /MedicationPrescribed/Quantity/UnitSourceCode is present, its value should be AC.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Refills/script:Quallifier)
			or
			not((script:Refills/script:Quallifier = R) 
				or (script:Refills/script:Quallifier = A) 
				or (script:Refills/script:Quallifier = PRN))">
			ERROR: If /MedicationPrescribed/Refills/Quallifier is present, its value should be R or A or PRN.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Refills/script:Quallifier = PRN)
			or
			script:Refills/script:Value">
			ERROR: If /MedicationPrescribed/Refills/Quallifier is PRN, then /MedicationPrescribed/Refills/Value must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Substitutions)
			or
			not ((script:Substitutions = 0) 
				or (script:Substitutions = 1) 
				or (script:Substitutions = 7) 
				or (script:Substitutions = 8)) ">
			ERROR: If /MedicationPrescribed/Substitutions is present, its value should be 0 or 1 or 7 or 8.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis)
			or
			script:Diagnosis/script:ClinicalInformationQualifier">
			ERROR: If /MedicationPrescribed/Diagnosis is present, then /MedicationPrescribed/Diagnosis/ClinicalInformationQualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis)
			or
			script:Diagnosis/script:ClinicalInformationQualifier">
			ERROR: If /MedicationPrescribed/Diagnosis is present, then /MedicationPrescribed/Diagnosis/ClinicalInformationQualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis)
			or
			script:Diagnosis/script:Primary">
			ERROR: If /MedicationPrescribed/Diagnosis is present, then /MedicationPrescribed/Diagnosis/Primary must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis/script:Primary/script:Value)
			or
			script:Diagnosis/script:Primary/script:Qualifier">
			ERROR: If /MedicationPrescribed/Diagnosis/Primary/Value is present, then /MedicationPrescribed/Diagnosis/Primary/Qualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis/script:Secondary/script:Value)
			or
			script:Diagnosis/script:Secondary/script:Qualifier">
			ERROR: If /MedicationPrescribed/Diagnosis/Secondary/Value is present, then /MedicationPrescribed/Diagnosis/Secondary/Qualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:Diagnosis/script:PriorAuthorization/script:Value)
			or
			script:Diagnosis/script:PriorAuthorization/script:Qualifier">
			ERROR: If /MedicationPrescribed/Diagnosis/PriorAuthorization/Value is present, then /MedicationPrescribed/Diagnosis/PriorAuthorization/Qualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
		<assert test="not(script:DrugUseEvaluation/script:CoAgent/script:CoAgentID)
			or
			script:DrugUseEvaluation/script:CoAgent/script:CoAgentQualifier">
			ERROR: If /MedicationPrescribed/DrugUseEvaluation/CoAgent/CoAgentID is present, then /MedicationPrescribed/DrugUseEvaluation/CoAgent/CoAgentQualifier must also be present.
			See NEWRX DRU Rules 12-13-2011 update.
		</assert>
	</rule>
    </pattern>
</schema>