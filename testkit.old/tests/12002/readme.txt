R.b Reject Add Document to Folder - Patient ID does not match

This test requires special handling. Each of the sections must be run
separately using the Select Section: control below. You will need two
valid Patient IDs for your Document Registry. Then . . .

1) Run section "create_folder" using your first Patient ID.

2) Run section "add_to_folder" using your second Patient ID. The transaction will
returned an error indicating a non-matching
Patient ID. This is an expected error.  

3) Run section "eval" using any Patient ID (doesn't use it).
This should succeed at detecting that the RPLC failed.

create_folder - creates a folder to work in

add_to_folder - attempt add document to folder with different Patient ID

eval - verify registry contents


