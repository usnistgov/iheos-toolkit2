# PnR.b Accept document

## Overview

Send a Provide and Register transaction to the Repository under test containing a single document.
The Repository is expected to forward the metadata to a Registry simulator.

Initialization instructions for the Repository under test are at the top of the window.
The forwarding of the metadata will be validated by querying the Registry simulator.

This test does not Retrieve the document.  This tests the submission only.