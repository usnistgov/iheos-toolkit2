#!/bin/sh

# run from the ~/bin directory 
# a newly expanded listener.jar should be be present 
# which should be built via ant listener

tttwebinf="/home/bill/ttt/webapps/ttt-test/WEB-INF"

rm -r -f listener

mkdir listener

# listener.jar assumed in the home directory (this lives in ~/bin)
(cd listener; jar xf ../../listener.jar)

(cd listener; cp $tttwebinf/lib/*.jar lib)


