Extra Metadata

Section ITI-TF 4.1.14 defines the composition and handling of 
Extra Metadata, ebRIM Slots not defined in XD* metadata.  This 
test tests the required handling by the XDS Document Registry actor.

There are two test sections defined: support and no_support. The
XDS Document Registry actor is required to implement Extra
Metadata by either 

	Accepting, not storing and returning the XdsExtraMetadataNotSaved
	warning code
	
	Accepting, storing and returning the extra metadata in query
	responses
	
The two sections align with these two approaches the Registy actor
can support.  Only one of the will succeed (they are mutually
exclusive). When logging results in Gazelle, upload the results
for both sections. 