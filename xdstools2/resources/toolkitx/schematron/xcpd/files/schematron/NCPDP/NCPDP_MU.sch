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
            <assert test="@version='008' or @version='010'">FAIL: The Message/@version shall contain the value 008.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </assert>
            <report test="@version='008' or @version='010'">PASS: The Message/@version contains the value 008.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </report>
            <assert test="@release='001' or @release='006'">FAIL: The Message/@release shall contain the value 001.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </assert>
            <report test="@release='001' or @release='006'">PASS: The Message/@version contains the value 001.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 4: NEWRX/UIH Field-level Conformance Statements.
            </report>              
        </rule>
    </pattern>
    
    <!-- MeaningFul Use Test Procedure rules for UIB - Interactive Interchange Control Header -->
    <pattern name="NEWRX_UIB-Errors" id="NEWRX_UIB-Errors">
        <rule context="script:Message/script:Header/script:From">
            <assert test="@Qualifier='D'or @Qualifier='C'">FAIL: The Message/Header/From@Qualifier shall contain value D or C.
             See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </assert>
            <report test="@Qualifier='D' or @Qualifier='C'">PASS: The Message/Header/From@Qualifier contains value D or C.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </report>
        </rule>
        <rule context="script:Message/script:Header/script:To">
            <assert test="@Qualifier='P'">FAIL: The Message/Header/To@Qualifier shall contain the value P.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </assert>
            <report test="@Qualifier='P'">PASS: The Message/Header/To@Qualifier contain the value P.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. Table 3: NEWRX/UIB Field-level Conformance Statements.
            </report>
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
            <report test="script:Quantity/script:Refills/script:Qualtity">NOTE: The /MedicationPrescribed/Quantity/Refills/Quantity shall contain the appropriate
                value based on the number of refills identified in the prescription.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
    
    <pattern name="NEWRX_DRU-Errors" id="NEWRX_DRU-Errors">
        <rule context="script:Message/script:Body/script:NewRx/script:MedicationPrescribed">
            <assert test="script:Quantity/script:CodeListQualifier = 38">FAIL: The /MedicationPrescribed/Quantity/CodeListQualifier shall contain a value = 38.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <report test="script:Quantity/script:CodeListQualifier = 38">PASS: The /MedicationPrescribed/Quantity/CodeListQualifier contains the value = 38.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
            <assert test="script:DrugCoded/script:DrugDBCode">FAIL: The /MedicationPrescribed/DrugCoded/DrugDBCode shall be present and contain the appropriate medications vocabulary value 
                for prescribed medications as determined by the medications source vocabulary implemented in the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <report test="script:DrugCoded/script:DrugDBCode">PASS: The /MedicationPrescribed/DrugCoded/DrugDBCode shall be present and contain the appropriate medications vocabulary value 
                for prescribed medications as determined by the medications source vocabulary implemented in the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
            <assert test="script:DrugCoded/script:DrugDBCodeQualifier">FAIL: The /MedicationPrescribed/DrugCoded/DrugDBCodeQualifier shall contain the appropriate
                coded responsible organization identifier for the medications source vocabulary implemented within the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <report test="script:DrugCoded/script:DrugDBCodeQualifier">PASS: The /MedicationPrescribed/DrugCoded/DrugDBCodeQualifier shall contain the appropriate
                coded responsible organization identifier for the medications source vocabulary implemented within the EHR.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
           
            <!-- this is what I have now but the R isn't really a constraint. -->
            <assert test="script:Quantity/script:Refills/script:Qualifier = R">FAIL: The /MedicationPrescribed/Quantity/Refills/Qualifier shall contain a value = R.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </assert>
            <report test="script:Quantity/script:Refills/script:Qualifier = R">PASS: The /MedicationPrescribed/Quantity/Refills/Qualifier contains the value = R.
                See Test Procedure v1.0 for 170.304 (b) Electronic Prescribing. NEWRX Message DRU Field-level Conformance Statements.
            </report>
        </rule>
    </pattern>
</schema>