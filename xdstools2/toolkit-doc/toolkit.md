XDS Toolkit
===========

Related pages
=============

Alternate names
===============

* Within IHE
** XD* Toolkit
** Bill's stuff
** Connectathon XDS Toolkit
* Within ONC
** NwHIN transport toolkit
** Transport Testing Toolkit (TTT) (Direct project)

Philosophy
==========

1. Single toolkit to support both pre-Connectathon and Connectathon testing of XD* and related profiles within IHE.
2. Be simple to download and install on your system/site
3. Provide a reference for testing
4. Run anywhere using the minimum supporting infrastructure

History
=======

The toolkit as it is today started as GUI wrapper for what was called xdstoolkit.  xdstoolkit was a command line tool encompassing the test engine (see a following section for definition). 
The main use for this tool was pre-Connecathon testing of XD* profiles. At the time we were also using this command line tool at Connectathons to help verify systems and in some cases 
test vendor systems when a profile testing partner could not be found. We found that using a command line tool during a busy Connectathon to be difficult given all the activity around us. 
Also, most monitors did not know how to use the command line xdstoolkit. The GUI version was created solely for Connecathon support purposes. That didn't last. 
The GUI format became my primary platform for tool creation.

The choice of technologies for the toolkit came from two separate points of view:
* Easy to deploy at vendor sites
* Easy/fast to develop

The primary technologes are:
* Java (inherited from command line tool)
* Apache Axis2 (also inherited)
* Servlet (Tomcat is really small, fast, and easy to install)
* Google Web Toolkit (for GUI development) - this was at the time the only platform around that let you develop in Java (including GUI stuff) 
and use the Java debugger in development then later generate JavaScript for final deployment.
* Ant for building from sources

Support
=======

I do not support the sources although they are available as open source on SourceForge project name IheOs. The reason for the no-source support is time.

Binary support is available through the Google Group ihe-xds-implementors@googlegroups.com. I devote as much time as I can to helping folks there.

The following binary formats are supported:
* WAR file to be run in a Servlet container.  The current preferred container is Tomcat 5.5
* Virtual Machine based on VMWare. This format also contains a copy of the Public Registry

Toolkit contents
================

The toolkit contents are layed out on the Home page, the first page to be displayed when you launch the toolkit. For the most part
each of these tools are independent, like a hammer and plyers in a little red toolbox.  When selected, each tool launches into a separate tab
so you can launch a bunch and move between them as you need.  Note, these tabs are supported within the toolkit.  They are not browser tabs.

The following areas of functionality are found in the toolkit:

__Test Engine__ 
> The toolkit acts as a client (in the client/server sense) to initiate a connection to a system under test (SUT)

> Examples of this are the queries and retrieves found in the first column of the home page

__Simulators__
> Simulators are implementations of IHE actors outfitted to provide some degree of diagnostic feedback. The usual question is why call them
simulators instead of implementations. To call them implementations would imply they implement the entire IHE actor.  They typically do not.
Instead they implement enough of the functionality of an IHE actor to support a fixed collection of tests. 
 
__Message Validators__
> Message validators accept an uploaded file and validate it against a selected format.  The output is a collection of log message detailing the
results of the validation.

__Test data__
> Each of these tools contains pre-built messages containing canned data that is useful in testing.

__General Utilities__
> which are found in the Tools column.
