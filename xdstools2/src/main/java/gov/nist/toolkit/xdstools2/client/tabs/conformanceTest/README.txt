Notes on extending the Conformance Test tool

## Adding an option to an actor

Look in ActorOptionManager.java - the primary table lives here

## Testing environment text and widgets

This is coded in BuildXXTestOrchestrationButton.java which does this
display, triggers initialization on the server, and then updates the display
with details of the orchestration.

## How to code tests that the SUT initiates

This is done in the section.  The beginning of the testplan can include the instruction
<SUTInitiates/>  (child of <TestPlan />). This applies when only a single test
is marked for SUT initiation.

When a collection of tests is to be marked for SUT initiation you must edit this into
the ACTOR_OPTIONS list in BuildXXXTestOrchestrationButton.java. The list is
referenced in ActorOptionManager.java.

This list has one entry for each option of the actor.  Each entry is a call
to the constructor for ActorAndOption.java.  The last parameter, externalStart,
triggers the behavior.

## Choosing step names

If you declare a duplicate step name (different section, same step name) within a test
then the results of the second use of the name will not display in the Inspector.


