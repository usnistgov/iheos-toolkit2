#!/bin/sh
# startup script for TTT Listener
#
# description: start the squeak image

# processname: 
#
#

bin=/home/bill/bin/listener

case "$1" in
     start)
         (
             echo Starting Listener
	     rm nohup.out
	     echo Last restart at `date` > $bin/listener_last_restart.txt
             nohup $bin/direct-listener.sh &
	     cat $bin/listener_last_restart.txt
	     cat nohup.out
         )
         ;;
     stop)
         echo Killing Listener
	 [ -e $bin/direct-listener.pid ] || exit 0
         kill -9 `cat $bin/direct-listener.pid`
	 rm -f $bin/direct-listener.pid
         echo
         echo
         ;;
     restart)
         $0 stop;$0 start
         ;;
     *)
         echo "Usage: $0 {start|stop|restart}"
         exit 1
esac
exit 0
