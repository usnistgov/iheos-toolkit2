# Toolkit configuration guide

![Toolkit Configuration could not be displayed](../images/config.png)

### External Cache

Location of the toolkit external cache. The external cache is where toolkit keeps your local data.
It is maintained separate from the toolkit WAR so that if you update the toolkit your data can remain.

Rules for creating the external cache:

* A directory writable by the user account that runs tomcat.  This could be the account tomcat or your personal 
account depending on your installation.
* The directory path must contain no white space (blank characters for instance)
As an example of what will not work, /home/bill/temp dir/ec.  This will not work because of the
 *temp dir* directory name with the embeded space character.
* This directory should start empty.  Toolkit will initialize it.

For more detail on the External Cache see [here](../facilities/external_cache.html)

### Toolkit Host

This is the hostname of the machine running toolkit. This must be configured so that endpoints can be 
created for simulators. If you use the value *localhost* then the simulator endpoints will only be usable
by software running on this machine.

### Toolkit Port

This is the port number for Tomcat. This gets configured into simulator endpoints.

### Tomcat TLS Port

This is the TLS port number for Tomcat. This gets configured into simulator endpoints. For
TLS to work with simulators you must install the appropriate certificates in Tomcat.

### Use Actors File

This should always be set to false.

### Default Environment

This is initialized to default.  The default environment is automatically installed in the external cache
when toolkit is first launched.

### Admin Password

The password needed to configure toolkit.

### Enable all ciphers

This must aloways be false

### Listener Port Range

The range of port numbers that can be managed by toolkit for accepting Patient Identity Feed messages.  
One port is allocated to each Document Registry simulator. If you run out of ports then attempts to create
new Document Registry simulators will fail.

The format is low_port, high_port.  

### Gazelle Config URL

This is the URL to contact Gazelle to download system configurations.  It is only used at the North American
and European Connectathons.  

