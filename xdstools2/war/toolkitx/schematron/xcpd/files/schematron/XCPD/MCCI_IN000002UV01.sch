<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!DOCTYPE schema [
<!-- Replace baseURI below with a reference to the published Implementation Guide HTML. -->
<!ENTITY baseURI "">

<!ENTITY ent-MCCI_IN000002UV01 SYSTEM 'MCCI_IN000002UV01.ent'>

]>
<schema xmlns="http://www.ascc.net/xml/schematron" queryBinding="xslt2" xmlns:msg="urn:hl7-org:v3">
    <!-- 
        To use iso schematron instead of schematron 1.5, 
        change the xmlns attribute from
        "http://www.ascc.net/xml/schematron" 
        to 
        "http://purl.oclc.org/dsdl/schematron"
    -->
    
    <title>MCCI_IN000002UV01</title>
    <ns prefix="msg" uri="urn:hl7-org:v3"/>
    
    <phase id='errors'>
        <active pattern='MCCI_IN000002UV01-errors'/>
    </phase>
    
    &ent-MCCI_IN000002UV01;
    
</schema>
