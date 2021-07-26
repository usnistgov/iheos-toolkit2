---
title: Document Sharing Tools
---

# Document Sharing Tools
Bill Majurski (bmajur@gmail.com), Sunil Bhaskaria

## Important links

Toolkit at Connectathon is [here](http://172.16.0.72:8080/toolkit)

Download latest codes.xml from [here](https://github.com/usnistgov/iheos-toolkit2/releases/latest)

Download latest Toolkit from [here](https://github.com/usnistgov/iheos-toolkit2/releases/latest)

How to update your System configurations in Toolkit (pull from Gazelle and other options)
[here](https://github.com/usnistgov/iheos-toolkit2/wiki/Managing-System-Configurations-at-Connectathon) 

## News for Monday



## Big changes from last year

### Public Registry has retired
The Public Registry is now retired.  It is not on the network in The Hague and 
it will removed from the Internet around May 1.  XDS Toolkit is replacing it.

XDS Toolkit now has several modes of operation.
 
single user - the default when you download
and run it on your machine

multi-user - this will run on our Internet server. Each user's data is private.

Connectathon - this is multi-user with a default user 

CAS - (IHE Conformity Assessment) multi-user with restrictions placed on user creation.

At Connectathon the RED, GREEN, and BLUE copies of the Public Registry are replaced
with Toolkit simulators.

How does this change Connectathon?

The change to Registry/Repository simulators for RED/GREEN/BLUE should not much accept
that now the logs are viewed from the **New Simulator Logs** menu on the left side of 
Toolkit.

### More options for managing system configurations in Toolkit

There are more options for managing System definitions including user created private
configurations.  See 
[here](https://github.com/usnistgov/iheos-toolkit2/wiki/Managing-System-Configurations-at-Connectathon) 
for details.

<!--
### New tests and tools for MHD and FHIR in general.

The Conformance Tool now has tests for MHD Document Recipient/Responder

A new tool labeled **FHIR Search** in the menu can read/display any FHIR resource and
perform a limited set of queries on DocumentReferences.

A new tool labels **Submit Resource** can write a collection of pre-loaded FHIR resouces
to any FHIR server.  All of the Connectathon Patients are available here.
-->

### Inspector has new support for Metadata Update

With the display of a DocumentEntry, an update can be composed and sent back to
the same Registry.
