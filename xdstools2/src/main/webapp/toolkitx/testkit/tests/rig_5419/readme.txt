Responding Imaging Gateway: Read This First
<h2>Responding Imaging Gateway: Read This First</h2>

<p>The System Under Test (SUT) is a Responding Imaging Gateway
</p>

<p>
The tables immediately below describe the environment at a high level with
values for homeCommunityID's and repositoryUniqueID's.
We use fixed values for homeCommunityID’s.
</p>

<table border="1">
  <tr><th>Community / System</th><th>homeCommunityID</th></tr>
  <tr bgcolor="#FFA500"><td>Under Test: Responding Imaging Gateway</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.201</td></tr>
  <tr bgcolor="#FFFFFF"><td>Initiating Imaging Gateway (Simulator)</td><td>urn:oid:1.3.6.1.4.1.21367.13.70.202</td></tr>
</table>

<br/>

<table border="1">
  <tr><th>Imaging Document Source (Simulator)</th><th>Repository Unique ID</th></tr>
  <tr><td>E</td><td>1.3.6.1.4.1.21367.13.71.201.1</td></tr>
  <tr><td>F</td><td>1.3.6.1.4.1.21367.13.71.201.2</td></tr>
  <tr><td>G</td><td>1.3.6.1.4.1.21367.13.71.201.3</td></tr>
  <tr><td>Unknown (do not configure in your Responding Imaging Gateway; <br/>used to test error conditions)</td>
    <td>1.3.6.1.4.1.21367.13.71.201.2.999</td></tr>
</table>

<p>After you have initialized the test environment, you should see the full set of configuration
    parameters needed to configure and test your system.
</p>

<p>Tests are run using three DICOM transfer syntaxes. The UIDs for these are:
 <ul><li>1.2.840.10008.1.2.1</li>
     <li>1.2.840.10008.1.2.4.50</li>
     <li>1.2.840.10008.1.2.4.70</li>
 </ul>
</p>

<p>In some cases, the Imaging Document Source simulator will respond to RAD-69 retrieve requests
   with images that are encoded with a requested transfer syntax.
   In other cases, the Imaging Document Source simulator will have the image
   but will not be able to supply it in the requested transfer syntax.
</p>

