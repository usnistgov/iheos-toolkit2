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
Tomcat installation to its environment including the External Cache.  These properties are
housed in the file toolkit.properties which resides in the WEB-INF
directory created when the WAR file is expanded. This file will be updated
to reflect your local installation.

This file is usually edited from the toolkit Home window by following the link

    [Toolkit Configuration]

The toolkit properties are

*Use_Actors_File* - true/false - if true then that site definitions are kept in the External Cache file
actors.xml.  If false they are kept in the External Cache directory named actors and each site definition
is kept in its on file named for the site. Support for the actors file will be removed in the future
and only the directory form will be supported

*Default_Environment* - name of the default environment.  An environment named *default* is always present.  It
is automatically installed by toolkit (as of version 202.0). If the environment name indicated in *Default_Environment*
does not exist in the configuration (External Cache directory named environment) then the *default* environment
will be automatically selected and used.

*Admin_password* - toolkit is delivered with this set to "easy".  It can be changed after installation.  The password
is used to control the editing (from the UI) of the toolkit.properties file (using the tool *Toolkit Configuration*
and the actors definitions using the tool *Site/Actor Configuration*.

*Enable_all_ciphers* is obsolete and its value of false should not be changed.

*Toolkit_Host* and *Toolkit_Port* and *Toolkit_TLS_Port* tell the toolkit software the Tomcat (or equivalent) configuration
where toolkit is running.  These are needed when creating endpoints for simulators.

*Listener_Port_Range* - the range of port numbers that can be allocated to Document Registry simulators for their
V2 Patient Identity Feed listener. Each Document Registry simulator is assigned a port from this range so the range needs
to be large enough to accomodate your usage.  The format is lowvalue, highvalue.

*External_Cache* - absolute path of the External Cache directory.  This directory must be writable. Toolkit uses
this directory to hold localized data such as configurations. When you hit the Save button on the Toolkit Configuration
Editor it will validate that this directory exists and is writable.

*Gazelle_Config_URL* is the URL that Gazelle is contacted on to request updated site configurations (Gazelle calls them
systems).  This is only used at Connectathons where Gazelle is present and is not intended for user manipulation.

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

Site definitions reside in the External Cache in the directory actors.
