This test evaluates the Document Registry's enforcement of the Document Sharing Metadata Model. 

Two steps for this evaluation: 

1) Attempt to remove only the replacement Submission Set. The Document Registry should respond with the exception, XDSMetadataModelException, as the remaining original document is not the latest version.  

2) Attempt to remove only the original Submission Set and DE-DE RPLC Association. This request should be successful as the replacement document represents the latest version.

