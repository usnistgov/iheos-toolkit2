<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!DOCTYPE schema [
<!-- Replace baseURI below with a reference to the published Implementation Guide HTML. -->
<!ENTITY baseURI "">
<!-- ENTITY ent-PRPA_IN201309UV02 SYSTEM '/usr/local/tomcat/apache-tomcat-5.5.26/webapps/pixpdq3ValidationTool/schematronRules/PDQ/PRPA_IN201306UV02/templates/PRPA_IN201306UV02.ent' -->
<!-- ENTITY ent-PRPA_IN201306UV02 SYSTEM 'PRPA_IN201306UV02.ent' -->
<!ENTITY ent-PRPA_IN201306UV02 SYSTEM '/usr/local/tomcat/apache-tomcat-6.0.18/webapps/SubDiscValidationTool/schematron02/Connectathon2010/PDQ/PRPA_IN201306UV02/templates/PRPA_IN201306UV02.ent'>
]>
<schema xmlns="http://www.ascc.net/xml/schematron" xmlns:msg="urn:hl7-org:v3">
    <!-- 
        To use iso schematron instead of schematron 1.5, 
        change the xmlns attribute from
        "http://www.ascc.net/xml/schematron" 
        to 
        "http://purl.oclc.org/dsdl/schematron"
    -->
    
    <title>PRPA_IN201306UV02</title>
    <ns prefix="msg" uri="urn:hl7-org:v3"/>
    <ns prefix="xsi" uri="http://www.w3.org/2001/XMLSchema-instance"/>
    
    
    <phase id='errors'>
        <active pattern='PRPA_IN201306UV02-errors'/>
    </phase>
    
    <!--
        <phase id='warning'>
        <active pattern=' '/>
        </phase>
        
        <phase id='manual'>
        <active pattern=''/>
        </phase>
    -->
    
        <phase id='note'>
        <active pattern='PRPA_IN201306UV02-note'/>
        </phase>
        
    
    
    
    &ent-PRPA_IN201306UV02;
    
</schema>
