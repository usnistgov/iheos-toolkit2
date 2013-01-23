Scripts/directions for updating the TTT service on hit-testing
==============================================================

1) All operations on this service must be done using the ttt account.

2) TTT runs in a separate tomcat, called tomcat_ttt, on hit-testing.  This tomcat is assigned
port 9100. This tomcat is owned by account ttt.

3) Starting/stopping ttt tomcat shall only be done using these procedures:

   START:     sudo service tomcat_ttt start
   STOP:      sudo service tomcat_ttt stop
   RESTART:   sudo service tomcat_ttt restart
   STATUS:    sudo service tomcat_ttt status
   
Using the low-level tomcat shell scripts will break things. These scripts do not require root
access to use, just membership in group ttt_users, which should include all of us.

4) The listener for TTT (listens for incoming SMTP traffic) relies on the ttt.war servlet
being installed and running in tomcat.  It runs from the same copy of the files.
If ttt.war is updated then the listener must be restarted.  See the section below on managing 
the listener.

5) TTT relies on an external cache of information that does not change when a new version
of the software is installed.  This cache is a directory owned by account ttt. This cache
is located at /var/lib/tomcat_ttt/ttt/external_cache.

6) Part of the configuration of TTT after a new version of ttt.war is installed is checking 
that the ttt.war files point to the external_cache.  This is done by launching TTT in a
web browser:
	http://hit-testing.nist.gov:9100/ttt
At the top of the main/home page is a link that looks like 
	[Toolkit Configuration]
Opening this page requires the TTT toolkit administrative password.  Once open, the property
named External_Cache should be checked.  It must hold the value shown above in item #5.
Once this has been checked/updated, reload the tool in the browser (normal page reload) and
everything should operate normally.


INSTALLING A NEW COPY OF TTT.WAR ON HIT-TESTING
===============================================

1) A new copy of ttt.war is generated from sources.

2) Copy ttt.war to your account on hit-testing.  I use the Unix command scp. I have been told
that Windows users can use Putty.

3) If this is a new versioned instance of TTT then there are separate procedures for archiving 
the WAR file.  They are not covered here. Note that every versioned release of TTT is archived.

4) The rest of this procedure must be done from the ttt account.  So, now, log in to ttt:
	su ttt
This will require the account password for ttt.

5) The new ttt.war file has been uploaded and sits in one of your directories.  Cd to 
that directory (remember, you are logged in as ttt) and run:
	install-ttt.sh
This command is available on the default path for user ttt only.  It is forbidden to run 
from any other account. This will copy your new ttt.war to the webapps directory of ttt_tomcat
overwriting the current file.  Then it will execute a proper restart of the TTT tomcat.
Only on restart will the new WAR file be recognized. 

6) Once ttt.war is installed you must restart the listener.  This is documented in the
next section.

Managing the listener
=====================

1) All operations on this service must be done using the ttt account.

2) There are no files to upload for the listener. Listener depends on ttt.war being installed
and simply references files in the exploded war structure.

3) This listener listens on port 12999 (an unrestricted port).  Incoming
connections to the normal SMTP port, port 25, are redirected to 12999 by a 
firewall rule running on this server (hit-testing).

4) The scripts to operate the listener are located on hit-testing at:
	/var/lib/tomcat_ttt/ttt
Note that /var/lib/tomcat_ttt is the home directory for user ttt

5) Under this directory are three sub-directories:
	external_cache - the External Cache for the toolkit
	bin - start/stop scripts for the listener
	logs - log files.
	
6) bin contents:
	install-ttt.sh - used to install a new version of TTT.  
	listener.sh - the only script to be used to start/stop the listener.  It must be run 
	from the ttt account (it checks and exists otherwise).  The commands are:
	listener.sh start
	listener.sh stop
	listener.sh restart
	listener.sh status
The individual meanings should be obvious.

This bin is in the default path for user ttt so no directory prefix is needed to use its scripts.

