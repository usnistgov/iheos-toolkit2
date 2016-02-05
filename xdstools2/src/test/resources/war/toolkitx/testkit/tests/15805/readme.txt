Test 15805 - Registry oriented On-Demand DocumentEntry tests.

Note: 
1) A Patient ID must be registered using a PIF (See Test 15804).
2) The same Patient ID must be used for the all tests sections in Test 15806.

Register1_OD - This section submits an On-Demand DocumentEntry to the Registry's ability to accept them and to support the queries that follow. The submission of an On-Demand DocumentEntry is done with transaction ITI-61.
Query1_OD - Verify the Registry under test returns only the On-Demand DocumentEntry previously submitted in the RegisterODDE section when queried for.
Register2_Stable - This section submits a Stable DocumentEntry to the Registry. The submission of a Stable DocumentEntry is done with transaction ITI-42.
Query2_Stable - Verify the Registry under test returns only the Stable DocumentEntry previously submitted in the RegisterStable section when queried for.
Query3_Both - Verify the Registry under test returns both the On-Demand and the Stable DocumentEntries.


