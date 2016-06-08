# How to move simulators to a new host:port

When the Tomcat hosting toolkit is reconfigured to a different hostname or port number (TLS or non-TLS)
this causes a problem with the simulators.  Simulator endpoints are generated when the simulator is created using
the hostname, port, and TLS port configured into toolkit (toolkit.properties). If the Tomcat hosting toolkit is 
reconfigured all the simulator configurations must be updated.

This process has been automated.

Toolkit restarts when Tomcat restarts or you use the Tomcat Manager to reload the toolkit application. On restart
toolkit scans all simulators and compares their endpoints to the hostname, port, TLS port configurations in 
toolkit.properties. If they are different the simulator endpoints are updated.

There are two ways to update the hostname, port, and TLS port configurations in toolkit. Use the 
Toolkit Configuration link on the home page or manually edit WEB-APP/classes/toolkit.properties.
 