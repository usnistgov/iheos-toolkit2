This test evaluates the Document Registry's enforcement of the Document Sharing Metadata Model. 

Three steps for this evaluation: 

1) Attempt to remove the DE-DE APND Association. The Document Registry should prevent this and respond with the exception, XDSMetadataModelException.

2) Attempt to remove only the original Submission Set and DE-DE APND Association. The Document Registry should respond with the exception, XDSMetadataModelException, as the appended document is referencing the original submission set.  

3) Attempt to remove the Submission Set containing the appended document. This request should be successful. The original submission set will remain on the Document Registry, but no longer be referenced.

