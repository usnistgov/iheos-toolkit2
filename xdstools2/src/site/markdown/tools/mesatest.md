# Conformance Test Tool

The Patient ID Manager and the Conformance Test tool are not yet integrated, mostly because of the lack of support
for a V3 Patient Identity Feed transaction.  Here is what you need to do to manage Patient IDs when running
the Registry actor tests.

From the perspective of Patient ID management there are two groups of tests, Stored Query and everything else. For
everything else you will need a single Patient ID that is registered with your Document Registry.  You can use
more than one, each test is independent.

The Stored Query tests are different.  You start with tests 12346 and 12374, which are listed under
*Initialize for Stored Query*. Each of these needs a clean Patient ID (no metadata registered against it). So
running these goes like this:

* Using new Patient ID
* Run 12346
* Using a different new Patient ID
* Run 12374
* Run all the Stored Query tests under the Registry heading (Select Actor Name)

During development I run these tests against the included Registry simulator.
