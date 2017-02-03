

Step validate_original - verify the original DocumentEntry (had amendment added to it) from submit/submit has status Approved (apnd/apnd (adding amendment) didn't change the status)
	
Step validate_apnd - verify appended DocumentEntry from apnd/apnd is present and has status Approved
	
Step no_validate_rplc_apnd - verify metadata from apnd/apnd_rplc was not stored (cannot add amendment to deprecated DocumentEntry)

