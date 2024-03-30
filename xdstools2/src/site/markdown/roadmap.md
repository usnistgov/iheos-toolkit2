# Project Road Map #

## Recent 
For recent updates, please see https://github.com/usnistgov/iheos-toolkit2/wiki/Plans.

## Previous updates
As of December 2021

Apache Log4j server side logging framework was replaced with Java Util Logging. See https://github.com/usnistgov/iheos-toolkit2/wiki/XDS-Toolkit-Server-Logging-Configuration
The logging replacement is a back-end server change only, it does not modify any profile test related Toolkit features.

NIST XDS Toolkit users should report bugs and features requests through the XDS Toolkit GitHub Project Issues page in https://github.com/usnistgov/iheos-toolkit2/issues

DSUB, XPID, and Async features are put on hold until this can be discussed by the IHE Testing and Tools Committee.

General IHE XDS Profile questions should be posted to
XDS Implementers mailing list
ihe-xds-implementors@googlegroups.com

# Roadmap History
As of November 2015

## Short term ##

**Upgrade** to the Find Document Stored Query tool to include all defined query parameters.  This tool is in development
but not yet ready for distribution.

**Conformance Test tool** replacement that

* Shows current status of all tests for the given system/actor under test
* Display logs of any test in the collection
* Run all defined tests for a given system/actor from a single button click
* Delete one or all logs for a given system/actor

**More** bug fixes.

**Integrate** support for the Delete transaction into the Document Registry simulator. This tool is in
development but not yet ready for distribution.

**Additional** functionality for the Manage Patient IDs tool including: listing patient name for the patient id, tools
that use a patient id get upgraded to display a favorites drop-down selection box along with the type-in/copy/paste box,
V3 Patient ID Feed generation.

**Service interface**. A callable interface has been created to allow toolkit functionality to be integrated into other projects.
This is structured as a collection of 3 Java jar files and documentation for the callable API.  The current functionality
is focused on create/update/delete simulator configurations and XDR Provide and Register send and receive. If configured,
a Java/Servlet based application can receive notification that an XDR was received. The connection between the API
library and the running copy of toolkit is a collection of REST calls.  The API is documented but the REST calls
are internal to the implementation. Eventually all toolkit functionality will be available through this interface.
This set of interfaces is in alpha test at two sites and not yet being distributed beyond that. The next functionality
to be included is access to transaction validators and conformance test execution (equivalent to
Conformance/Pre-Connectathon Test tool).

**On-Demand Documents** - Test and simulator support.

## Longer Term ##

**DSUB** support. The receive notification work described above under the service interface is part of the
funcionality needed for DSUB.

**XPID** support.  The Patient ID management functions added in release 202 are a start towards other patient managment
functionlity including XPID.

**Patient ID update** via Metadata Update.

**Lots** of negative tests.

**Async support**.  I am considering re-introducing asynchronous transaction support.  It was present in toolkit back
when it was a command line tool.  Conflicts were introduced when toolkit was ported to a servlet container so it could
support a GUI interface.