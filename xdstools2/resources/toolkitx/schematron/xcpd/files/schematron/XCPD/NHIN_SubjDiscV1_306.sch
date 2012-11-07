<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!DOCTYPE schema [
<!-- Replace baseURI below with a reference to the published Implementation Guide HTML. -->
<!ENTITY baseURI "">

<!ENTITY ent-NHIN_SubjDiscV1_306 SYSTEM 'NHIN_SubjDiscV1_306.ent'>
<!ENTITY ent-IHE_XCPD2010_306 SYSTEM 'IHE_XCPD2010_306.ent'>
<!ENTITY ent-MCCI_IN000100UV01 SYSTEM 'MCCI_IN000100UV01.ent'>

]>
<schema xmlns="http://www.ascc.net/xml/schematron" queryBinding="xslt2" xmlns:msg="urn:hl7-org:v3">
    <!-- 
        To use iso schematron instead of schematron 1.5, 
        change the xmlns attribute from
        "http://www.ascc.net/xml/schematron" 
        to 
        "http://purl.oclc.org/dsdl/schematron"
    -->
    
    <title>NHIN_SubjectDiscovery_V1 Initiatng Gateway 306</title>
    <ns prefix="msg" uri="urn:hl7-org:v3"/>
    <ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance"/>

    <phase id='errors'>
        <active pattern='NHIN_SubjDiscV1_306-errors'/>
        <active pattern='NHIN_SubjDiscV1_306-errors'/>
        <!--active pattern='IHE_XCPD2010_306-errors'/-->       
        <active pattern='MCCI_IN000100UV01-errors'/>      
    </phase>
    
   <!--&ent-NHIN_SubjDiscV1_306;-->
   &ent-NHIN_SubjDiscV1_306;
   &ent-IHE_XCPD2010_306; 
   &ent-MCCI_IN000100UV01;
    
</schema>
