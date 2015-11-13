# Simulator Manager

A simulator is a lightweight implementation of an IHE Actor intended to support testing. The simulators that
can be created in this tool all represent servers, they begin operation when a transaction is received.  There
are client simulators but they are not managed through this tool.

There are two parts to a simulator ID, the *testSession* and the raw ID.  The *testSession*, which could have been
called *user*, is created/chosen at the top of the tool window.  Different testSessions (users) are useful to help keep
my simulators from being confused with your simulators.  There is not authentication involved, just labeling.
A simulator ID is made up of the testSession and the raw ID. The raw ID is entered in the tool and is called
*Simulator ID* in the window.  For a testSession of bill and id of reg the resulting simulator id is

    bill__reg

which you will see after creating the simulator. You will notice that the simulator id does not indicate the type of
simulator.  I chose the raw id of reg for my convenience not because it is a shorthand for registry.

## Creating a Simulator ##

1. Select an Environment.  For now this is only used by actors that are sensitive to the Affinity Domain coding
policies, which is only the Document Registry actor.
2. Select a TestSession name.  This will be part of the Simulator ID
3. Select an Actor Type
4. Enter a Simulator ID.
5. Press Create Actor Simulator

The simulator configuration will be shown in the table below.  Note that not all columns apply to all simulator types.
The content statistics (such as number of DocumentEntries - which only applies to Registry) are only update when you
press the [reload] button at the top of the tool window.

*Transaction Log* - displays the transaction log for the actor if it has one

*Patient ID Feed* - displays the Patient IDs that have been received by the actor (Registry actor only).

*Configure* - display/edit the simulator configuration

*Delete* - delete the simulator including logs and configurations

*Download Site File* - a site file is an XML encoding of the transaction endpoints and OIDs associated with the
simulator.  This format has been used with toolkit for a very long time.  It can be used to import the configuration into
your tooling.

## Lifetime of a Simulator ##

Simulators do not expire.  As long as the supporting data is present the simulator exists. You can view the simulators
you have by selecting a test session in this window. All simulators owned by this test session will be displayed.

Stopping/starting the toolkit does not affect simulators.  If toolkit is running so are all of its simulators.

## Registry simulator and the Patient Identity Feed

The Document Registry simulator now includes a V2 Patient Identity Feed listener.  A new toolkit property managed
in Toolkit Configuration, named Listener Port Range, tells toolkit what range of ports it can manage. The format
of this field is first_port, last_port.

When a Document Registry simulator is created a port is allocated from this range for the V2 Patient
Identity Feed listener. The Registry simulator now validates Patient IDs received in Register transactions against this
feed.  This validation step can be disabled by updating the simulator configuration in the Simulator Manager tool.

## Document Recipient simulator

The Document Recipient simulator is a combination of the Registry and Repository simulators.  By default
validation against the Patient Identity Feed is disabled.  Validation against the Affinity Domain configuration is
also disabled by default.  These can be changed by updating the simulator configuration in the Simulator
Manager tool.
