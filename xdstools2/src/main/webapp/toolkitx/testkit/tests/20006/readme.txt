Multi-Op: Document Metadata Update and Submit Association (Beta)

This test the ability of the Document Registry to detect a condition when a request includes both a Metadata Update operation using Association Propagation to update a DocumentEntry, and a Submit Association that adds a Relationship Association between the updated version of the  DocumentEntry object and another existing Document Entry object.

The Document Administrator shall not request Association Propagation when performing a "manual" operation within the same transaction. When this condition is detected by the Document Registry, the transaction shall fail and an XDSMetadataUpdateOperationError error returned to the Document Administrator.

Beta Test (CP-ITI-1029) – Current text is silent on this subject.

Use case based on XDS Metadata Update: Section 3.57.4.1.3.1.1 - Rules for Update Planning.
