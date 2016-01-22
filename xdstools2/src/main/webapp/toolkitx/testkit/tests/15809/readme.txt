Retrieve request to IG (one RG)

The System Under Test (SUT) is an Initiating Gateway (IG) with Affinity Domain
option. The test environment, provided by Toolkit, is a Document Consumer (DC)
initiating the requests
and a Responding Gateway (RG) responding to requests coming out of the Initiating Gateway.

This test contains the following sections:

OneDocRetrieve - toolkit's Document Consumer issues a Retrieve request to the IG
for the Document corresponding to the DocumentEntry returned in test 15808/OneDocFindDocuments.
The homeCommunityId, repositoryId, documentUniqueId, and the mimeType from the response
are validated.