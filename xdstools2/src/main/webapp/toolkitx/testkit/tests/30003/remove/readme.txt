This test evaluates the Document Registry's enforcement of the Document Sharing Metadata Model. 

Three steps for this evaluation: 

1) Attempt to remove all objects except the Submission Set.  Because the SubmissionSet no longer references any other metadata objects, the Document Registry should respond with the exception, XDSUnreferencedObjectException. 

2) Attempt to remove only the Submission Set. The references to the SubmissionSet object is not included in this request. As these Associations still reference the Submission Set, the Document Registry should respond with the exception, ReferencesExistException. 

3) Attempt to remove membership of the Document Entry from a Folder. This request should be successful.  As the Folder remains referenced by the Submission Set, the empty Folder may remain on the Document Registry. 

