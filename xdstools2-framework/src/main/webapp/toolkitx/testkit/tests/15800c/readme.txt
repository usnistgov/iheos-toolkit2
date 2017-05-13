Wrong transaction used

Submit a first version of a DocumentEntry via the 
update transaction.  This is illegal, you cannot use metadata update to submit the initial
version of a document. (ITI TF-2b:3.57.4.1.3.1 Rule #2)

The content of the transaction is identical to a normal Provide and Register.  The only issue is that instead of using the Provide and Register SOAP action, the Update Metadata SOAP action is used.