# Common Toolkit terms

**test session** - similar to a user account.  Instead of identifying a user it identifies
a project.  Different work is done in different test sessions to keep the test results
and simulators separate.  Simulators are always owned by a test session.

**simulator** - a testing oriented implementation of an IHE actor built into toolkit.
Sometimes a simulator implements more than one actor when it is convenient. Most
simulators represent servers, that is a piece of software that starts its operation by
receiving a transaction.  A Document Registry is an example of a simulator.

**test client** - a testing oriented implementation of an IHE actor that acts as a client,
initiating a transaction. A Document Source is an example of a simulator. Tools that
represent the test client initiate transactions like Provide and Register, Stored Query,
and Retrieve.

**site** - describes the configuration of a system in terms of the transactions
it accepts and its ids like repositoryUniqueId and homeCommunityId.  Sites are created
manually for real software systems using the *Site/Actor Configuration* tool. These
configurations are permanent - they exist until you delete them.  A site can contain
any number of IHE actors but can only contain one of each type.  So a site can hold
a Document Registry and a Document Repository but cannot hold two Document Repositories.
For that you need a second site.  The concept of a site is similar to what Gazelle
calls a system.