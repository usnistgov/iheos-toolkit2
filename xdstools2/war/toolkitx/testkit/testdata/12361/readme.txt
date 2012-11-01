Load test data for FindDocumentsForMultiplePatients 

This test data must be loaded in your Registry Actor exactly once!
This may require the ability to delete old data (previous copy) if
you need to reload this data.  Tests based on this data are expected
to report exact number of instances of the data present here. 
In other tests we rely on different Patient IDs to separate multiple
loads of test data.  But since the MPQ allows queries unrestricted
by Patient ID we cannot simply use a new Patient ID!

This test data set consists of two submissions each with a unique
Patient ID.

multi_doc defines three DocumentEntry objects with codes:
Document01) MPQ-classcode-1, MPQ-eventcode-1, MPQ-hcftcode-1
Document02) MPQ-classcode-1, MPQ-eventcode-2, MPQ-hcftcode-2
Document03) MPQ-classcode-1, MPQ-eventcode-2, MPQ-hcftcode-2

single_doc defines one DocumentEntry objects with codes
MPQ-classcode-1, MPQ-eventcode-1, MPQ-hcftcode-1

To submit this using the GUI interface, select a section
(multi_doc or single_doc), enter a new Patient ID and
submit.  Then repeat other section with another new 
Patient ID.