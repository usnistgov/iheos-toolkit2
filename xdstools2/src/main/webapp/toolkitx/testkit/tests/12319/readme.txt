RMU, XDS Persistence Option, of DE with deprecated status

In an XDS setting, update a deprecated DocumentEntry that represents the latest version.
Then attempt to update a previous, not latest, version which must fail.

The first four sections submit a DocumentEntry, verify the submission by reading it back,
use MU to
mark it Deprecated and verify the Deprecated status. These steps are performed against
the Document Registry.

The section rmu_update_confCode issues an RMU against the Document Registry exercising the
XDS Persistence Option defined by the RMU supplement. The update changes a
DocumentEntry Confidentiality Code.
Although the DocumentEntry is Deprecated, it is the most recent version so this
should work. This
tests compliance with CP 1190-02. This update is made against version 1 of the
DocumentEntry. The
result is the creation of version 2.

The next section verifies the updated Confidentiality Code

The section rmu_update_typeCode attempts to update the TypeCode of the version
1 DocumentEntry
which must fail since the version 1 is not the most recent version.




