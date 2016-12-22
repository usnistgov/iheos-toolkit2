When the test section is run, the testkit will evaluate the metadata sent with
the Provide and Register (RAD-68) Request to the repository/registry by the IDS 
SUT. Values in the metadata will be compared with those in a 'gold standard' 
metadata file object which is part of the testkit. Validations include:
<ol>
<li/>The Patient ID values are the same.
<li/>The Mime Type values are the same.
<li/>The Format Code values are the same. &#42;
<li/>The Unique ID values are the same.
</ol>

&#42; Code values represented in metadata Classification elements are considered to be the same if:
<ol> 
<li/>The values of the nodeRepresentation attribute of the Classification 
element are the same, and
<li/>The Slot element named "codingScheme" Value element text values are the 
same.
</ol>