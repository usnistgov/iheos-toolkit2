This test evaluates the Document Registry's enforcement of the Document Sharing Metadata Model. 

Three steps for this evaluation: 

1) Attempt to remove only the Submission Set Object.  References, not included in the Remove Metadata Request, will remain on the Document Registry and trigger the exception, ReferencesExistException. 

2) Attempt to remove the DocumentEntry and Association object. The reference to the SubmissionSet object is not included in this request. Becuase the SubmissionSet no longer references any other metadata objects, the Document Registry should respond with the exception, XDSUnreferencedObjectException. 

3) Attempt to remove all members of the original submission including the SubmissionSet, DocumentEntry, and Association object.  This request should be successful. 


