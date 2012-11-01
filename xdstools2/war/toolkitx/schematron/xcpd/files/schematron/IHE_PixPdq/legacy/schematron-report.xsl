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
   version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" 
	xmlns:sch="http://www.ascc.net/xml/schematron">

  
<xsl:import href="http://gunshot.ncsl.nist.gov:8080/SubDiscValidationTool/schematron02/skeleton1-5.xsl"/>
<!--xsl:param name="diagnose">yes</xsl:param-->     

<xsl:template name="process-prolog">
   <axsl:output method="html" />
</xsl:template>

<xsl:template name="process-root">
   <xsl:param name="title" />
   <xsl:param name="icon" />
   <xsl:param name="contents" />
 
   <p>  
      <h1>
         <font color="#330099">Schematron Report </font>
      </h1>
      <h2 title="{@ns} {@fpi}">
         <font color="#330099"><xsl:value-of select="$title" /></font>
      </h2>
      
      <div class="errors">
         <ul>
            <xsl:copy-of select="$contents" />
         </ul>
      </div>    
   </p>
 
</xsl:template>

<xsl:template name="process-assert">
   <xsl:param name="icon" />
   <xsl:param name="pattern" />
   <xsl:param name="role" />
   <xsl:param name="diagnostics" />
   <xsl:param name="test"/>
  
   <li>
        <p><font color="#333399">
         <!--  <axsl:text>Assert: </axsl:text> -->
            <xsl:apply-templates mode="text"/>         
         </font>
        </p>
        <p><font color="#660066">
        <i>
           <axsl:text>Location:  </axsl:text>
        </i>
            <axsl:apply-templates select="." mode="schematron-get-full-path"/>
        </font>
        </p>
   
   <p><font color="#CC0000">
   <i>
      <axsl:text>Test:  </axsl:text>
   </i>
      <xsl:value-of select="@test"/>  
   </font>
   </p>
   <p></p>
   </li>
</xsl:template>

<xsl:template name="process-report">
   <xsl:param name="pattern" />
   <xsl:param name="icon" />
   <xsl:param name="role" />
   <xsl:param name="diagnostics" />
   
      <axsl:variable name="checkout">
           <xsl:value-of select="@test"/>
      </axsl:variable>
   
   <axsl:if test="$checkout !='.'"> 
   
     <li>     
         <p><font color="#333399">
          <!--  <axsl:text>Report: </axsl:text> -->
            <xsl:apply-templates mode="text"/>
            
         </font>
         </p>
         <!--
         <p><font color="#333399">
            <axsl:text>the checkout is in report </axsl:text>
            <axsl:value-of select="$checkout"/>       
         </font>
         </p>
         -->
         <p><font color="#660066">
            <i>
               <axsl:text>Location:  </axsl:text>
            </i>
            <axsl:apply-templates select="." mode="schematron-get-full-path"/>
         </font>
         </p>
         
       
         <p><font color="#CC0000">
            <i>
               <axsl:text>Test:  </axsl:text>
            </i>
            <xsl:value-of select="@test"/>  
         </font>
         </p>
       
      </li>
   </axsl:if>
</xsl:template>
  
</xsl:stylesheet>
