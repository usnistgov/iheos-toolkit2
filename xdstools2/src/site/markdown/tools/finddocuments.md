# Find Documents Tool

This tool initiates the Find Documents Stored Query to the Document Registry, Initiating Gateway,
or Responding Gateway of your choice.

After running the query, use the *Inspect Results* button to browse the query
response.

Note that when you change the TLS setting that the selectable sites change depending on which
sites offer TLS/non-TLS endpoints.

The sites listed as potential targets for this transaction depend on the sites configured in the tool:


**Document Registry** -
Document Registries are listed that have an endpoint for the Stored Query transaction. With this selection the
tool plays the role of a Document Consumer. The Stored Query
generated will not carry the homeCommunityId since this is a Find- type query. In this query, the tool
acts as a Document Consumer sending to a Document Registry.

**Initiating Gateway** -
Initiating Gateways are listed that have an Initiating Gateway Query transaction configured (a term
used in this tool that is not present in the Technical Framework). With this selection the tool
plays the role of a Document Consumer. The Stored Query
generated will not carry the homeCommunityId since this is a Find- type query. In this query, the tool
acts as a Document Consumer sending to an Initiating Gateway.

**Responding Gateway** -
Responding Gateways are listed that have a Cross Gateway Query transaction configured. With this selection the
tool plays the role of an Initiating Gateway. The query
generated will not carry the homeCommunityId since this is a Find- type query. In this query, the tool
acts as an Initiating Gateway sending to a Responding Gateway.

