Multiple Responding Gateways
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html;
      charset=windows-1252">
    <title>Initiating Imaging Gateway: Multiple Responding Gateways</title>
  </head>
  <body>
    <h2>Multiple Responding Gateways</h2>
    <p>Tests the ability of the Initiating Imaging Gateway actor (SUT)
      to respond correctly to a Retrieve Image Document Set (RAD-69)
      Request from an Image Document Consumer actor (Simulator), for a
      DICOM image files from two Responding Imaging Gateways.</p>
    <p>One study is located in Community A, and the second study is
      located in Community B. The Initiating Imaging Gateway is expected
      to submit retrieve requests to both communities and provide a
      consolidated result. </p>
    <h3>Retrieve Parameters</h3>
    <table border="1">
      <tbody>
        <tr>
          <td>RIG Home Community ID (A)</td>
          <td>urn:oid:1.3.6.1.4.1.21367.13.70.101</td>
        </tr>
        <tr>
          <td>IDS Repository Unique ID (A1)</td>
          <td>1.3.6.1.4.1.21367.13.71.101</td>
        </tr>
        <tr>
          <td>RIG Home Community ID (B)</td>
          <td>urn:oid:1.3.6.1.4.1.21367.13.70.102</td>
        </tr>
        <tr>
          <td>IDS Repository Unique ID (B1)</td>
          <td>1.3.6.1.4.1.21367.13.71.102</td>
        </tr>
        <tr>
          <td>Transfer Syntax UID</td>
          <td>1.2.840.10008.1.2.1</td>
        </tr>
      </tbody>
    </table>
    <p><br>
    </p>
    <h3>Test Execution</h3>
    <p>The test consists of four steps: </p>
    <ol>
      <li>Test software sends RAD-69 request to System Under test and
        records response.</li>
      <ul>
        <li>System Under Test sends a RAD-75 request <b>to all
            Responding
            Imaging Gateways</b> which store the requests.</li>
        <li><i>Responding Imaging Gateways provide RAD-75 responses</i>
          to
          System Under Test.</li>
        <li>System Under Test <b>provides one RAD-69 response </b>to
          test software.<br>
        </li>
      </ul>
      <li>Test software validates the RAD-75 requests that are sent by
        the
        System Under Test.</li>
      <li>Test software validates the RAD-69 response sent by the System
        Under Test.</li>
      <li>Test software validates the image returned in the RAD-69
        response to make sure the System Under Test did not alter the
        image.<br>
      </li>
    </ol>
  </body>
</html>
