# Simulator Message Viewer

This tool shows the messages processed by the simulator.  The message list
on the left shows the messages by time stamp and type in reverse chronological order,
most resent first. This tool is oriented to show the operation of a simulator
that starts its work by receiving a message and responding: a server. The
request message is shown on the left and the response message on the right.  The bottom
is the collection of log messages produced by the simulator and the validators it
calls.

While there are both client and server simulators, based on how they start their
operation, each simulator of either type can be a composite simulator, that is two
or more simulators combined into one. A composite simulator shows in its
configuration the endpoints of all of its component simulators.  Also in these logs
it shows the messages processed by each component simulators.

An example is a composite Document Repository and Document Registry.  A Provide and
Register transaction sent to the Repository will result in a Register
transaction being sent to the Registry.  Both transactions will appear in the
logs.