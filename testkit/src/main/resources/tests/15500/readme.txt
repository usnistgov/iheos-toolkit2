XCPD Find Patient Messages

Tests the ability of a responding gateway to respond to a find patient request (305). This tests the response message (306).

The test is composed of three steps stored in three different XML files:
- 1. test1_findpatient_dob_gender_name
	Attempts to locate a patient using DOB, gender and name. Expected status: success.
- 2. test2_findpatient_gender_name_failure
	Attempts to locate a patient by name only, which means DOB is voluntarily missing. Expected status: failure.
- 3. test3_findpatient_dob_gender_name_failure
	Attempts to locate a patient using (wrong) gender and name. Expected status: failure.
	

I will put in more tests later in order to test more parameters: PatientID and address.



--- Data ---
Here is the data we use for this series of tests. You will probably need to enter it manually in your system before we can arrange for something more permanent.

PatientID
                assigningAuthorityName: domain1
                ext: 100
                root: 1.2.3.4.5.1000
Name
                Given: Chip
                Family: Moore

Gender: M
DOB: 19849711
Address:
                City: Montreal



--- Addressed issues ---
- The issue dated of ~December 15th, 2011 regarding missing DOBs in some of the tests has been fixed.



--- Contact ---
diane.azais@nist.gov
