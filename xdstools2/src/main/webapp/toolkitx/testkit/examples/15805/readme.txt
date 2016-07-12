Test 15805 - Registry oriented On-Demand DocumentEntry tests.

Register_OD - This section submits an On-Demand Document Entry to the Registry's ability to accept it. The submission of an On-Demand Document Entry is done with transaction ITI-61.
Register_Snapshot - This section submits a Stable DocumentEntry to the Registry. The submission of a Stable DocumentEntry is done with transaction ITI-42.
Query_OD_FindDocuments - Verify the Registry under test returns only the On-Demand Document Entry previously submitted in the register section when queried for using FindDocuments stored query.
Query_OD_GetAll - Verify the Registry under test returns only the On-Demand Document Entry previously submitted in the register section when queried for using GetAll stored query.
Query_OD_GetSSAndContents - Verify the Registry under test returns only the On-Demand Document Entry previously submitted in the register section when queried for using GetSubmissionSetAndContents stored query.
Query_Stable_FindDocuments - Verify the Registry under test returns only the Stable DocumentEntry previously submitted in the RegisterStable section when queried for using FindDocuments stored query.
Query_Stable_GetAll - Verify the Registry under test returns only the Stable DocumentEntry previously submitted in the RegisterStable section when queried for using GetAll stored query.
Query_Stable_GetSSAndContents - Verify the Registry under test returns only the Stable DocumentEntry previously submitted in the RegisterStable section when queried for using GetSubmissionSetAndContents stored query.
Query_Both_FindDocuments - Verify the Registry under test returns both the On-Demand and the Stable DocumentEntries using FindDocuments stored query.
Query_Both_GetAll - Verify the Registry under test returns both the On-Demand and the Stable DocumentEntries using GetAll stored query.


