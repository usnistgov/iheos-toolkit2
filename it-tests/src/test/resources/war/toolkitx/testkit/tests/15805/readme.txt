Test 15805 - Registry oriented On-Demand DocumentEntry tests.

Note: 
1) A Patient ID must be registered using a PIF (See Test 15804).
2) The same Patient ID must be used for the all tests sections in Test 15806.

Register_OD - This section submits an On-Demand Document Entry to the Registry's ability to accept it. The submission of an On-Demand Document Entry is done with transaction ITI-61.
Query_OD - Verify the Registry under test returns only the On-Demand Document Entry previously submitted in the register section when queried for.
Register_Stable - This section submits a Stable DocumentEntry to the Registry. The submission of a Stable DocumentEntry is done with transaction ITI-42.
Query_Stable - Verify the Registry under test returns only the Stable DocumentEntry previously submitted in the RegisterStable section when queried for.
Query_Both - Verify the Registry under test returns both the On-Demand and the Stable DocumentEntries.


