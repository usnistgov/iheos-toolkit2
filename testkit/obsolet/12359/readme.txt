Ret.a Retrieve mimetype

submit - Submit a document with a new mime type.

query - Query the Public Registry to see what URI attribute your 
Repository sent to represent the document.

retrieve - Use the above URI attribute to retrieve the document. Mime type,
hash, size must match the original document.

NOTE: This test uses a nonsensical mime type of text/goofy.  Your repository 
code must be able to respond to new mime types. 

NOTE: The Provide and Register transaction generated in the submit step will contain text/goofy in the metadata
mimetype attribute.  The Content-Type header of the document attachment will have a value of text/plain. This is legal
since text/goofy is a sub-type of text/plain.  
