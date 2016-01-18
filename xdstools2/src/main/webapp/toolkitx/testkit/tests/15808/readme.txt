FindDocuments for LeafClass to IG - one RG

The System Under Test (SUT) is an Initiating Gateway (IG) with Affinity Domain
option. The test environment, provided by Toolkit, is a Docuemnt Consumer (DC)
and a Responding Gateway (RG).

The SUT IG shall be configured to forward query and retrieve requests to the
Responding Gateway provided by the test. The test is driven by the Document
Consumer.

The Responding Gateway is initialized with content for two Patient IDs. The first
Patient ID has a single SubmissionSet containing a single DocumentEntry with Document.
The second Patient ID has two SubmissionSets each containing a single DocumentEntry
with Document.

The test contains the following sections:

OneDocQuery - sends a FindDocuments stored query to the Initiating Gateway with
the Patient ID for the single document submission.  It shall return one DocumentEntry.

TwoDocQuery - sends a FindDocuments stored query to the Initiating Gateway with
the Patient ID for the two document submission.  It shall return two DocumentEntries.

OneDocGetDocuments - Depends on output of OneDocQuery. Send GetDocuments query passing
DocumentEntry.entryUUID returned by OneDocQuery.




