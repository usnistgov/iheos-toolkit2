<?xml version="1.0" ?>
<!-- Report Generator for the Schematron XML Schema Language.
	http://www.ascc.net/xml/resource/schematron/schematron.html
   
 Copyright (c) 2000,2001 David Calisle, Oliver Becker,
	 Rick Jelliffe and Academia Sinica Computing Center, Taiwan

 This software is provided 'as-is', without any express or implied warranty. 
 In no event will the authors be held liable for any damages arising from 
 the use of this software.

 Permission is granted to anyone to use this software for any purpose, 
 including commercial applications, and to alter it and redistribute it freely,
 subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not claim
 that you wrote the original software. If you use this software in a product, 
 an acknowledgment in the product documentation would be appreciated but is 
 not required.

 2. Altered source versions must be plainly marked as such, and must not be 
 misrepresented as being the original software.

 3. This notice may not be removed or altered from any source distribution.

    1999-10-25  Version for David Carlisle's schematron-report error browser
    1999-11-5   Beta for 1.2 DTD
    1999-12-26  Add code for namespace: thanks DC
    1999-12-28  Version fix: thanks Uche Ogbuji
    2000-03-27  Generate version: thanks Oliver Becker
    2000-10-20  Fix '/' in do-all-patterns: thanks Uche Ogbuji
    2001-02-15  Port to 1.5 code
    2001-03-15  Diagnose test thanks Eddie Robertsson
-->

<!-- Schematron report -->

<xsl:stylesheet
   version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" 
	xmlns:sch="http://www.ascc.net/xml/schematron">
   <xsl:import href="skeleton1-5.xsl"/>   
  
 <xsl:template name="process-prolog">
   <axsl:output method="xml" />
</xsl:template>

<xsl:template name="process-root">
   <xsl:param name="title" />
   <xsl:param name="icon" />
   <xsl:param name="contents" />
<!--
    <axsl:element name="Results" namespace="urn:gov:nist:cdaGuideValidator">
-->
    <axsl:element name="Results">
            <axsl:attribute name="severity">
               <xsl:value-of select="$phase"/>
            </axsl:attribute>
            <axsl:attribute name="specification">
               <xsl:value-of select="//*[local-name()='title']"/>
            </axsl:attribute>
            <axsl:attribute name="severity">
               <xsl:value-of select="$phase"/>
            </axsl:attribute>
            <xsl:copy-of select="$contents" />
   </axsl:element>  
</xsl:template>

<xsl:template name="process-assert">
   <xsl:param name="title"></xsl:param>
   <xsl:param name="icon" />
   <xsl:param name="pattern" />
   <xsl:param name="role" />
   <xsl:param name="diagnostics" />
   <xsl:param name="test"/>
  
<!-- 
    <axsl:element name="validationResult">
-->
       <axsl:element name="issue">
           <axsl:element name="message">
                 <xsl:apply-templates mode="text"/>
           </axsl:element>
            <axsl:element name="context">         
                 <axsl:apply-templates select='.' mode='schematron-get-full-path'/>  
            </axsl:element>
           <axsl:element name="test">
                <xsl:value-of select="@test"/>  
           </axsl:element>  
<!--               
          <axsl:element name="specification">
             <xsl:value-of select="//*[local-name()='title']"/>
           </axsl:element>  
-->
         </axsl:element>
<!--
    </axsl:element>
-->
</xsl:template>

<xsl:template name="process-report">
   <xsl:param name="pattern" />
   <xsl:param name="icon" />
   <xsl:param name="role" />
   <xsl:param name="diagnostics" />
<!--
   <axsl:element name="validationResult">
-->
      <axsl:element name="issue">
<!--
         <axsl:attribute name="severity">
            <xsl:value-of select="$phase"/>
         </axsl:attribute>
-->
         <axsl:element name="message">
            <xsl:apply-templates mode="text"/>
         </axsl:element>
         <axsl:element name="context">         
            <axsl:apply-templates select='.' mode='schematron-get-full-path'/>  
         </axsl:element>
         <axsl:element name="test">
            <xsl:value-of select="@test"/>  
         </axsl:element>   
<!--
         <axsl:element name="specification">
            <xsl:value-of select="//*[local-name()='title']"/>
         </axsl:element>  
-->
      </axsl:element>
<!--
   </axsl:element> 
-->
</xsl:template>

<axsl:template match="*|@*" mode="schematron-get-full-path-modify">
			<axsl:apply-templates select="parent::*" mode="schematron-get-full-path-modify"/>
			<axsl:text>/</axsl:text>
			<axsl:if test="count(. | ../@*) = count(../@*)">@</axsl:if>
			<axsl:value-of select="name()"/>
			<axsl:text>[</axsl:text>
	  		<axsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
	  		<axsl:text>]</axsl:text>
</axsl:template>

</xsl:stylesheet>
