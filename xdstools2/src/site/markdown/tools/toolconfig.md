# Toolkit Configuration tool

This tool is used to configure the toolkit.  The individual settings are:


### Use Actors File

Within the External Cache is either a directory named actors or a file named actors.xml.  Actually both may
exist but only one is used at a time.  The setting of this parameter chooses which is used.

When this parameter is *true* the site configurations are taken from actors.xml.  When *false* they are taken from
the actors directory.  Within the directory each site is contained in a file sitename.xml.  The
*Site/Actor Configuration* can edit either format.

The single file format (setting of true) has been deprecated and will be removed in a future release.

### Default Environment

An environment contains the Affinity Domain codes and client TLS certificates.  Toolkit can be configured with
many environments, each a directory under the External_Cache/environment directory.  This parameter controls which
is the default environment, the one to be selected when toolkit is launched in browser window.

When toolkit starts up it checks the External Cache and installs a default environment named *default*.

### Admin Password

This password is used to edit toolkit configuration and to save from the Site/Actor Configuration tool.  As shipped
the default value is "easy" witout the quotes.

### Enable all ciphers

Do not change.  This field is deprecated and will be removed.

### Toolkit Host, Toolkit Port, Toolkit TLS Port

These tell toolkit the web address of where toolkit resides.  They are necessary to create endpoints for simulators.
If you use *localhost* as the hostname and have users on a different machine that try to access your simulators you will
have obvious problems.

### Listener Port Range

The Document Registry Simulator (and Document Recipient if so configured) accept a V2 Patient Identity Feed
transaction.  The listeners are automatically generated when the simulators are created.  This setting tells
toolkit the range of ports that may be allocated to these listeners.

The format is lowest_port_number, highest_lowest_port_number

### Gazelle Config URL

This is the URL for contacting Gazelle to automatically download system configurations.  It is only used at
Connectathons.