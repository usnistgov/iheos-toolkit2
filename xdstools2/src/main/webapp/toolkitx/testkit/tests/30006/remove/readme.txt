This test evaluates the Document Registry's enforcement of the Document Sharing Metadata Model. 

Five steps for this evaluation: 

1) Attempt to remove only the original Submission Set. The Document Registry should respond with the exception, ReferencesExistException, as it remains referenced by the replacement DocumentEntry.  

2) Attempt to remove the original Submission Set and DE-DE XFRM_RPLC Association. This request should be successful.

3) Attempt to remove the replacement Submission Set. The Document Registry should prevent this and respond with the exception, ReferencesExistException, as it remains referenced by the appended DocumentEntry object.

4) Attempt to remove only the appended Submission Set and DE-DE APND Association. This request should be successful.

5) Attempt to remove the replacement Submission Set. This request should be successful as the replacement DocumentEntry object is no long referenced.
