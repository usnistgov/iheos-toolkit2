# System Components #

The following are the primary components of the toolkit.

## WAR ##

Toolkit is downloaded as a single WAR file for installation under Tomcat.

## External Cache ##

The External Cache holds local user data. It is external to the WAR
installation so that updating the WAR file inside Tomcat does not disrupt
the data stored here. The major components are:

* actors/ - list of sites and their endpoints, OIDs
* environment/ - each environment contains an Affinity Domain configuration and a client
certificate for TLS for use with the test client.
* simdb/ - collection of simulator configurations and data
* testLogCache/ - logs for the Conformance Test Tool

## Toolkit Properties ##

A small collection of properties that link toolkit to the underlying
Tomcat installation an to the External Cache.  These properties are
housed in the file toolkit.properties which resides in the WEB-INF
directory created when the WAR file is expanded. This file will be updated
to reflect your local installation.

This file is usually edited from the toolkit Home window by following the link

    [Toolkit Configuration]

## Environment Definition(s) ##

An Environment defines your customization of the Document Sharing
community in which toolkit resides.  An instance of toolkit may contain
multiple environment definitions. They are selectable at runtime.

An environment is composed of two elements: codes definitions and TLS
certificates.  The codes definitions control what are the legal coding
schemes usable in Document Sharing metadata. They are used to validate
messages containing metadata.  The certificates are used when toolkit
is a client in a network conversation.  Here client indicates that toolkit
is initiating the connection/transaction.  An example is when toolkit
acts as an XDS Document Source sending a Provide and Register transaction
to a Document Repository.

When toolkit acts as a server the certificate to be used must be
configured directly into Tomcat.  In the most common configuration a
certificate is assigned to a port.

Environment definitions are stored in the External Cache.

## Site Definition(s) ##

A site defines a system that toolkit may interact with. The name system is
used in Gazelle.  A site contains:

* A name by which it is referenced
* A collection of endpoints, eached mapped to a transaction name
and a TLS setting (on/off)
* RepositoryUniqueId value for Document Repository actors
* HomeCommunityId values for Responding Gateways actors.

Site definitions reside in the External Cache.
