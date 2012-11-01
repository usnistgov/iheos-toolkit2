Scripts for managing the port 25 listener until the real inetd can be written

After ant build, listener.jar is copied to ~bill on hit-testing.  It is assumed that there is a copy
of ttt.war in ~ as well.  The script prep-listener.sh is run (with WD set to ~/bin).  This creates 
the directory ~/bin/listener from the jars in ttt.war and the classes and config files from listener.jar.
This assumes that listener.jar is updated more often and why copy the big jar collection for every update.

To start the listener using screen(1), run start-screen.sh (WD set to ~/bin).  Later to stop listener, use
screen -ls to get a listing of screen managed jobs and screen -d -r <job> to cancel the job.  After
cancelation, a new copy can be started using start-screen.sh above.
