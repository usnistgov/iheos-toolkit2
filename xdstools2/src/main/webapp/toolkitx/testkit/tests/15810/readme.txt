FindDocuments for LeafClass request to IG (two RGs)

The System Under Test (SUT) is an Initiating Gateway (IG) with Affinity Domain
option. The test environment, provided by Toolkit, is a Document Consumer (DC)
initiating the requests
and two Responding Gateways responding to requests coming out of the Initiating Gateway.

The SUT IG shall be configured to forward query and retrieve requests to both of the
Responding Gateways provided by the test. The test is driven by the Document
Consumer.

Each Responding Gateway is initialized with content for the same Patient ID. It
has a single SubmissionSet containing a single DocumentEntry with Document.

The test contains the following sections:

TwoDocFindDocuments - sends a FindDocuments stored query to the Initiating Gateway with
the Patient ID that matches a single DocumentEntry held behind each RG.
Both of the returned DocumentEntry.homeCommunityId shall match the configured
value for the RG that returned them.



