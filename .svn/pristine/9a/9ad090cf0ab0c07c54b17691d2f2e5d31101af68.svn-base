Transport Test Tool (T3)
===================

Frequently Asked Questions
--------------------------


Q: [Is the Transport Test Tool (T3) available for use online (in the cloud)?](#available_online)   
Q: [Where can I ask questions about or get help with T3?](#google_group)   
Q: [What tools can I use to generate certs for use with Direct?](#cert_generation)   
Q: [How do I use the FindDocuments tab to send a FindDocuments query to my Responding Gateway?](#finddocs_config)   

* * *
* * *
### <a id="available_online"></a> Q: Is the Transport Test Tool (T3) available for use online (in the cloud)? ###

A: Yes, NIST maintains a web version at <http://hit-testing.nist.gov:9100/ttt>.  Because this is a public copy
there are a few restrictions.  For example, new SOAP endpoints cannot be added.  To have this update/configuration 
capability you need to run your own copy where you will have update rights.

* * *
### <a id="google_group"></a> Q: Where can I ask questions about or get help with T3? ###

A: Two Google Groups have been established for this purpose. <transport-testing-tool-atl@googlegroups.com> supports 
use of these tools for the test labs and <transport-testing-tool@googlegroups.com> supports 
more generic issues and users.  ATL's are 
asked to use the general group when the topic is not focused on certification. Membership in these groups
requires an invite and permission from the group manager in order to keep spam off the list. More specifically   
    Anybody can view group content (the atl list is restricted to view by members only)   
    Only members can view group members list   
    People can request an invitation to join   
    Only members can post   

* * *
### <a id="finddocs_config"></a> Q: How do I use the FindDocuments tab to send a FindDocuments query to my Responding Gateway? ###

A: This is a two step process, first the configuration for the Responding Gateway must be added to the tool.
Then the FindDocuments tab can be used to send a FindDocuments Stored Query.

To edit the configurations, you must have edit permissions on the toolkit.  If you are using the copy hosted at NIST
on hit-testing.nist.gov then only NIST employees can do this update.  If you have the VMWare version of the toolkit
installed at your site then you have the necessary permissions.

On the home page in the Tools column is a menu entry for Site/Actor Configuration.  Open this tool by clicking on 
this link.  The page that opens will be labeled Configure Sites. On the left side you can create a new entry by
clicking on the plus-sign-icon at the bottom.  This brings up the Site edit page on the right.  

First, a unique site name must be entered at the top. Your new config cannot be saved without it. Next, under the
Responding Gateway label near the bottom, enter the homeCommunitId and the query SOAP endpoint(s). 
Remember that a homeCommunityId has the prefix urn:oid: followed by an OID.

Save the new site definition and open the FindDocuments tab.

The FindDocuments tab lists sites under 3 categories: Document Registry, Initiating Gateway, and
Responding Gateway.  The first two are defined by the IHE XDS.b profile and are not used in NwHIN.  
Responding Gateway is defined by the IHE XCA Profile and is the one used by NwWIN.  Choose a 
Responding Gateway, enable TLS and SAML as meets your testing needs and enter a Patient ID.  The Find Documents
Stored Query offers over a dozen parameters but this tool enables the use of just one, Patient ID.  Push
the Run button.  When the results are returned, the Inspect Results button will be enabled to allow you to
view the request and response messages (and a lot more).  

Note, if execution of this tool return successful status (as displayed at the bottom) this means the Responding Gateway
returened a proper response.  This response can be empty (no metadata) and still be proper/valid.

* * *

### <a id="cert_generation"></a> Q: What tools can I use to generate certs for use with Direct? ###

A: The only tool we know of that can generate certs meeting the requirements of Direct is the cert tool
that is included as part of the Direct Reference Implementation. The Java keytool is reported to be inadequate
and we cannot find any documentation to guide us on the use of openssl. The only relevant online documentation
we have found is:

<http://www.mail-archive.com/openssl-users@openssl.org/msg19407.html>
<br /> and <br />
<http://wiki.directproject.org/Misc+Certificate+Notes>

* * *
