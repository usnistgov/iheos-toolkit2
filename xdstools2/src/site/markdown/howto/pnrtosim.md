# Send a Provide and Register to a Simulator

**Goal** - send a Provide and Register transaction to a simulator and follow up with a 
Retrieve transaction.

The following concepts and tools are used.

**test session** - similar to a user account.  Instead of identifying a user it identifies
a project.  Different work is done in different test sessions to keep the test results
and simulators separate.  Simulators are always owned by a test session.

**simulator** - a testing oriented implementation of an IHE actor built into toolkit.
Sometimes a simulator implements more than one actor when it is convenient. Most
simulators represent servers, that is a piece of software that starts its operation by
receiving a transaction.  A Document Registry is an example of a simulator.

**test client** - a testing oriented implementation of an IHE actor that acts as a client,
initiating a transaction. A Document Source is an example of a simulator. Tools that
represent the test client initiate transactions like Provide and Register, Stored Query,
and Retrieve.

**site** - describes the configuration of a system in terms of the transactions
it accepts and its ids like repositoryUniqueId and homeCommunityId.  Sites are created 
manually for real software systems using the *Site/Actor Configuration* tool. These
configurations are permanent - they exist until you delete them.  A site can contain
any number of IHE actors but can only contain one of each type.  So a site can hold 
a Document Registry and a Document Repository but cannot hold two Document Repositories.
For that you need a second site.  The concept of a site is similar to what Gazelle
calls a system.

In this scenario toolkit will act as Document Source, Document Repository, Document 
Registry, and Document 
Consumer.  Typically you will want your own software to perform as
one or more of these actors.  This demonstrates how to do all these functions with
toolkit as a starting point.

Start by building the simulators. A Document Registry and Document Repository will be needed.
Because they are used together so often, these two actors are implemented as a single
simulator.

Open the *Simulator Manager* (bottom of the left tools column)

Create a test session at the top of the screen.  Enter a short name like bill and press add.
This test session will own the simulators you create.  Most tools in toolkit have the 
test session selector and will only display simulators that belong to that test session.

Within the *Simulator Manager*  select the Actor Type -> Document Repository/Registry 
(a simulator that includes a Document Registry and a Document Repository, 
Repository forwards to Registry)

Enter Simulator ID  (something short and lower case) like rr (for Registry/Repository).

Press *Create Actor Simulator*.

The screen will update showing the simulator you have created.  The simulator is now 
running.

Now you have to decide whether for your test you want to require the Patient Identity 
Feed to the Registry as required by profile.  If you do the simulator has already 
created a port 
to accept V2 Patient Identity Feed messages.  (V3 Patient Identity Feed not implemented 
yet).  The V2 port is displayed.  If you want to bypass Patient Id feed validation then 
edit the simulator's configuration by pressing Configure button on the simulator.  
The 8th item on the list is a 
check box that controls this validation.  Click off and hit save (upper right). The other 
valuable part of this screen is the display of the generated SOAP endpoints.  These can 
be dowloaded in a format that toolkit used via the *Download Site File* link on the 
*Simulator Manager* tool.

Back on the Simulator Manager tool, anytime you don't see what you expect hit the 
[reload] button at the top to force a refresh of the screen.  This works on all the tools.

Next you can manually send a v2 feed by launching the tool labeled *Manage Patient IDs*. 
Here you can create a new Patient ID and send it in a V2 feed to any configured system.

The Patient ID Assigning Authorities configured in the toolkit are listed on the right 
side. Use a *Generate Patient ID* button to create a new Patient ID.  It will be displayed
in the *Favorite Patient IDs* list.  One you have created a Patient ID select it in this
list. Note that if you have a particular Patient ID you wish to use you can copy/paste
it into the *Add existing Patient ID* box in the top right and click *Add to Favorites*.

Once you have a Patient ID added to and selected in the Favorites box you can select a 
Document Registry at the bottom of the screen and send a V2 Patient Identity Feed message.
This requires that the site configurations have the hostname and port configured. See
the *Site/Actor Configuration* tool to add this if necessary. It is created automatically
for simulators.

Note that if you want to copy/paste a Patient ID from the Favorites list, select the ID and
it will appear in the far right portion of the display where it can be swiped and copied using
keyboard or mouse commands.

To send the Provide and Register, open the tool named *XDS Provide & Register*. 
Select a Data Set (submission).  *SingleDocument* is always a good place to start. This
is a SubmissionSet with a single DocumentEntry.
Insert the Patient ID of choice.  TLS probably isn't configured yet so leave that checked 
off.  Select the Document Repository to send to (if the simulator you just created is not 
listed, hit [reload].) Still not there - check the test session is the one you added
the simulator to. Hit Run.  A log of the operation will be appended to the bottom of the
window. Any problems result in lots of red print on the screen.  
Not too subtle.

You now a have a choice of what to do next.  You can inspect the transaction you just
sent by clicking the *Inspect Results* button.  Many Stored Queries are listed in 
the tool tray on the left side.  You can proceed there. Going back to the *Simulator
Manager* you can view the logs of this transaction from the point of view of 
the Registry/Repository simulator.

First lets open the *Inspector* by clicking the *Inspect Results* button. The left
side shows a history of transactions. The Provide and Register transaction you just
generated should be the only thing listed. You can click on any of the included
objects to see their content.  Remember you are viewing the submission which has
different characteristics than a query response. Once an object is displayed in the 
center panel, selecting a displayed attribute will show its XML representation on the
right.

The *load logs* link on the left will load the detailed logs into the display.  Here
you can review the SOAP messages exchanged.  The *assertions* link displays the 
log trace that was displayed back when you hit *Run* to initiated the transaction. 

Under the *Document Entry* element is a menu of secondary actions you can perform. 
Remember that you are viewing the message as it was sent.  To view the object
as seen through the Stored Query point of view, open a tool representing the Stored Query
of choice.

To view the relevant content in the Repository and Registry, go to the tool labeled
*FindDocuments*.  Your Patient ID should already be there or paste it if it is not.  
Select the Registry and hit *Run*.  Open the *Inspector* and you can review the 
results of the Stored Query.  Open the *Document Entry* and select *Action: Retrieve*
to send a Retrieve transaction to the Repository.  This will display a listing labeled
Document [external] which is a link.  At this point the Retrieve is complete and the
document is stored in a cache inside toolkit.  Click on the link and the document
will be displayed in a new browser tab.




