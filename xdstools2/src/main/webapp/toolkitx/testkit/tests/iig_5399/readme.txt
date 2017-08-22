Initiating Imaging Gateway: Read This First
<h2>Initiating Imaging Gateway: Read This First</h2>

<p>The System Under Test (SUT) is an Initiating Imaging Gateway
</p>
<p>You need to configure your Initiating Imaging Gateway to communicate with
   simulators listed in the conformance testing tool.
   If you are reading this document as a standalone document, that configuration
   is not available to you.
   If you are reading this document in the context of the XDS Toolkit, it is better
   to close this test and read the introductory material in the toolkit for testing
   the Imaging Document Source.
   That introductory text includes configuration parameters and diagrams.
</p>
<table border="1">
  <tr><th>homeCommunityID</th><th>Imaging Doc Source Repository Unique ID</th></tr>
  <tr bgcolor="#FFA500"><td colspan="2"><center>Under Test:Initiating Imaging Gateway</center></td></tr>
  <tr bgcolor="#FFA500"><td>urn:oid:1.3.6.1.4.1.21367.13.70.1</td><td>&nbsp;</td></tr>

  <tr bgcolor="#FFFFFF"><td colspan="2"><center>Community A: Responding Imaging Gateway</center></td></tr>
  <tr bgcolor="#FFFFFF"><td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td>
  <td>1.3.6.1.4.1.21367.13.71.101 <br/>1.3.6.1.4.1.21367.13.71.101.1</td></tr>

  <tr bgcolor="#A0A0A0"><td colspan="2"><center>Community B: Responding Imaging Gateway</center></td></tr>
  <tr bgcolor="#A0A0A0"><td>urn:oid:1.3.6.1.4.1.21367.13.70.102</td><td>1.3.6.1.4.1.21367.13.71.102</td></tr>

  <tr bgcolor="#FFFFFF"><td colspan="2"><center>Community C: Responding Imaging Gateway</center></td></tr>
  <tr bgcolor="#FFFFFF"><td>urn:oid:1.3.6.1.4.1.21367.13.70.103</td><td>1.3.6.1.4.1.21367.13.71.103</td></tr>

  <tr bgcolor="#A0A0A0"><td colspan="2">
   <center>Unregistered Community Represents Error Conditions<br/>Do not configure these in your Initiating Imaging Gateway</center>
      "</td></tr>
  <tr bgcolor="#A0A0A0"><td>urn:oid:1.3.6.1.4.1.21367.13.70.102.999</td><td>1.3.6.1.4.1.21367.13.71.102.999</td></tr>
</table>

<p>After you have initialized the test environment, you should see the full set of configuration
   parameters needed to configure and test your system.
</p>
<p>Note that your Initiating Imaging Gateway only communicates with the Responding Imaging Gateway simulators.
   Your system will not connect directly to any of the Imaging Document Source simulators.
</p>
